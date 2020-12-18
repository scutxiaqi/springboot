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
//有数据站点：[AJ6001, AH4002, AH3002, AF4001]
    private String[] stationIds = { "AK4003" };

    @Test
    public void myTest() throws Exception {
        service.myRunSql(sql, stationIds);
    }

    //@Test
    public void test() throws Exception {
        //service.runSql(stationIds);
    }

}
