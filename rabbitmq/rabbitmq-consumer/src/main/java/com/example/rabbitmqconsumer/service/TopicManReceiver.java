package com.example.rabbitmqconsumer.service;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
 
@Component
@RabbitListener(queues = "topic.man")
public class TopicManReceiver {
 
    @RabbitHandler
    public void process(String message) {
        System.out.println("TopicManReceiver消费者收到消息  : " + message);
    }
}
