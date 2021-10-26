package com.mdnurakmal.chat.controller;

import com.mdnurakmal.chat.model.Message;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.*;

import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

@RestController
public class KafkaController {

    @Autowired
    private ConsumerFactory<Integer, String> consumerFactory;

    @Autowired
    private KafkaTemplate<String, Message> kafkaTemplate;

    @Autowired
    SimpMessagingTemplate messagingTemplate;

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

    @MessageMapping("/topic/getallmessagesfromuser/{recipient}/{sender}")
    public void getallmessagesfromuser(@DestinationVariable String recipient, @DestinationVariable String sender, @Payload String message) {
        System.out.println("get all messages  from " + recipient + " , to: " + sender + " / " + message);
        seekToStart("topic.messages." +   recipient.hashCode()  +"." +   sender.hashCode());
    }

    @MessageMapping("/topic/getallmessagesforuser/{sender}")
    public void getallmessagesforuser(@DestinationVariable String sender, @Payload String message) {
        getUniqueUser(sender);
        //"topic.messages.*." + sender.hashCode()
    }

    @MessageMapping("/topic/getallmessagessend/{sender}")
    public void getallmessagessend(@DestinationVariable String sender, @Payload String message) {
        seekToStart("topic.messages."+sender.hashCode()+".*");
    }

    public void getUniqueUser(String sender){
        // configuration
        Map<String, Object> consumerConfig = new HashMap<>(consumerFactory.getConfigurationProperties());
        consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);



        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerConfig);

        var pattern = Pattern.compile("topic.messages.*." + sender.hashCode());
        consumer.subscribe(pattern);
        consumer.seekToBeginning(consumer.assignment());

        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1_000)); //no loop to simplify

        records.forEach(record -> {
            JSONObject jsonObject= new JSONObject(record.value() );
            System.out.println("sending !! /topic/messages/"+jsonObject.getString("receiver")+"/"+jsonObject.getString("sender"));

            messagingTemplate.convertAndSend( "/topic/messages/"+jsonObject.getString("receiver")+"/"+jsonObject.getString("sender"),jsonObject.toString());
            System.out.println("partition: " + record.partition() +
                    ", topic: " + record.topic() +
                    ", offset: " + record.offset() +
                    ", key: " + record.key() +
                    ", value: " + record.value());
        });





    }

    public void seekToStart(String topic ) {
        // configuration
        Map<String, Object> consumerConfig = new HashMap<>(consumerFactory.getConfigurationProperties());
        consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerConfig);

        List<TopicPartition> topicPartitions = new ArrayList<>();
        for (PartitionInfo partitionInfo : consumer.partitionsFor(topic)) {
            topicPartitions.add(new TopicPartition(partitionInfo.topic(), partitionInfo.partition()));
        }

        // seek from first
        consumer.assign(topicPartitions);
        consumer.seekToBeginning(consumer.assignment());

        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1_000));

        records.forEach(record -> {
            JSONObject jsonObject= new JSONObject(record.value() );
            System.out.println("sending !! /topic/messages/"+jsonObject.getString("receiver")+"/"+jsonObject.getString("sender"));

            messagingTemplate.convertAndSend( "/topic/messages/"+jsonObject.getString("receiver")+"/"+jsonObject.getString("sender"),jsonObject.toString());
            System.out.println("partition: " + record.partition() +
                    ", topic: " + record.topic() +
                    ", offset: " + record.offset() +
                    ", key: " + record.key() +
                    ", value: " + record.value());
        });


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