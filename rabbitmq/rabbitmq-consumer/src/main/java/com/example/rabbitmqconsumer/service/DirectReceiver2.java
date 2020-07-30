package com.example.rabbitmqconsumer.service;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DirectReceiver2 {
 
    @RabbitHandler
    public void process(String message) {
        System.out.println("Receiver2  : " + message);
    }
 
}