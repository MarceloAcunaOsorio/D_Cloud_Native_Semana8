package com.example.Kafka_consumer.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "senales_vitales", groupId = "group_id")
    public void consume(String message) {
        System.out.println("Consumed message: " + message);
    }
}
