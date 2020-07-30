package com.example.rabbitmqconsumer.service;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "TestDirectQueue")
public class DirectReceiver2 {
 
    @RabbitHandler
    public void process(String message) throws InterruptedException {
        Thread.sleep(1000);
        System.out.println("DirectReceiver2  : " + message);
    }
 
}