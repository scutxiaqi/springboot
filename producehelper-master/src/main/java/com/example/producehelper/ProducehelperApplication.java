package com.example.producehelper;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableEncryptableProperties
@MapperScan(basePackages = {"com.example.producehelper.mapper"}, sqlSessionFactoryRef = "sqlSessionFactory")
public class ProducehelperApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(ProducehelperApplication.class, args);
    }

}
