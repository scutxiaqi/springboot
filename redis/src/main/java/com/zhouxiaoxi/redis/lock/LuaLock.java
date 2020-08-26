package com.zhouxiaoxi.redis.lock;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

/**
 * Lua版本的分布式锁
 */
@Service
public class LuaLock {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 加锁
     *
     * @param key
     */
    public void lock(String key) {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("lua/lock.lua"));
        script.setResultType(Long.class); // 返回值类型
        for (;;) {
            Long ttl = redisTemplate.execute(script, Arrays.asList(key), 30000, Thread.currentThread().getId());
            if (ttl == null) {
                return;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
    }

    public void unlock(String key) {
        DefaultRedisScript<Integer> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("lua/unlock.lua"));
        script.setResultType(Integer.class);
        redisTemplate.execute(script, Arrays.asList(key), 30000, Thread.currentThread().getId());
    }
}
