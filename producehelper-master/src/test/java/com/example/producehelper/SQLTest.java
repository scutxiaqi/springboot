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
    String sql = "";

    private String[] stationIds = { "AEE003" };

    @Test
    public void myTest() throws Exception {
        service.myRunSql(sql, stationIds);
    }

}
