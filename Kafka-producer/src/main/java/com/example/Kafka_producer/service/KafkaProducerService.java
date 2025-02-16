package com.example.Kafka_producer.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.Kafka_producer.model.SignosVitales;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Random;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;


/**implementa la logica para enviar mensajes a la cola de kafka */

@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();
    private Random random = new Random();
    private RestTemplate restTemplate = new RestTemplate();
    
    @Value("${kafka.topic.signos_vitales}")
    private String TOPIC;

    public void sendMessage(String message) {
        try {
            kafkaTemplate.send(TOPIC, message).whenComplete((result, ex) -> {
                if (ex == null) {
                    System.out.println("Mensaje enviado correctamente al topic: " + TOPIC);
                    System.out.println("Offset: " + result.getRecordMetadata().offset());
                } else {
                    System.err.println("Error al enviar mensaje: " + ex.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("Error inesperado al enviar mensaje: " + e.getMessage());
        }
    }

    public void sendSignosVitales(SignosVitales signos) throws JsonProcessingException {
        try {
            String message = objectMapper.writeValueAsString(signos);
            kafkaTemplate.send(TOPIC, message).whenComplete((result, ex) -> {
                if (ex == null) {
                    System.out.println("Signos vitales enviados correctamente al topic: " + TOPIC);
                    System.out.println("Offset: " + result.getRecordMetadata().offset());
                } else {
                    System.err.println("Error al enviar signos vitales: " + ex.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("Error inesperado al enviar signos vitales: " + e.getMessage());
        }
    }

    public void sendAlert(String message) {
        try {
            kafkaTemplate.send(TOPIC, message).whenComplete((result, ex) -> {
                if (ex == null) {
                    System.out.println("Alerta enviada correctamente al topic: " + TOPIC);
                    System.out.println("Offset: " + result.getRecordMetadata().offset());
                } else {
                    System.err.println("Error al enviar alerta: " + ex.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("Error inesperado al enviar alerta: " + e.getMessage());
        }
    }

    @Scheduled(fixedRate = 5000)
    public void sendVitalSigns() {
        try {
            System.out.println("Intentando obtener signos vitales del backend...");
            ResponseEntity<SignosVitales[]> response = restTemplate.getForEntity(
                "http://localhost:8085/api/signos-vitales", 
                SignosVitales[].class);
                
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                System.out.println("Se obtuvieron " + response.getBody().length + " signos vitales del backend");
                for (SignosVitales signs : response.getBody()) {
                    String message = objectMapper.writeValueAsString(signs);
                    kafkaTemplate.send(TOPIC, message).whenComplete((result, ex) -> {
                        if (ex == null) {
                            System.out.println("Signos vitales enviados correctamente al topic: " + TOPIC);
                        } else {
                            System.err.println("Error al enviar signos vitales: " + ex.getMessage());
                        }
                    });
                }
            } else {
                System.out.println("No se obtuvieron datos del backend, generando datos de prueba...");
                SignosVitales testSigns = generateRandomVitalSigns();
                String message = objectMapper.writeValueAsString(testSigns);
                kafkaTemplate.send(TOPIC, message);
            }
        } catch (Exception e) {
            System.err.println("Error al obtener/enviar signos vitales: " + e.getMessage());
            System.out.println("Generando datos de prueba debido al error...");
            try {
                SignosVitales testSigns = generateRandomVitalSigns();
                String message = objectMapper.writeValueAsString(testSigns);
                kafkaTemplate.send(TOPIC, message);
            } catch (Exception ex) {
                System.err.println("Error al enviar datos de prueba: " + ex.getMessage());
            }
        }
    }

    private SignosVitales generateRandomVitalSigns() {
        return new SignosVitales(
            random.nextInt(100) + 1, // frecuencia cardiaca
            random.nextInt(40) + 80, // presión arterial sistólica
            random.nextInt(30) + 50, // presión arterial diastólica
            random.nextDouble() * 10 + 35, // temperatura
            random.nextInt(100) + 1 // saturación de oxígeno
        );
    }
}
