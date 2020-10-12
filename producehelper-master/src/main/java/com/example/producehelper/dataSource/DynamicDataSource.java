package com.example.producehelper.dataSource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * 动态数据源
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

    public DynamicDataSource(DataSource defaultDataSource, Map<Object, Object> targetDataSourceMap) {
        super.setDefaultTargetDataSource(defaultDataSource);
        super.setTargetDataSources(targetDataSourceMap);
        super.afterPropertiesSet();// 初始化bean的时候执行, AbstractRoutingDataSource实现了InitializingBean接口
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return CONTEXT_HOLDER.get();
    }

    public static void setDataSourceKey(String key) {
        CONTEXT_HOLDER.set(key);
    }

    public static void clearDataSourceKey() {
        CONTEXT_HOLDER.remove();
    }
}
