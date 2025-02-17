package com.example.Kafka_producer.service;

import com.example.Kafka_producer.model.SignosVitales;
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
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class SignosVitalesService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Retryable(maxAttempts = 5)
    public CompletableFuture<SendResult<String, String>> sendMessage(String topicName, SignosVitales signosVitales) throws JsonProcessingException {
        String message = objectMapper.writeValueAsString(signosVitales);
        return this.kafkaTemplate.send(topicName, message);
    }

    @Value("${kafka.topic}")
    private String topicName;

    private static final String VITAL_SIGNS_ENDPOINT = "http://localhost:8085/api/signos-vitales";

    private final RestTemplate restTemplate = new RestTemplate();
    private static final Logger logger = LoggerFactory.getLogger(SignosVitalesService.class);

    //ejecucion de envio de mensajes
    public SignosVitalesService() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::publishVitalSigns, 0, 5, TimeUnit.SECONDS);
    }

    private void publishVitalSigns() {
        try {
            String vitalSignsJson = restTemplate.getForObject(VITAL_SIGNS_ENDPOINT, String.class);
            JsonNode vitalSignsNode = objectMapper.readTree(vitalSignsJson);

            // Extract values from JsonNode with null checks
            JsonNode pacienteIdNode = vitalSignsNode.get("pacienteId");
            String pacienteId = (pacienteIdNode != null) ? pacienteIdNode.asText() : null;

            JsonNode frecuenciaCardiacaNode = vitalSignsNode.get("frecuenciaCardiaca");
            int frecuenciaCardiaca = (frecuenciaCardiacaNode != null) ? frecuenciaCardiacaNode.asInt() : 0;

            JsonNode presionArterialSistolicaNode = vitalSignsNode.get("presionArterialSistolica");
            int presionArterialSistolica = (presionArterialSistolicaNode != null) ? presionArterialSistolicaNode.asInt() : 0;

            JsonNode presionArterialDiastolicaNode = vitalSignsNode.get("presionArterialDiastolica");
            int presionArterialDiastolica = (presionArterialDiastolicaNode != null) ? presionArterialDiastolicaNode.asInt() : 0;

            JsonNode temperaturaNode = vitalSignsNode.get("temperatura");
            double temperatura = (temperaturaNode != null) ? temperaturaNode.asDouble() : 0.0;

            JsonNode saturacionOxigenoNode = vitalSignsNode.get("saturacionOxigeno");
            int saturacionOxigeno = (saturacionOxigenoNode != null) ? saturacionOxigenoNode.asInt() : 0;

            // Create SignosVitales object
            SignosVitales signosVitales = new SignosVitales(pacienteId, frecuenciaCardiaca, presionArterialSistolica, presionArterialDiastolica, temperatura, saturacionOxigeno);

            sendMessage(topicName, signosVitales);
            logger.info("Published vital signs to Kafka topic {}: {}", topicName, signosVitales);
        } catch (IOException e) {
            logger.error("Error processing JSON: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Error fetching vital signs: {}", e.getMessage());
        }
    }
}
