package com.zhouxiaoxi.redis.util;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public interface ListOperations<K, V> {

    /**
     * 获取指定key的范围内的value值的 list列表。 （0 -1）反回所有值列表
     *
     * @param key   不能为null
     * @param start 起始位置
     * @param end   结束位置
     * @return V的列表
     */
    List<V> range(K key, long start, long end);

    /**
     * 保留key指定范围内的列表值。其它的都删除。
     *
     * @param key   不能为null
     * @param start 起始位置
     * @param end   结束位置
     */
    void trim(K key, long start, long end);

    /**
     * 获取key 列表的长度
     *
     * @param key 不能为null
     * @return 列表长度
     */
    Long size(K key);

    /**
     * 从key列表左边（从头部）插入 值
     *
     * @param key   不能为空
     * @param value 插入列表的值
     * @return 返回列表插入后的长度
     */
    Long leftPush(K key, V value);

    /**
     * 从key列表左边（头部）插入多个值
     *
     * @param key   不能为空
     * @param value 插入列表的值
     * @return 返回列表插入后的长度
     */
    Long leftPushAll(K key, V... values);

    /**
     * 从key列表左边（头部）依次插入集合中的值
     *
     * @param key   不能为空
     * @param value 插入列表的值
     * @return 返回列表插入后的长度
     */
    Long leftPushAll(K key, Collection<V> values);

    /**
     * 如果列表存在，则在列表左边插入值value
     *
     * @param key   不能为空
     * @param value 插入列表的值
     * @return 返回列表插入后的长度
     */
    Long leftPushIfPresent(K key, V value);

    /**
     * 在key的列表中指定的value左边（前面）插入一个新的value. 如果 指定的value不存在则不插入任何值。
     *
     * @param key   不能为空
     * @param pivot 指定的value
     * @param value 插入的value
     * @return 返回列表的c行都
     */
    Long leftPush(K key, V pivot, V value);

    /**
     * 参照 leftPush(K key, V value)
     */
    Long rightPush(K key, V value);

    /**
     * 参照 Long leftPushAll(K key, V... values);
     */
    Long rightPushAll(K key, V... values);

    /**
     * 参照 Long rightPushAll(K key, Collection<V> values);
     */
    Long rightPushAll(K key, Collection<V> values);

    /**
     * 参照 Long rightPushIfPresent(K key, V value);
     */
    Long rightPushIfPresent(K key, V value);

    /**
     * 参照 Long rightPush(K key, V pivot, V value);
     *
     */
    Long rightPush(K key, V pivot, V value);

    /**
     * 设置key列表中指定位置的值为value index不能大于列表长度。大于抛出异常 为负数则从右边开始计算
     * 
     * @param key   不能为空
     * @param index
     * @param value
     */
    void set(K key, long index, V value);

    /**
     * 删除列表中第一个遇到的value值。count指定删除多少个。
     *
     * @param key   不能为null
     * @param count
     * @param value
     * @return 返回列表的长度
     */
    Long remove(K key, long count, Object value);

    /**
     * 获取列表中指定索引的value
     *
     * @param key   不能为null
     * @param index
     * @return
     * @see <a href="http://redis.io/commands/lindex">Redis Documentation:
     *      LINDEX</a>
     */
    V index(K key, long index);

    /**
     * 移除列表中的第一个值，并返回该值
     *
     * @param key 不能为null
     * @return 移除的值
     */
    V leftPop(K key);

    /**
     * 阻塞版本的 leftPop(K key) 移除列表的第一个值，并且返回。如果列表为空， 则一直阻塞指定的时间单位
     *
     * @param key     不能为null
     * @param timeout 时间
     * @param unit    不能为空，时间单位
     * @return
     * @see <a href="http://redis.io/commands/blpop">Redis Documentation: BLPOP</a>
     */
    V leftPop(K key, long timeout, TimeUnit unit);

    /**
     * 参照 V leftPop(K key);
     */
    V rightPop(K key);

    /**
     * 参照 V leftPop(K key, long timeout, TimeUnit unit);
     */
    V rightPop(K key, long timeout, TimeUnit unit);

    /**
     * 从指定列表中从右边（尾部）移除第一个值，并将这个值从左边（头部）插入目标列表 返回移除（插入）的值
     */
    V rightPopAndLeftPush(K sourceKey, K destinationKey);

    /**
     * 阻塞版本的 V rightPopAndLeftPush(K sourceKey, K destinationKey);
     * 如果移除的列表中没有值，则一直阻塞指定的单位时间
     */
    V rightPopAndLeftPush(K sourceKey, K destinationKey, long timeout, TimeUnit unit);
}
