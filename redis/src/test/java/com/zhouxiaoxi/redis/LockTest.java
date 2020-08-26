package com.zhouxiaoxi.redis;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.zhouxiaoxi.redis.lock.LuaLock;
import com.zhouxiaoxi.redis.lock.SimpleLock;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LockTest {
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private SimpleLock sLock;
    @Autowired
    private LuaLock luaLock;

    private ExecutorService executorService = Executors.newCachedThreadPool();

    private int stock = 100;

    @Test
    public void simpleTest() throws InterruptedException {
        redissonlockDemo();
    }
    
    public void luaLockDemo() throws InterruptedException {
        String key = "xiaqi:myLock";
        for (int i = 0; i < 100; i++) {
            executorService.execute(() -> {
                luaLock.lock(key);
                stock--;
                System.out.println("扣减成功，库存stock：" + stock);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
                luaLock.unlock(key);
            });
        }
        Thread.sleep(10000);
    }

    /**
     * 多线程模拟Redisson分布式锁的使用
     */
    public void redissonlockDemo() throws InterruptedException {
        RLock lock = redissonClient.getLock("xiaqi:myLock");
        for (int i = 0; i < 100; i++) {
            executorService.execute(() -> {
                lock.lock();
                stock--;
                System.out.println("扣减成功，库存stock：" + stock);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
                lock.unlock();
            });
        }
        Thread.sleep(10000);
    }
    
    public void simpleLockDemo() throws InterruptedException {
        String key = "xiaqi:myLock";
        for (int i = 0; i < 100; i++) {
            executorService.execute(() -> {
                sLock.lock(key);
                stock--;
                System.out.println("扣减成功，库存stock：" + stock);
                sLock.unlock(key);
            });
        }
        Thread.sleep(10000);
    }
}
