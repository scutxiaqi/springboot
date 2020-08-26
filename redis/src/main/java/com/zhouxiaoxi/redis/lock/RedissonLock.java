package com.zhouxiaoxi.redis.lock;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.redisson.RedissonLockEntry;
import org.redisson.RedissonObject;
import org.redisson.api.RFuture;
import org.redisson.client.RedisException;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.LongCodec;
import org.redisson.client.protocol.RedisCommand;
import org.redisson.client.protocol.RedisCommands;
import org.redisson.client.protocol.RedisStrictCommand;
import org.redisson.command.CommandAsyncExecutor;
import org.redisson.command.CommandBatchService;
import org.redisson.misc.RPromise;
import org.redisson.misc.RedissonPromise;
import org.redisson.pubsub.LockPubSub;

public class RedissonLock extends RedissonObject {
    final String id;
    protected long internalLockLeaseTime;
    final CommandAsyncExecutor commandExecutor;
    protected final LockPubSub pubSub;
    final String entryName;

    public RedissonLock(CommandAsyncExecutor commandExecutor, String name) {
        super(commandExecutor, name);
        this.commandExecutor = commandExecutor;
        this.id = commandExecutor.getConnectionManager().getId();
        this.internalLockLeaseTime = commandExecutor.getConnectionManager().getCfg().getLockWatchdogTimeout();
        this.entryName = id + ":" + name;
        this.pubSub = commandExecutor.getConnectionManager().getSubscribeService().getLockPubSub();
    }
    
    /**
     * 加锁 锁的有效期默认30秒
     */
    public void lock() {
        try {
            lock(-1, null, false);
        } catch (InterruptedException e) {
            throw new IllegalStateException();
        }
    }

