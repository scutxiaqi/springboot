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
    String sql = "SELECT defvalue FROM `s_config` WHERE varname='store_state'";

    private String[] stationIds = { "3308A01" };

    //@Test
    public void myTest() throws Exception {
        //service.myRunSql(sql);
    }
    
    @Test
    public void test() throws Exception {
        service.runSql();
    }

}
