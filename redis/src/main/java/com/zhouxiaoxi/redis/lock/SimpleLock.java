package com.zhouxiaoxi.redis.lock;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Redis分布式锁(简单版)
 */
@Service
public class SimpleLock {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 加锁
     *
     * @param id
     * @return
     */
    public void lock(String key) {
        for (;;) {
            if (redisTemplate.opsForValue().setIfAbsent(key, 1, Duration.ofSeconds(60))) {
                //setExclusiveOwnerThread(current);
                return;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
    }
    
    public void unlock(String key) {
        redisTemplate.delete(key);
    }
    
    private void setExclusiveOwnerThread(Thread current) {
        
    }
}
