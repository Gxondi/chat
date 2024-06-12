package com.hyh.mallchat.common.chat.controller;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@RocketMQMessageListener(consumerGroup = "test-group", topic = "hyh-topic")
@Component
public class TestConsumer implements RocketMQListener<String> {

    @Override
    public void onMessage(String dto) {
        System.out.println("收到消息{}"+dto);
    }
}
