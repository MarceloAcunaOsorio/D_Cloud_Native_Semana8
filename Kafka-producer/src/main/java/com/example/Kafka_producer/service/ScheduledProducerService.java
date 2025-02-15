package com.example.Kafka_producer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.Kafka_producer.model.SignosVitales;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class ScheduledProducerService {

    @Autowired
    private KafkaProducerService producerService;

    @Scheduled(fixedRate = 5000) // Ejecutar cada segundo
    public void generateAndSendVitalSigns() {
        SignosVitales signos = new SignosVitales();
        signos.setFrecuenciaCardiaca((int) (60 + Math.random() * 40)); // 60-100 bpm
        signos.setPresionArterialSistolica((int) (90 + Math.random() * 30)); // 90-120 mmHg
        signos.setPresionArterialDiastolica((int) (60 + Math.random() * 20)); // 60-80 mmHg
        signos.setTemperatura(36.5 + Math.random() * 1.5); // 36.5-38°C

        try {
            producerService.sendSignosVitales(signos);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
