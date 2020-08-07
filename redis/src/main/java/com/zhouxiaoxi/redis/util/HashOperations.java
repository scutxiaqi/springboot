package com.zhouxiaoxi.redis.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;

public interface HashOperations<H, HK, HV> {
    /**
     * 从散列中删除给定的多个元素
     * @param key 不能为null 散列的名称
     * @param hashKeys 需要删除的keys集合
     */
    Long delete(H key, Object... hashKeys);

    /**
     * 判断散列中是否存在某个key
     */
    Boolean hasKey(H key, Object hashKey);

    /**
     * 得到某个散列中key的hash值
     */
    HV get(H key, Object hashKey);

    /**
     * 得到多个key的值。
     */
    List<HV> multiGet(H key, Collection<HK> hashKeys);

    /**
     *为散了中某个值加上 整型 delta
     */
    Long increment(H key, HK hashKey, long delta);

    /**
     * 为散了中某个值加上 double delta
     */
    Double increment(H key, HK hashKey, double delta);

    /**
     * 获取散列中所有的key集合
     */
    Set<HK> keys(H key);

    /**
     * 获取散列的大小
     */
    Long size(H key);

    /**
     * 为散列添加多个key-value键值对
     *
     * @param key must not be {@literal null}.
     * @param m must not be {@literal null}.
     */
    void putAll(H key, Map<? extends HK, ? extends HV> m);

    /**
     * 为散列添加或者覆盖一个 key-value键值对
     */
    void put(H key, HK hashKey, HV value);

    /**
     * 为散列添加一个key-value键值对。如果存在则不添加不覆盖。返回false
     */
    Boolean putIfAbsent(H key, HK hashKey, HV value);

    /**
     * 获取散列的value集合
     */
    List<HV> values(H key);

    /**
     * 获取散列的key-value键值对集合
     */
    Map<HK, HV> entries(H key);

    /**
     * 获取散列的游标。
     * 可以参考：http://blog.csdn.net/pengdandezhi/article/details/78909041
     */
    Cursor<Map.Entry<HK, HV>> scan(H key, ScanOptions options);
}
