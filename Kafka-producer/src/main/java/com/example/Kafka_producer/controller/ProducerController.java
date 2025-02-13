package com.example.Kafka_producer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Kafka_producer.service.KafkaProducerService;


@RestController
public class ProducerController {

    @Autowired
    private KafkaProducerService producerService;

    /*endpoind /send */
    @PostMapping("/send")
    public String sendMessage(@RequestParam("message") String message){

        producerService.sendMessage(message);
        return "Mensaje enviado: " + message;
    }
}
