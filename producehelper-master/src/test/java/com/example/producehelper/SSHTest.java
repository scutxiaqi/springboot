package com.example.producehelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.producehelper.service.inf.SSHService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SSHTest {
    @Autowired
    private SSHService service;

    private String stationId = "3308A01";

    private String[] stationIds = { "AL4001", "3308A01" };

    @Test
    public void myTest() throws Exception {
        service.publishBos(stationId);

        //Thread.sleep(1000 * 60 * 10); // 10分钟
    }
}
