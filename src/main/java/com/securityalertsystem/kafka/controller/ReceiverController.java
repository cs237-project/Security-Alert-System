package com.securityalertsystem.kafka.controller;


import com.google.gson.Gson;
import com.securityalertsystem.entity.AlertMessage;
import com.securityalertsystem.entity.Client;
import com.securityalertsystem.kafka.consumer.Kafka_Consumer;
import com.securityalertsystem.repository.ClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.ExecutorService;

@Slf4j
@Component
@RestController
@RequestMapping("/kafka/messageReceiver")
public class ReceiverController {
    @Autowired
    ClientRepository clientRepository;

    @RequestMapping("/createQueue")
    public void listener() {
        List<Client> clients = clientRepository.findAll();
        Kafka_Consumer kafka_Consumer = new Kafka_Consumer();
        try {
            kafka_Consumer.execute();
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            kafka_Consumer.shutdown();
        }
    }


//    @KafkaListener(topics = "topic1", containerFactory = "kafkaListenerContainerFactory1")
//    public void receive1(AlertMessage message) {
//        log.info(gson.toJson(message));
//    }
//
//    @KafkaListener(topics = "topic2", containerFactory = "kafkaListenerContainerFactory2")
//    public void receive2(AlertMessage message) {
//        log.info(gson.toJson(message));
//    }
//
//    @KafkaListener(topics = "topic3", containerFactory = "kafkaListenerContainerFactory3")
//    public void receive3(AlertMessage message) {
//        log.info(gson.toJson(message));
//    }

}






