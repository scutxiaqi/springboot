package com.zhouxiaoxi.redis.base;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private static final long serialVersionUID = -1636958140749926009L;
    
    /**
     * 缓存数据量大小
     */
    private final int CACHE_SIZE;

    public LRUCache(int cacheSize) {
        // 设置hashmap的初始大小，最后一个true让linkedhashmap按照访问顺序来进行排序
        super((int) Math.ceil(cacheSize / 0.75) + 1, 0.75f, true);
        CACHE_SIZE = cacheSize;
    }

    /**
     * 当map中的数据量大于最大缓存数据量时，就自动删除最老的数据
     */
    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        return size() > CACHE_SIZE;
    }
}
