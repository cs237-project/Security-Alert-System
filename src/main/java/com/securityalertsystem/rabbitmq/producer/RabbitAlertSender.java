package com.securityalertsystem.rabbitmq.producer;

import com.securityalertsystem.Service.AlertSender;
import com.securityalertsystem.entity.AlertMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RabbitAlertSender implements AlertSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void send1(AlertMessage message) {

    }

    public void send1(String message) {

        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend("alert-exchange0",
                "alert.abc",
                message,
                correlationData);
    }

    @Override
    public void send2(AlertMessage message) {

    }

    public void send2(String message) {

        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend("alert-exchange1",
                "alert.abc",
                message,
                correlationData);
    }

    @Override
    public void send3(AlertMessage message) {

    }

    public void send3(String message){

        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend("alert-exchange2",
                "alert.abc",
                message,
                correlationData);
    }
}
