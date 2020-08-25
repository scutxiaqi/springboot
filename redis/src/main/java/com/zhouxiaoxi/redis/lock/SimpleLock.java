package com.zhouxiaoxi.redis.lock;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Redis分布式锁(简单版)
 */
public class SimpleLock {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 加锁
     *
     * @param id
     * @return
     */
    public boolean lock(String key) {
        for (;;) {
            if (redisTemplate.opsForValue().setIfAbsent(key, 1, Duration.ofSeconds(60))) {
                return true;
            }
        }
    }
    
    public boolean unlock(String key) {
        return true;
    }
}
