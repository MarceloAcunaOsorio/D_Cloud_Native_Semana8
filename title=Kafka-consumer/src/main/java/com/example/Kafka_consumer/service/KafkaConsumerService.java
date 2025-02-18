package com.example.Kafka_consumer.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @KafkaListener(topics = "senales_vitales", groupId = "group_idx")
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
                
                String alertaJson = objectMapper.writeValueAsString(alerta);
                kafkaTemplate.send("alertas", alertaJson);
            }
        } catch (Exception e) {
            System.err.println("Error procesando mensaje: " + e.getMessage());
        }
    }

    private List<String> detectarAnomalias(SignosVitales signos) {
        List<String> anomalias = new ArrayList<>();
        
        if (signos.getFrecuenciaCardiaca() < 60 || signos.getFrecuenciaCardiaca() > 100) {
            anomalias.add("Frecuencia cardíaca anormal: " + signos.getFrecuenciaCardiaca());
        }
        
        if (signos.getPresionArterial() < 90 || signos.getPresionArterial() > 140) {
            anomalias.add("Presión arterial anormal: " + signos.getPresionArterial());
        }
        
        if (signos.getTemperatura() < 36.5 || signos.getTemperatura() > 37.5) {
            anomalias.add("Temperatura anormal: " + signos.getTemperatura());
        }
        
        if (signos.getSaturacionOxigeno() < 95) {
            anomalias.add("Saturación de oxígeno baja: " + signos.getSaturacionOxigeno());
        }
        
        return anomalias;
    }
} 