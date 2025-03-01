package com.example.Kafka_consumer.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import com.example.Kafka_consumer.model.Alerta;
import com.example.Kafka_consumer.model.SignosVitales;

@Service
public class KafkaConsumerService {
    
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final AlertaService alertaService;

    public KafkaConsumerService(KafkaTemplate<String, String> kafkaTemplate, 
                               ObjectMapper objectMapper,
                               AlertaService alertaService) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.alertaService = alertaService;
    }

    @KafkaListener(topics = "signosvitales_topic", groupId = "group_idx")
    public void consumeSignosVitales(String message) {
        try {
            SignosVitales signosVitales = objectMapper.readValue(message, SignosVitales.class);
            List<String> anomalias = detectarAnomalias(signosVitales);
            
            if (!anomalias.isEmpty()) {
                Alerta alerta = new Alerta();
                alerta.setPacienteId(Long.parseLong(signosVitales.getPacienteId()));
                alerta.setFecha(LocalDateTime.now());
                alerta.setDescripcion(String.join(", ", anomalias));
                alerta.setTipo("ANOMALIA");
                
                // Enviar alerta a Kafka
                String alertaJson = objectMapper.writeValueAsString(alerta);
                kafkaTemplate.send("alertas_topic", alertaJson);
                
                // Guardar alerta en la base de datos
                alertaService.guardarAlerta(alerta);
            }
        } catch (Exception e) {
            System.err.println("Error procesando mensaje: " + e.getMessage());
        }
    }

    private List<String> detectarAnomalias(SignosVitales signos) {
        List<String> anomalias = new ArrayList<>();
        
        // Verificar frecuencia cardíaca (60-100 normal)
        if (signos.getFrecuenciaCardiaca() < 60 || signos.getFrecuenciaCardiaca() > 100) {
            anomalias.add("Frecuencia cardíaca anormal: " + signos.getFrecuenciaCardiaca());
        }
        
        // Verificar presión arterial (90-140 normal)
        if (signos.getPresionArterial() < 90 || signos.getPresionArterial() > 140) {
            anomalias.add("Presión arterial anormal: " + signos.getPresionArterial());
        }
        
        // Verificar temperatura (36.5-37.5 normal)
        if (signos.getTemperatura() < 36.5 || signos.getTemperatura() > 37.5) {
            anomalias.add("Temperatura anormal: " + signos.getTemperatura());
        }
        
        // Verificar saturación de oxígeno (95-100 normal)
        if (signos.getSaturacionOxigeno() < 95) {
            anomalias.add("Saturación de oxígeno baja: " + signos.getSaturacionOxigeno());
        }
        
        return anomalias;
    }
}
