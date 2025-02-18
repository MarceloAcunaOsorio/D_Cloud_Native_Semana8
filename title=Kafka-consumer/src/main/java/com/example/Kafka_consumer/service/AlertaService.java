package com.example.Kafka_consumer.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.Kafka_consumer.model.Alerta;
import com.example.Kafka_consumer.repository.AlertaRepository;
import org.springframework.kafka.annotation.KafkaListener;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AlertaService {
    
    private final AlertaRepository alertaRepository;
    private final ObjectMapper objectMapper;
    
    public AlertaService(AlertaRepository alertaRepository, ObjectMapper objectMapper) {
        this.alertaRepository = alertaRepository;
        this.objectMapper = objectMapper;
    }
    
    @Transactional
    public void guardarAlerta(Alerta alerta) {
        alertaRepository.save(alerta);
    }
    
    // Se actualiza el tópico a "alertas" para recibir las notificaciones de alerta
    @KafkaListener(topics = "alertas", groupId = "alert-service-group")
    public void procesarAlerta(String alertaJson) {
        try {
            Alerta alerta = objectMapper.readValue(alertaJson, Alerta.class);
            guardarAlerta(alerta);
            System.out.println("Alerta guardada en base de datos: " + alertaJson);
        } catch (Exception e) {
            System.err.println("Error procesando alerta: " + e.getMessage());
        }
    }
} 