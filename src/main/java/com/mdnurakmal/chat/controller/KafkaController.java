package com.mdnurakmal.chat.controller;

import com.mdnurakmal.chat.model.ChatRoom;
import com.mdnurakmal.chat.model.Message;
import com.mdnurakmal.chat.service.ChatRoomService;
import org.apache.kafka.clients.consumer.*;
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
import java.util.stream.Collectors;

import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

@RestController
public class KafkaController {

    @Autowired
    private ConsumerFactory<Integer, String> consumerFactory;

    @Autowired
    private KafkaTemplate<String, Message> kafkaTemplate;

    @Autowired
    ChatRoomService chatRoomService;

//    private String recipient;
//    private String sender;
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
    @MessageMapping("/topic/messages/{hashcode}")
    public void sendMessage(@DestinationVariable String hashcode, @Payload Message message) {
        System.out.println("Receive Message from: " + hashcode);

        try {
            //Sending the message to kafka topic queue
            //System.out.println("sending to kafka at topic:" + "topic.messages." + sender.hashCode()  +"." + recipient.hashCode() );
            kafkaTemplate.send("topic.messages." +   hashcode  , message).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


    @MessageMapping("/topic/loadMessages/{sender}/{recipient}")
    public void loadMessages(@DestinationVariable String recipient, @DestinationVariable String sender, @Payload String message) {
        System.out.println("Change recipient to: " + recipient );

        if(message=="getAllMessagesTopic")
        {
            try{
                String topic = chatRoomService.sendMessage(sender,recipient);
                messagingTemplate.convertAndSend( "/topic/loadMessages/"+sender+"/"+recipient+"/result",topic.hashCode());

            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        else if(message=="subscribedToTopic")
        {
            try{
                String topic = chatRoomService.sendMessage(sender,recipient);
                seekToStart( sender, recipient,String.valueOf(topic.hashCode() ));
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @MessageMapping("/topic/loadSidebar/{sender}")
    public void loadSidebar(@DestinationVariable String sender, @Payload String message) {
        List<ChatRoom> topics = chatRoomService.getAllRecipients(sender);
        for (ChatRoom chatRoom : topics) {
            System.out.println(chatRoom.toString());
            getLastMessage(chatRoom.getTopic(),sender);
        }

        //"topic.messages.*." + sender.hashCode()
    }

    public void getLastMessage(String topic,String sender){
        // configuration

        Map<String, Object> consumerConfig = new HashMap<>(consumerFactory.getConfigurationProperties());
        consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerConfig.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);
        System.out.println("subscribing");
//

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerConfig);

        List<TopicPartition> topicPartitions = new ArrayList<>();
        for (PartitionInfo partitionInfo : consumer.partitionsFor("topic.messages." +   topic.hashCode())) {
            topicPartitions.add(new TopicPartition(partitionInfo.topic(), partitionInfo.partition()));
        }

        // seek from first
        consumer.assign(topicPartitions);
        consumer.seekToBeginning(consumer.assignment());

        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1_000));

        records.forEach(record -> {
            JSONObject jsonObject= new JSONObject(record.value() );
            System.out.println("sending jsonobject to string:" + jsonObject.toString());
            System.out.println("sending raw value:" + record.value());

            messagingTemplate.convertAndSend( "/topic/loadSidebar/"+sender+"/result",record.value());
        });
    }




    public void getUniqueUser(String sender){
        // configuration
        Map<String, Object> consumerConfig = new HashMap<>(consumerFactory.getConfigurationProperties());
        consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerConfig.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);
        System.out.println("subscribing");
//
        var pattern = Pattern.compile("topic.messages.*."+sender.hashCode());
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerConfig);
//        consumer.subscribe(pattern);
//        consumer.poll(Duration.ofMillis(100L));
//        consumer.seekToBeginning(consumer.assignment());
//        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1_000));

//        records.forEach(record -> {
//            JSONObject jsonObject= new JSONObject(record.value() );
//            System.out.println("sending !! /topic/messages/"+jsonObject.getString("receiver")+"/"+jsonObject.getString("sender"));
//
//            messagingTemplate.convertAndSend( "/topic/messages/"+jsonObject.getString("receiver")+"/"+jsonObject.getString("sender"),jsonObject.toString());
//            System.out.println("partition: " + record.partition() +
//                    ", topic: " + record.topic() +
//                    ", offset: " + record.offset() +
//                    ", key: " + record.key() +
//                    ", value: " + record.value());
//        });

        //consumer.subscribe(pattern);
//        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100L)); //no loop to simplify
//        System.out.println("******************************************");
//        records.forEach(record -> {
//            JSONObject jsonObject= new JSONObject(record.value() );
//            System.out.println("partition: " + record.partition() +
//                    ", topic: " + record.topic() +
//                    ", offset: " + record.offset() +
//                    ", key: " + record.key() +
//                    ", value: " + record.value());
//        });

        Map<String, List<PartitionInfo>> topics = consumer.listTopics();

        List<String> topicsMatched = new ArrayList();

        for (Map.Entry<String, List<PartitionInfo>> topic : topics.entrySet()) {

            String[] words = topic.getKey().split("\\.");
            if( words.length == 4)
            {
                if(Integer.parseInt(words[3]) ==sender.hashCode())
                {
                    topicsMatched.add(topic.getKey());

                    List<TopicPartition> topicPartitions = new ArrayList<>();
                    for (PartitionInfo partitionInfo : consumer.partitionsFor(topic.getKey())) {
                        topicPartitions.add(new TopicPartition(partitionInfo.topic(), partitionInfo.partition()));
                    }

                    // seek from first
                    consumer.assign(topicPartitions);
                    consumer.seekToBeginning(consumer.assignment());

                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1_000));


                    records.forEach(record -> {
                        JSONObject jsonObject= new JSONObject(record.value() );
                        System.out.println("sending jsonobject to string:" + jsonObject.toString());
                        System.out.println("sending raw value:" + record.value());

                        messagingTemplate.convertAndSend( "/topic/loadSidebar/"+sender+"/result",record.value());

                    });


                }
            }
        }



    }

    public void seekToStart(String sender,String recipient,String hashcode ) {
        // configuration
        Map<String, Object> consumerConfig = new HashMap<>(consumerFactory.getConfigurationProperties());
        consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerConfig);

        List<TopicPartition> topicPartitions = new ArrayList<>();
        for (PartitionInfo partitionInfo : consumer.partitionsFor("topic.messages."+hashcode)) {
            topicPartitions.add(new TopicPartition(partitionInfo.topic(), partitionInfo.partition()));
        }

        // seek from first
        consumer.assign(topicPartitions);
        consumer.seekToBeginning(consumer.assignment());

        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1_000));

        records.forEach(record -> {
            JSONObject jsonObject= new JSONObject(record.value() );
            System.out.println("sending !! /topic/messages/"+hashcode);

            messagingTemplate.convertAndSend( "/topic/messages/"+hashcode,jsonObject.toString());
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