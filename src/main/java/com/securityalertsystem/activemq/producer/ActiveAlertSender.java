package com.securityalertsystem.activemq.producer;

import com.securityalertsystem.Service.AlertSender;
import com.securityalertsystem.entity.AlertMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class ActiveAlertSender implements AlertSender {
    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    public void send1(AlertMessage message){
        jmsMessagingTemplate.convertAndSend("queue0", message);
    }

    @Override
    public void send1(String message) {

    }

    public void send2(AlertMessage message) {
        jmsMessagingTemplate.convertAndSend("queue1", message);
    }

    @Override
    public void send2(String message) {

    }

    public void send3(AlertMessage message) {
        jmsMessagingTemplate.convertAndSend("queue2", message);
    }

    @Override
    public void send3(String message) {

    }
}
