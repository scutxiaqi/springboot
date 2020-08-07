package com.zhouxiaoxi.redis;

import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.Data;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisApplicationTests {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testRedis() {
        string();
    }
    
    public void string() {
        redisTemplate.opsForValue().increment("xiaqi:userNum");
    }
    
    /**
     * list用法。如果需要使用列表可以考虑，例如新闻网站首页的最近新闻（只要10条）
     */
    public void list() {
        News news = new News();
        news.setTitle("美国大选");
        news.setDate("2020-08-06");
        redisTemplate.opsForList().leftPush("lastestNews", news);
        news = new News();
        news.setTitle("中印边界冲突");
        redisTemplate.opsForList().leftPush("lastestNews", news);
        List<Object> list = redisTemplate.opsForList().range("lastestNews", 0, -1);
        System.out.println(list);
    }
    
    public void set() {
        redisTemplate.opsForSet().add("xiaqi:guanzhu", "迪丽热巴","杨幂");
        redisTemplate.opsForSet().add("leilei:guanzhu", "迪丽热巴","赵丽颖");
        Set<Object> set = redisTemplate.opsForSet().intersect("xiaqi:guanzhu", "leilei:guanzhu");
        System.out.println(set);
    }
    
    public void zset() {
        stringRedisTemplate.opsForZSet().add("dianjibang", "仙逆", 10000);
        stringRedisTemplate.opsForZSet().add("dianjibang", "凡人修仙传", 12000);
        stringRedisTemplate.opsForZSet().add("dianjibang", "老司机", 3000);
        // 正序获取列表（分值从小到大），start表示起始位置的index，从0开始。index表示end的位置，-1表示获取全部
        Set<String> set = stringRedisTemplate.opsForZSet().range("dianjibang", 0, -1);
        // 倒序获取列表
        Set<String> set2 = stringRedisTemplate.opsForZSet().reverseRange("dianjibang", 0, -1);
    }
}

@Data// 新闻
class News{
    private String title;// 标题
    private String date;// 发布日期
}
