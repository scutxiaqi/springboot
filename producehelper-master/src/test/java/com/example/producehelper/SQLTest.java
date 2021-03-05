package com.example.producehelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.producehelper.service.inf.IExecuteSQLService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SQLTest {
    @Autowired
    private IExecuteSQLService service;
    String sql = "SELECT defvalue FROM s_config WHERE varname='store_state'";
//团购增加字段失败站点："AC3001", "AC8002", "AC1002", "AC1003"
    private String[] stationIds = { "AJL001", "AY3001"};

    @Test
    public void myTest() throws Exception {
        service.myRunSql(sql, stationIds);
    }

    // @Test
    public void test() throws Exception {
        // service.runSql(stationIds);
    }

}
