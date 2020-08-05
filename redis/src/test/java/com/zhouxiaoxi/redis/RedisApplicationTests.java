package com.zhouxiaoxi.redis;

import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.zhouxiaoxi.redis.util.RedisDao;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisApplicationTests {
    @Autowired
    private RedisDao redisDao;

    @Autowired
    private StringRedisTemplate template;

    //@Test
    public void testRedis() {
        redisDao.setKey("namehaha", "forezp");
    }
    
    @Test
    public void testZSet() {
        
        Set<String> set = template.opsForZSet().reverseRange("dianjibang", 0, -1);
        System.out.println(set);
    }
    
    private void dd() {
        template.opsForZSet().add("dianjibang", "仙逆", 10000);
        template.opsForZSet().add("dianjibang", "凡人修仙传", 12000);
        template.opsForZSet().add("dianjibang", "老司机", 3000);
        // 正序获取列表（分值从小到大），start表示起始位置的index，从0开始。index表示end的位置，-1表示获取全部
        Set<String> set = template.opsForZSet().range("dianjibang", 0, -1);
        // 倒序获取列表
        Set<String> set2 = template.opsForZSet().reverseRange("dianjibang", 0, -1);
    }

}
