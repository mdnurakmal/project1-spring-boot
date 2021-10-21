package com.mdnurakmal.chat.consumer;


import com.mdnurakmal.chat.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

@Component
public class MessageListener {
    @Autowired
    SimpMessagingTemplate template;

    @KafkaListener(topics = TOPIC)
    public void listen(Message message) {
        System.out.println("sending via kafka listener..");
        System.out.println("Message received: " + message);
    }
}