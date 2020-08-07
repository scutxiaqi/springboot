package com.zhouxiaoxi.redis.util;

import java.util.Collection;
import java.util.Set;

import org.springframework.data.redis.connection.RedisZSetCommands.Limit;
import org.springframework.data.redis.connection.RedisZSetCommands.Range;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

public interface ZSetOperations<K, V> {
    /**
     * 给有序集合添加一个指定分数的成员 如果成员存在则覆盖
     *
     * @param key must not be {@literal null}.
     * @param score the score.
     * @param value the value.
     * @return
     */
    Boolean add(K key, V value, double score);

    /**
     * 通过TypedTuple的方式，往有序集合中添加成员集合。
     *TypedTuple 默认实现为 DefaultTypedTuple
     * @param key must not be {@literal null}.
     * @param tuples must not be {@literal null}.
     * @return
     */
    Long add(K key, Set<TypedTuple<V>> tuples);

    /**
     * 移除有序集合中指定的多个成员. 如果成员不存在则忽略
     *
     * @param key must not be {@literal null}.
     * @param values must not be {@literal null}.
     * @return
     */
    Long remove(K key, Object... values);

    /**
     * 给有序集合中的指定成员的分数增加 delta
     * @param key must not be {@literal null}.
     * @param delta
     * @param value the value.
     * @return
     */
    Double incrementScore(K key, V value, double delta);

    /**
     * 计算并返回成员在有序集合中从低到高的排名（第一名为0）
     *
     * @param key must not be {@literal null}.
     * @param o the value.
     * @return
     */
    Long rank(K key, Object o);

    /**
     * 计算并返回成员在有序集合中从高到低的排名（第一名为0）
     * @param key must not be {@literal null}.
     * @param o the value.
     * @return
     */
    Long reverseRank(K key, Object o);

    /**
     * 获取指定范围内的成员集合。（0 -1）返回所有。 如果为正数，则按正常顺序取，如果为负数则反序取。
     *
     * @param key must not be {@literal null}.
     * @param start
     * @param end
     * @return
     */
    Set<V> range(K key, long start, long end);

    /**
     * 获取有序集合中指定分数范围内的成员集合
     *
     * @param key must not be {@literal null}.
     * @param start
     * @param end
     * @return
     */
    Set<TypedTuple<V>> rangeWithScores(K key, long start, long end);

    /**
     * 获取有序集合中分数在指定的最小值 与最大值之间的所有成员集合  闭合区间
     *
     * @param key must not be {@literal null}.
     * @param min
     * @param max
     * @return
     */
    Set<V> rangeByScore(K key, double min, double max);

    /**
     * 获取有序集合中分数在指定的最小值 与最大值之间的所有成员的TypedTuple集合  闭合区间
     * @param key must not be {@literal null}.
     * @param min
     * @param max
     * @return
     */
    Set<TypedTuple<V>> rangeByScoreWithScores(K key, double min, double max);

    /**
     *从有序集合中从指定位置（offset)开始，取 count个范围在（min)与（max)之间的成员集合
     * @param key must not be {@literal null}.
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return
     */
    Set<V> rangeByScore(K key, double min, double max, long offset, long count);

    /**
     * 从有序集合中从指定位置（offset)开始，取 count个范围在（min)与（max)之间的
     * 成员TypedTuple集合
     * @param key
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return
     */
    Set<TypedTuple<V>> rangeByScoreWithScores(K key, double min, double max, long offset, long count);

    /**
     * 从有序集合中获取指定范围内从高到低的成员集合
     *
     * @param key must not be {@literal null}.
     * @param start
     * @param end
     * @return
     */
    Set<V> reverseRange(K key, long start, long end);

    /**
     *从有序集合中获取指定范围内从高到低的成员TypedTuple集合
     *
     * @param key must not be {@literal null}.
     * @param start
     * @param end
     * @return
     */
    Set<TypedTuple<V>> reverseRangeWithScores(K key, long start, long end);

    /**
     *从有序集合中获取分数在指定范围内从高到低的成员集合
     *
     * @param key must not be {@literal null}.
     * @param min
     * @param max
     * @return
     */
    Set<V> reverseRangeByScore(K key, double min, double max);

    /**
     *从有序集合中获取分数在指定范围内从高到低的成员TypedTuple集合
     */
    Set<TypedTuple<V>> reverseRangeByScoreWithScores(K key, double min, double max);

    /**
     * 从有序集合中从指定位置（offset)开始 获取 count个
     * 分数在指定（min, max)范围内从高到低的成员集合
     * sorted set ordered high -> low.
     *
     * @param key must not be {@literal null}.
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return
     */
    Set<V> reverseRangeByScore(K key, double min, double max, long offset, long count);

    /**
     *  从有序集合中从指定位置（offset)开始 获取 count个
     * 分数在指定（min, max)范围内从高到低的成员TypedTuple集合
     */
    Set<TypedTuple<V>> reverseRangeByScoreWithScores(K key, double min, double max, long offset, long count);

    /**
     * 统计分数在范围内的成员个数
     */
    Long count(K key, double min, double max);

    /**
     * 返回有序集合的大小
     */
    Long size(K key);

    /**
     * 返回有序集合的大小
     */
    Long zCard(K key);

    /**
     *获取有序集合中某个成员的分数
     */
    Double score(K key, Object o);

    /**
     * 移除有序集合中从start开始到end结束的成员 闭区间
     */
    Long removeRange(K key, long start, long end);

    /**
     * 移除有序集合中分数在指定范围内[min,max]的成员
     */
    Long removeRangeByScore(K key, double min, double max);

    /**
     * 求两个有序集合的并集，并存到目标集合中。 如果存在相同的成员，则分数相加。
     */
    Long unionAndStore(K key, K otherKey, K destKey);

    /**
     * 
     *一个有序集合与多个有序集合进行并集， 如果存在相同成员，则分数相加。
     */
    Long unionAndStore(K key, Collection<K> otherKeys, K destKey);

    /**
     * 求两个有序集合中相同成员的并集（分数相加），并存到目标集合中。没有共同的成员则忽略
     */
    Long intersectAndStore(K key, K otherKey, K destKey);

    /**
     * 一个有序集合与多个有序集合进行相同成员并集， 如果存在不相同相同成员，则忽略
     */
    Long intersectAndStore(K key, Collection<K> otherKeys, K destKey);

    /**
     * 获取有序集合的TypedTuple游标。
     * ScanOptions  可以配置匹配正则 设置参考的count
     */
    Cursor<TypedTuple<V>> scan(K key, ScanOptions options);

    /**
     * 获取有序集合中成员在指定范围内的集合（成员范围，不是分值范围）
     */
    Set<V> rangeByLex(K key, Range range);

    /**
     * 获取指定索引开始，获取count个成员在指定范围内的集合（成员范围，不是分值范围）
     */
    Set<V> rangeByLex(K key, Range range, Limit limit);
}
