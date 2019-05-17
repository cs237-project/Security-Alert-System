package com.securityalertsystem.kafka.producer;

import com.securityalertsystem.Service.AlertSender;
import com.securityalertsystem.entity.AlertMessage;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

@Component
public class SimpleProducer implements AlertSender {

    @Autowired
    @Qualifier("kafkaTemplate")
    private KafkaTemplate<String, AlertMessage> kafkaTemplate;



//    public void send(String topic, AlertMessage message) {
//        kafkaTemplate.send(topic, message);
//    }
    @Override
    public void send1(AlertMessage message) {
        ProducerRecord<String, AlertMessage> record = new ProducerRecord<>(
                "topic1",
                message);

        kafkaTemplate.send(record);
    }

    @Override
    public void send2(AlertMessage message) {
        ProducerRecord<String, AlertMessage> record = new ProducerRecord<>(
                "topic2",
                message);

        kafkaTemplate.send(record);
    }

    @Override
    public void send3(AlertMessage message) {
        ProducerRecord<String, AlertMessage> record = new ProducerRecord<>(
                "topic3",
                message);

        kafkaTemplate.send(record);
    }
}