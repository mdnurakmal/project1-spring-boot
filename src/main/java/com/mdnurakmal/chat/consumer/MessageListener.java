package com.mdnurakmal.chat.consumer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mdnurakmal.chat.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

@Component
public class MessageListener {
    @Autowired
    SimpMessagingTemplate template;

    @KafkaListener(topics = TOPIC)
    public void listen(@Payload String message) throws JsonProcessingException {
        JsonNode data = new ObjectMapper().readTree(message);
        System.out.println("sending via kafka listener..");
        System.out.println("Message received: " + message.toString());
        System.out.println("Json converted: " + data);
    }
}