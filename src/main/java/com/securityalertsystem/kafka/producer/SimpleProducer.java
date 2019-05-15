package com.securityalertsystem.kafka.producer;

import com.securityalertsystem.entity.AlertMessage;
import com.securityalertsystem.kafka.common.MessageEntity;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

@Component
public class SimpleProducer {

    @Autowired
    @Qualifier("kafkaTemplate")
    private KafkaTemplate<String, AlertMessage> kafkaTemplate;

    public void send(String topic, AlertMessage message) {
        kafkaTemplate.send(topic, message);
    }

    public void send(String topic, String key, AlertMessage entity) {
        ProducerRecord<String, AlertMessage> record = new ProducerRecord<>(
                topic,
                key,
                entity);

        long startTime = System.currentTimeMillis();

        ListenableFuture<SendResult<String, AlertMessage>> future = kafkaTemplate.send(record);
        future.addCallback(new ProducerCallback(startTime, key, entity));
    }

}