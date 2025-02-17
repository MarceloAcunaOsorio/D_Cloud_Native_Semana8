package com.example.Kafka_producer.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import org.springframework.kafka.support.SendResult;
import org.springframework.retry.annotation.Retryable;

@Service
public class SignosVitalesService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Retryable(maxAttempts = 5)
    public CompletableFuture<SendResult<String, String>> sendMessage(String topicName, String message) {
        return this.kafkaTemplate.send(topicName, message);
    }

    @Value("${kafka.topic}")
    private String topicName;

    private static final String VITAL_SIGNS_ENDPOINT = "http://localhost:8085/api/signos-vitales";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(SignosVitalesService.class);

    public SignosVitalesService() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::publishVitalSigns, 0, 13600, TimeUnit.SECONDS);
    }

    private void publishVitalSigns() {
        try {
            String vitalSignsJson = restTemplate.getForObject(VITAL_SIGNS_ENDPOINT, String.class);
            JsonNode vitalSigns = objectMapper.readTree(vitalSignsJson);
            sendMessage(topicName, vitalSigns.toString());
            logger.info("Published vital signs to Kafka topic {}: {}", topicName, vitalSigns);
        } catch (IOException e) {
            logger.error("Error processing JSON: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Error fetching vital signs: {}", e.getMessage());
        }
    }
}
