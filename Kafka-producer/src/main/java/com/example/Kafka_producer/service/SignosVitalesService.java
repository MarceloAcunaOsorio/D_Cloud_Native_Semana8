package com.example.Kafka_producer.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.retry.annotation.Retryable;

@Service
public class SignosVitalesService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Retryable(maxAttempts = 3)
    public CompletableFuture<SendResult<String, String>> sendMessage(String topicName, String message) {
        return this.kafkaTemplate.send(topicName, message);
    }
}

