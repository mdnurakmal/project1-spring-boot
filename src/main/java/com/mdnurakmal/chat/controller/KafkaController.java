package com.mdnurakmal.chat.controller;

import com.mdnurakmal.chat.model.Message;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

@RestController
public class KafkaController {

    @Autowired
    private KafkaTemplate<String, Message> kafkaTemplate;

    @PostMapping(value = "/api/send", consumes = "application/json", produces = "application/json")
    public void sendMessage(@RequestBody Message message) {
        System.out.println(message);
        message.setTimestamp(LocalDateTime.now().toString());
        try {
            //Sending the message to kafka topic queue
            kafkaTemplate.send(TOPIC, message).get();

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    //@MessageMapping("/sendMessage")
    //@MessageMapping("/topic/messages")
    @MessageMapping("/topic/messages/{sender}/{recipient}")
    public void broadcastGroupMessage(@DestinationVariable String sender, @DestinationVariable String recipient, @Payload Message message) {
        //Sending this message to all the subscribers
        //message.setTimestamp(LocalDateTime.now().toString());
        System.out.println("receive message from " + sender + " , to: " + recipient + " / " + message);

        try {
            //Sending the message to kafka topic queue
            System.out.println("sending to kafka at topic:" + "topic.messages." + sender.hashCode()  +"." + recipient.hashCode() );

            kafkaTemplate.send("topic.messages." +   sender.hashCode()  +"." +   recipient.hashCode()  , message).get();

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @MessageMapping("/newUser")
    @SendTo("/topic/group")
    public Message addUser(@Payload Message message,
                           SimpMessageHeaderAccessor headerAccessor) {
        // Add user in web socket session
        headerAccessor.getSessionAttributes().put("username", message.getSender());
        return message;
    }
}