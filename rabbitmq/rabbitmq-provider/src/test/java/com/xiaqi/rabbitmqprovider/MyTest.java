package com.xiaqi.rabbitmqprovider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MyTest {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    public void myTest() throws Exception {
        for (int i = 0; i < 10; i++) {
            String messageId = "hello world "+ i;
            // 将消息携带绑定键值：TestDirectRouting 发送到交换机TestDirectExchange
            rabbitTemplate.convertAndSend("TestDirectExchange", "TestDirectRouting", messageId);
        }
        
    }
}
