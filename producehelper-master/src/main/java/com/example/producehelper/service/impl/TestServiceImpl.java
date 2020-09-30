package com.example.producehelper.service.impl;

import com.example.producehelper.mapper.SConfigMapper;
import com.example.producehelper.service.inf.ITestService;
import com.example.producehelper.service.inf.ITestService2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
public class TestServiceImpl implements ITestService
{
    @Autowired
    private SConfigMapper configMapper;

    @Autowired
    private ITestService2 testService2;

    @Override
    @Transactional
    public String transactionTest1()
    {
        String value = configMapper.findDefValueByName("stock_statistics_date");
        System.out.println("test1:------" + value);
        configMapper.updateValByName("stock_statistics_date", "16");
        try
        {
            TimeUnit.SECONDS.sleep(15);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        value = configMapper.findDefValueByName("stock_statistics_date");
        System.out.println("test1:------" + value);
        return value;
    }

    @Override
    @Transactional()
    public String transactionTest2()
    {
        String value = configMapper.findDefValueByName("stock_statistics_date");
        System.out.println("test2:------" + value);
        try
        {
            TimeUnit.SECONDS.sleep(15);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        value = configMapper.findDefValueByName("stock_statistics_date");
        System.out.println("test2:------" + value);
        return value;
    }

    @Override
    @Transactional()
    public String transactionTest3()
    {
        String value = configMapper.findDefValueByName("stock_statistics_date");
        System.out.println("test3:------" + value);
//        configMapper.updateValByName("cur_shift", "31");
        try
        {
//            testError2();
            testService2.testError();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        configMapper.updateValByName("cur_shift", "25");
//        testError2();
//        testError();
        value = configMapper.findDefValueByName("stock_statistics_date");
        System.out.println("test3:------" + value);
        return value;
    }

    @Override
    @Transactional()
    public String transactionTest4()
    {
        String value = configMapper.findDefValueByName("stock_statistics_date");
        System.out.println("test1:------" + value);
        configMapper.updateValByName("stock_statistics_date", "17");
        return "SUCCESS";
    }

    private void testPrivate()
    {
        String value = configMapper.findDefValueByName("stock_statistics_date");

        System.out.println("testPrivate:------" + value);

        configMapper.updateValByName("stock_statistics_date", "14");
    }

    private void testError()
    {
        String value = configMapper.findDefValueByName("stock_statistics_date");

        System.out.println("testError:------" + value);

        configMapper.updateValByName("stock_statistics_date", "11");

        int i = 1/0;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testError2()
    {
        String value = configMapper.findDefValueByName("stock_statistics_date");

        System.out.println("testError2:------" + value);

        configMapper.updateValByName("stock_statistics_date", "55");

        int i = 1/0;
    }
}
