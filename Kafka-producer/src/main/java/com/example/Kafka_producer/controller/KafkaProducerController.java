package com.example.Kafka_producer.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.Kafka_producer.service.KafkaProducerService;
import com.example.Kafka_producer.model.SignosVitales;
import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
@RequestMapping("/api/kafka")
public class KafkaProducerController {

    private final KafkaProducerService kafkaProducerService;

    public KafkaProducerController(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    @PostMapping("/send/message")
    public String sendMessage(@RequestBody String message) {
        kafkaProducerService.sendMessage(message);
        return "Message sent to Kafka topic";
    }

    @PostMapping("/send/vital")
    public String sendSignosVitales(@RequestBody SignosVitales signosVitales) throws JsonProcessingException {
        kafkaProducerService.sendSignosVitales(signosVitales);
        return "Signos vitales sent to Kafka topic";
    }
}
