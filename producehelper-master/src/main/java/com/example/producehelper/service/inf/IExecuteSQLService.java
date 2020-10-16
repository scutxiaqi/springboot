package com.example.producehelper.service.inf;

import com.example.producehelper.model.StationSelected;

public interface IExecuteSQLService {
    String runSql(StationSelected stationSelected) throws Exception;

    String init(StationSelected stationSelected) throws Exception;
    
    void runSql(String[] stationIds) throws Exception;

    void runSql(String sql, String[] stationIds) throws Exception;
    
    void runSql(String sql) throws Exception;
    
    void runSql() throws Exception;
}
