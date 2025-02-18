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

    //Indica que el método debe ser reintentado en caso de que ocurra un error.
    @Retryable(maxAttempts = 5)
    public CompletableFuture<SendResult<String, String>> sendMessage(String topicName, SignosVitales signosVitales) throws JsonProcessingException {
        String message = objectMapper.writeValueAsString(signosVitales);
        return this.kafkaTemplate.send(topicName, message);
    }

    @Value("${kafka.topic}")
    private String topicName;

    private static final String VITAL_SIGNS_ENDPOINT = "http://34.196.103.34:8085/api/signos-vitales";

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



            // Verificar que el JSON sea un arreglo
            if (vitalSignsNode.isArray()) 
            {
                // Iterar sobre cada objeto en el arreglo
                for (JsonNode vitalSignNode : vitalSignsNode) 
                {
                    // Extraer el pacienteId de cada objeto en el arreglo
                    JsonNode pacienteIdNode = vitalSignNode.get("pacienteId");
                    String pacienteId = (pacienteIdNode != null && !pacienteIdNode.isNull()) ? pacienteIdNode.asText() : null;
                    int frecuenciaCardiaca = vitalSignNode.get("frecuenciaCardiaca").asInt(0);
                    int presionArterial = vitalSignNode.get("presionArterial").asInt(0);
                    double temperatura = vitalSignNode.get("temperatura").asDouble(0.0);
                    int saturacionOxigeno = vitalSignNode.get("saturacionOxigeno").asInt(0);

                    // Crear el objeto SignosVitales con los datos extraídos
                    SignosVitales signosVitales = new SignosVitales(pacienteId, frecuenciaCardiaca, presionArterial, temperatura, saturacionOxigeno);

                    // Publicar los datos a Kafka (o lo que sea necesario)
                    sendMessage(topicName, signosVitales);
                    logger.info("Published vital signs to Kafka topic {}: {}", topicName, signosVitales);
                }
            } 
            else {
                logger.warn("El JSON recibido no es un arreglo. No se puede procesar.");
            }

                    
          
        } catch (IOException e) {
            logger.error("Error processing JSON: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Error fetching vital signs: {}", e.getMessage());
        }
    }
}
