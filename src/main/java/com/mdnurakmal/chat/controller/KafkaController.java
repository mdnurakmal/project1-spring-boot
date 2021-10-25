package com.mdnurakmal.chat.controller;

import com.mdnurakmal.chat.model.Message;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.*;

import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
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
    private ConsumerFactory<Integer, String> consumerFactory;

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
            seekToStart("topic.messages." +   sender.hashCode()  +"." +   recipient.hashCode());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void seekToStart(String topic ) {
        // configuration
        Map<String, Object> consumerConfig = new HashMap<>(consumerFactory.getConfigurationProperties());
        consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, "SEEKTOSTART");
        consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
        consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        KafkaConsumer<String, Message> consumer = new KafkaConsumer<>(consumerConfig);

        List<TopicPartition> topicPartitions = new ArrayList<>();
        for (PartitionInfo partitionInfo : consumer.partitionsFor(topic)) {
            topicPartitions.add(new TopicPartition(partitionInfo.topic(), partitionInfo.partition()));
        }

        // seek from first
        consumer.assign(topicPartitions);
        consumer.seekToBeginning(consumer.assignment());

        ConsumerRecords<String, Message> records = consumer.poll(Duration.ofMillis(1_000));

        records.forEach(record -> {
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