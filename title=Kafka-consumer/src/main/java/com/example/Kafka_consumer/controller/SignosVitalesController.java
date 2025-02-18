package com.example.Kafka_consumer.controller;

import com.example.Kafka_consumer.model.SignosVitales;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/signosvitales")
public class SignosVitalesController {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public SignosVitalesController(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public String publishSignosVitales(@RequestBody SignosVitales signosVitales) {
        try {
            // Convertir el objeto SignosVitales a JSON y publicarlo en el tópico "senales_vitales"
            String message = objectMapper.writeValueAsString(signosVitales);
            kafkaTemplate.send("senales_vitales", message);
            return "Mensaje enviado correctamente a Kafka";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al enviar el mensaje: " + e.getMessage();
        }
    }
} 