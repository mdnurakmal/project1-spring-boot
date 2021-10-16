package com.mdnurakmal.chat.constants;


import org.springframework.beans.factory.annotation.Value;

public class KafkaConstants {
    @Value("${spring.kafka.consumer.bootstrap-servers}")
    public static String KAFKA_BROKER = "10.3.240.241:9092";

    public static final String KAFKA_TOPIC = "mytopic";
    public static final String GROUP_ID = "kafka-sandbox";

}