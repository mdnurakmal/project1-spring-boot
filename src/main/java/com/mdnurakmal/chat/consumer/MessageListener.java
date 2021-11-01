package com.mdnurakmal.chat.consumer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mdnurakmal.chat.model.Message;
import com.mdnurakmal.chat.service.ChatRoomService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;


import java.util.concurrent.ExecutionException;

import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

@Component
public class MessageListener {
    @Autowired
    SimpMessagingTemplate messagingTemplate;

    @Autowired
    ChatRoomService chatRoomService;


    @KafkaListener(topicPattern  = "topic.messages.*")
    public void reply(@Payload String message) throws JsonProcessingException {
        JSONObject jsonObject= new JSONObject(message );

        try {
            //Sending the message to kafka topic queue
            String topic = chatRoomService.sendMessage(jsonObject.getString("sender"),jsonObject.getString("receiver"));
            System.out.println("sending via kafka listener..TO :" + "/topic/messages/"+jsonObject.getString("receiver")+"/"+jsonObject.getString("sender"));
            System.out.println("Message received: " + jsonObject.toString());
            System.out.println("Hashcode: " + topic.hashCode());
            messagingTemplate.convertAndSend( "/topic/messages/"+topic.hashCode(),jsonObject.toString());
            messagingTemplate.convertAndSend( "/topic/updateSidebar/"+topic.hashCode()+"/"+jsonObject.getString("receiver"),jsonObject.toString());
            messagingTemplate.convertAndSend( "/topic/updateSidebar/"+topic.hashCode()+"/"+jsonObject.getString("sender"),jsonObject.toString());


        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
   }

}

