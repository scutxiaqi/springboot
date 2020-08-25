package com.zhouxiaoxi.redis.util;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.util.Assert;

public interface ValueOperations<K, V> {
    /**
     * 设置 key 的值为 value 如果key不存在添加key 保存值为value 如果key存在则对value进行覆盖
     */
    void set(K key, V value);

    /**
     * 设置 key 的值为 value 其它规则与 set(K key, V value)一样
     * 
     * @param key     不能为空
     * @param value   设置的值
     * @param timeout 设置过期的时间
     * @param unit    时间单位。不能为空
     * @see <a href="http://redis.io/commands/setex">Redis Documentation: SETEX</a>
     */
    void set(K key, V value, long timeout, TimeUnit unit);

    /**
     * 如果key不存在，则设置key的值为 value. 存在则不设置. 底层调用setnx命令
     * 
     * @param key   key不能为空
     * @param value 设置的值
     * @return 设置成功返回true 失败返回false
     */
    Boolean setIfAbsent(K key, V value);

    /**
     * 
     * @param key
     * @param value
     * @param timeout 过期时间
     * @param unit    时间单位
     * @return
     */
    Boolean setIfAbsent(K key, V value, long timeout, TimeUnit unit);

    /**
     * 
     * @param key
     * @param value
     * @param timeout 过期时间
     * @return
     */
    default Boolean setIfAbsent(K key, V value, Duration timeout) {
        Assert.notNull(timeout, "Timeout must not be null!");
        if (TimeoutUtils.hasMillis(timeout)) {
            return setIfAbsent(key, value, timeout.toMillis(), TimeUnit.MILLISECONDS);
        }
        return setIfAbsent(key, value, timeout.getSeconds(), TimeUnit.SECONDS);
    }

    /**
     * 如果key存在，则设置key的值为 value. 不存在则不设置
     * 
     * @param key   key不能为空
     * @param value 设置的值
     * @return 设置成功返回true 失败返回false
     */
    Boolean setIfPresent(K key, V value);

    /**
     * 把一个map的键值对添加到redis中，key-value 对应着 key value。如果key已经存在就覆盖，
     * 
     * @param map不能为null 为null抛出空指针异常 可以为空集合
     */
    void multiSet(Map<? extends K, ? extends V> map);

    /**
     * 把一个map的键值对添加到redis中，key-value 对应着 key value。 当且仅当map中的所有key都 不存在的时候，添加成功返回 true，否则返回false.
     * 
     * @param map map不能为空 可以为empty
     */
    Boolean multiSetIfAbsent(Map<? extends K, ? extends V> map);

    /**
     * 根据 key 获取对应的value 如果key不存在则返回null
     * 
     * @param key 不能为null
     */
    V get(Object key);

    /**
     * 设置key的值为value 并返回旧值。 如果key不存在返回为null
     * 
     * @param key 不能为null
     */
    V getAndSet(K key, V value);

    /**
     * 根据提供的key集合按顺序获取对应的value值
     * 
     * @param 集合不能为null 可以为empty 集合
     */
    List<V> multiGet(Collection<K> keys);

    /**
     * 为key 的值加 1
     * 
     * @param key
     * @return
     */
    Long increment(K key);

    /**
     * 为key 的值加上 long delta. 原来的值必须是能转换成Integer类型的。否则会抛出异常。
     * 
     * @param key   不能为null
     * @param delta 需要增加的值
     */
    Long increment(K key, long delta);

    /**
     * 为key 的值加上 double delta. 原来的值必须是能转换成Integer类型的。否则会抛出异常。 添加double后不能再加整数。已经无法在转换为Integer
     * 
     * @param key  不能为null
     * @param 增加的值
     */
    Double increment(K key, double delta);

    /**
     * 为 key的值末尾追加 value 如果key不存在就直接等于 set(K key, V value)
     *
     * @param key   不能为null
     * @param value 追加的值
     * @see <a href="http://redis.io/commands/append">Redis Documentation: APPEND</a>
     */
    Integer append(K key, String value);

    /**
     * 获取key 值从 start位置开始到end位置结束。 等于String 的 subString 前后闭区间 0 -1 整个key的值 -4 -1 从尾部开始往前截长度为4
     * 
     * @param key   不能为null
     * @param start 起始位置
     * @param end   结束位置
     * @see <a href="http://redis.io/commands/getrange">Redis Documentation: GETRANGE</a>
     */
    String get(K key, long start, long end);

    /**
     * 将value从指定的位置开始覆盖原有的值。如果指定的开始位置大于字符串长度，先补空格在追加。 如果key不存在，则等于新增。长度大于0则先补空格 set("key10", "abc", 3) 得到结果为： 3空格 +"abc"
     * 
     * @param key    不能为null
     * @param value  值
     * @param offset 开始的位置
     */
    void set(K key, V value, long offset);

    /**
     * 获取key的value的长度。key不存在返回0
     * 
     * @param key 不能为空
     */
    Long size(K key);

    /**
     * 设置key的值偏移量为offset的bit位上的值为0或者1.true:1 false:0
     *
     * @param key    不能为空
     * @param offset 偏移量
     * @param value  true or false
     */
    Boolean setBit(K key, long offset, boolean value);

    /**
     * 获取key的值偏移量offset的bit位的值。 返回true or false
     *
     * @param key    不能为空
     * @param offset 偏移量 可以通过redis的 JedisConverters 对布尔结果进行转换
     */
    Boolean getBit(K key, long offset);
}
