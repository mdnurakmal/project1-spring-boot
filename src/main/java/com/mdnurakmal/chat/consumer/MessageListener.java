package com.mdnurakmal.chat.consumer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mdnurakmal.chat.model.Message;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;


import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

@Component
public class MessageListener {
    @Autowired
    SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topicPattern  = "topic.messages.*.*")
    public void reply(@Payload String message) throws JsonProcessingException {
//        JsonNode data = new ObjectMapper().readTree(message);
//        System.out.println("sending via kafka listener..");
//        System.out.println("Message received: " + message.toString());
//        System.out.println("Json converted: " + data);
//        ((ObjectNode)data).put("content", "from kafka listener");
        JSONObject jsonObject= new JSONObject(message );
        jsonObject.put("content","from kafkalistener");
        System.out.println("sending via kafka listener..TO :" + "/topic/messages/jsonObject.getString(\"sender\")/jsonObject.getString(\"content\")");
        System.out.println("Message received: " + jsonObject.toString());
        System.out.println("Json converted: " + jsonObject.getString("sender"));
        messagingTemplate.convertAndSend( "/topic/messages/jsonObject.getString(\"sender\")/jsonObject.getString(\"content\")",jsonObject.toString());
  }
}

