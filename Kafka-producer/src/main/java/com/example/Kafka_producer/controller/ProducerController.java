package com.example.Kafka_producer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Kafka_producer.model.SignosVitales;
import com.example.Kafka_producer.service.KafkaProducerService;
import com.fasterxml.jackson.core.JsonProcessingException;


@RestController
public class ProducerController {

    @Autowired
    private KafkaProducerService producerService;

    /*endpoind /send */
    @PostMapping("/send/vital")
    public ResponseEntity<String> sendVitalMessage(@RequestBody SignosVitales signos) {
        try {
            producerService.sendSignosVitales(signos);
            return ResponseEntity.ok("Señales vitales enviadas: " + signos.toString());
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error procesando las señales vitales: " + e.getMessage());
        }
    }

    @PostMapping("/send/alert") 
    public String sendAlertMessage(@RequestParam("message") String message) {
        producerService.sendAlert(message);
        return "Mensaje de alerta enviado: " + message;
    }
}
