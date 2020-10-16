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
    String sql="INSERT INTO b_goods_stock_record(ctime,mtime,cuser,muser,goods_id,goods_count_old,goods_count_now,stock_record_count,stock_record_type,employee_id,remark)\n"+ 
            " VALUES (NOW(), NOW(), 'xiaqi', 'xiaqi', ?, ?, ?, ?, '22', 'xiaqi', '站点非能切换');";

    private String[] stationIds = {"3308A01"};

    @Test
    public void myTest() throws Exception {
        service.runSql(sql, stationIds);
    }
}
