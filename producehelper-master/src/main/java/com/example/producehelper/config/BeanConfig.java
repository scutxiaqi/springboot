package com.example.producehelper.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class BeanConfig
{
    //@Bean("myExecutor")
    public ExecutorService myExecutor()
    {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        return executor;
    }
}
