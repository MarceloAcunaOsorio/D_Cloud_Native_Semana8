package com.example.Kafka_producer.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

/**
 * Configuración del productor de Kafka.
 * Define los beans necesarios para la producción de mensajes a Kafka.
 */
@Configuration
public class KafkaProducerConfig {
    
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.producer.key-serializer}")
    private Class<?> keySerializer;

    @Value("${spring.kafka.producer.value-serializer}")
    private Class<?> valueSerializer;

    @Value("${spring.kafka.producer.acks}")
    private String acks;

    @Value("${spring.kafka.producer.retries}")
    private int retries;

    @Value("${spring.kafka.producer.batch-size}")
    private int batchSize;

    @Value("${spring.kafka.producer.linger-ms}")
    private int lingerMs;

    @Value("${spring.kafka.producer.buffer-memory}")
    private int bufferMemory;

    @Value("${spring.kafka.producer.client-id}")
    private String clientId;

    @Value("${spring.kafka.producer.compression-type}")
    private String compressionType;

    /**
     * Crea y configura el ProducerFactory para Kafka.
     * @return ProducerFactory configurado
     */
    @Bean
    public ProducerFactory<String, String> producerFactory(){
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
        configProps.put(ProducerConfig.ACKS_CONFIG, acks);
        configProps.put(ProducerConfig.RETRIES_CONFIG, retries);
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);
        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
        configProps.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, compressionType);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Crea y configura el KafkaTemplate.
     * @return KafkaTemplate configurado
     */
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
