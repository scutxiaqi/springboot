package com.example.rabbitmqconsumer.service;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.stereotype.Component;

@Component
public class DirectReceiver {
 
    @RabbitHandler
    public void process(String message) {
        System.out.println("DirectReceiver  : " + message);
    }
 
}