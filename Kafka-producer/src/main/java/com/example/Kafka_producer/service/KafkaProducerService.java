package com.example.Kafka_producer.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**implementa la logica para enviar mensajes a la cola de kafka */

@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;


    private static final String TOPIC = "mi-topic";

    public void sendMessage(String message){
        kafkaTemplate.send(TOPIC, message);
    }
    
}
