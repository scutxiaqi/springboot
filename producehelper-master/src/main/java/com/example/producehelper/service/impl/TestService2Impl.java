package com.example.producehelper.service.impl;

import com.example.producehelper.mapper.SConfigMapper;
import com.example.producehelper.service.inf.ITestService2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestService2Impl implements ITestService2
{
    @Autowired
    private SConfigMapper configMapper;
    @Override

    @Transactional()
    public void testError()
    {
        String value = configMapper.findDefValueByName("stock_statistics_date");

        System.out.println("testError2:------" + value);

        configMapper.updateValByName("stock_statistics_date", "18");

        int i = 1/0;
    }
}
