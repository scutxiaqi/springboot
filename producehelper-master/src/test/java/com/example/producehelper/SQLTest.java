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
    String sql = "SELECT goods_id FROM b_goods_stock_daily_statistics WHERE transfer_avg_price=0 AND statistics_date = (SELECT max(statistics_date) FROM b_goods_stock_daily_statistics)";

    private String[] stationIds = {"AC2001"};

    @Test
    public void myTest() throws Exception {
        service.runSql(sql, stationIds);
    }
}
