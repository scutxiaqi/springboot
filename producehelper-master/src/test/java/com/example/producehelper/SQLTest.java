package com.example.producehelper;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.producehelper.model.StationSelected;
import com.example.producehelper.service.inf.IExecuteSQLService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SQLTest {
    @Autowired
    private IExecuteSQLService service;
    
    private String[] stations = {"3308A01"};
    
    @Test
    public void myTest() throws Exception {
        StationSelected item = new StationSelected();
        //item.setSelected("all");
        item.setStations(Arrays.asList(stations));
        service.runSql(item);
    }
}
