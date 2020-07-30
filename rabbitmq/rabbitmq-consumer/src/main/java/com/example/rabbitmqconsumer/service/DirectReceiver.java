package com.example.rabbitmqconsumer.service;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 多个消费者可以订阅同一个queue，消息会被轮询获取（不会重复消费）
 *
 */
@Component
@RabbitListener(queues = "TestDirectQueue")
public class DirectReceiver {
 
    @RabbitHandler
    public void process(String message) throws InterruptedException {
        Thread.sleep(1000);
        System.out.println("DirectReceiver  : " + message);
    }
 
}