    private void lock(long leaseTime, TimeUnit unit, boolean interruptibly) throws InterruptedException {
        // 当前线程ID
        long threadId = Thread.currentThread().getId();
        // 尝试获取锁
        Long ttl = tryAcquire(-1, leaseTime, unit, threadId);
        // 如果ttl为空，则证明获取锁成功
        if (ttl == null) {
            return;
        }
        // 如果获取锁失败，则订阅到对应这个锁的channel
        RFuture<RedissonLockEntry> future = subscribe(threadId);
        commandExecutor.syncSubscription(future);
        try {
            while (true) {
                // 再次尝试获取锁
                ttl = tryAcquire(-1, leaseTime, unit, threadId);
                // ttl为空，说明成功获取锁，跳出循环
                if (ttl == null) {
                    break;
                }
                // ttl大于0 则等待ttl时间后继续尝试获取
                if (ttl >= 0) {
                    try {
                        future.getNow().getLatch().tryAcquire(ttl, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        if (interruptibly) {
                            throw e;
                        }
                        future.getNow().getLatch().tryAcquire(ttl, TimeUnit.MILLISECONDS);
                    }
                } else {
                    if (interruptibly) {
                        future.getNow().getLatch().acquire();
                    } else {
                        future.getNow().getLatch().acquireUninterruptibly();
                    }
                }
            }
        } finally {
            unsubscribe(future, threadId);
        }
    }

    protected RFuture<RedissonLockEntry> subscribe(long threadId) {
        return pubSub.subscribe(getEntryName(), getChannelName());
    }

    protected void unsubscribe(RFuture<RedissonLockEntry> future, long threadId) {
        pubSub.unsubscribe(future.getNow(), getEntryName(), getChannelName());
    }

    private Long tryAcquire(long waitTime, long leaseTime, TimeUnit unit, long threadId) {
        return get(tryAcquireAsync(waitTime, leaseTime, unit, threadId));
    }

    private <T> RFuture<Long> tryAcquireAsync(long waitTime, long leaseTime, TimeUnit unit, long threadId) {
        // 如果带有过期时间，则按照普通方式获取锁.lock()方法不带过期时间
        if (leaseTime != -1) {
            return tryLockInnerAsync(waitTime, leaseTime, unit, threadId, RedisCommands.EVAL_LONG);
        }
        // 先按照30秒的过期时间来执行获取锁的方法
        RFuture<Long> ttlRemainingFuture = tryLockInnerAsync(waitTime, commandExecutor.getConnectionManager().getCfg().getLockWatchdogTimeout(), TimeUnit.MILLISECONDS,
                threadId, RedisCommands.EVAL_LONG);
        // 如果还持有这个锁，则开启定时任务不断刷新该锁的过期时间
        ttlRemainingFuture.onComplete((ttlRemaining, e) -> {
            if (e != null) {
                return;
            }

            // lock acquired
            if (ttlRemaining == null) {
                scheduleExpirationRenewal(threadId);
            }
        });
        return ttlRemainingFuture;
    }

    <T> RFuture<T> tryLockInnerAsync(long waitTime, long leaseTime, TimeUnit unit, long threadId, RedisStrictCommand<T> command) {
        internalLockLeaseTime = unit.toMillis(leaseTime);// 过期时间
        String script = // 如果锁不存在，则通过hset设置它的值，并设置过期时间
                "if (redis.call('exists', KEYS[1]) == 0) then " + "redis.call('hincrby', KEYS[1], ARGV[2], 1); " + "redis.call('pexpire', KEYS[1], ARGV[1]); "
                        + "return nil; " + "end; " +
                        // 如果锁已存在，并且锁的是当前线程，则通过hincrby给数值递增1
                        "if (redis.call('hexists', KEYS[1], ARGV[2]) == 1) then " + "redis.call('hincrby', KEYS[1], ARGV[2], 1); "
                        + "redis.call('pexpire', KEYS[1], ARGV[1]); " + "return nil; " + "end; " +
                        // 如果锁已存在，但并非本线程，则返回过期时间ttl
                        "return redis.call('pttl', KEYS[1]);";
        return evalWriteAsync(getName(), LongCodec.INSTANCE, command, script, Collections.singletonList(getName()), internalLockLeaseTime, getLockName(threadId));
    }

    protected <T> RFuture<T> evalWriteAsync(String key, Codec codec, RedisCommand<T> evalCommandType, String script, List<Object> keys, Object... params) {
        CommandBatchService executorService = createCommandBatchService();
        RFuture<T> result = executorService.evalWriteAsync(key, codec, evalCommandType, script, keys, params);
        if (!(commandExecutor instanceof CommandBatchService)) {
            executorService.executeAsync();
        }
        return result;
    }
    
    private CommandBatchService createCommandBatchService() {
        return null;
    }

    protected String getLockName(long threadId) {
        return id + ":" + threadId;
    }

    private void scheduleExpirationRenewal(long threadId) {

    }

    protected String getEntryName() {
        return entryName;
    }

    String getChannelName() {
        return prefixName("redisson_lock__channel", getName());
    }

    public void unlock() {
        try {
            get(unlockAsync(Thread.currentThread().getId()));
        } catch (RedisException e) {
            if (e.getCause() instanceof IllegalMonitorStateException) {
                throw (IllegalMonitorStateException) e.getCause();
            } else {
                throw e;
            }
        }
    }

    public RFuture<Void> unlockAsync(long threadId) {
        RPromise<Void> result = new RedissonPromise<Void>();
        RFuture<Boolean> future = unlockInnerAsync(threadId);

        future.onComplete((opStatus, e) -> {
            cancelExpirationRenewal(threadId);

            if (e != null) {
                result.tryFailure(e);
                return;
            }

            if (opStatus == null) {
                IllegalMonitorStateException cause = new IllegalMonitorStateException(
                        "attempt to unlock lock, not locked by current thread by node id: " + id + " thread-id: " + threadId);
                result.tryFailure(cause);
                return;
            }

            result.trySuccess(null);
        });

        return result;
    }

    protected RFuture<Boolean> unlockInnerAsync(long threadId) {
        return evalWriteAsync(getName(), LongCodec.INSTANCE, RedisCommands.EVAL_BOOLEAN,
                // 如果锁已经不存在， 发布锁释放的消息
                "if (redis.call('hexists', KEYS[1], ARGV[3]) == 0) then " + "return nil;" + "end; " +
                // 通过hincrby递减1的方式，释放一次锁
                // 若剩余次数大于0 ，则刷新过期时间
                        "local counter = redis.call('hincrby', KEYS[1], ARGV[3], -1); " + "if (counter > 0) then " + "redis.call('pexpire', KEYS[1], ARGV[2]); "
                        + "return 0; " +
                        // 否则证明锁已经释放，删除key并发布锁释放的消息
                        "else " + "redis.call('del', KEYS[1]); " + "redis.call('publish', KEYS[2], ARGV[1]); " + "return 1; " + "end; " + "return nil;",
                Arrays.asList(getName(), getChannelName()), LockPubSub.UNLOCK_MESSAGE, internalLockLeaseTime, getLockName(threadId));
    }

    void cancelExpirationRenewal(Long threadId) {

    }
}
