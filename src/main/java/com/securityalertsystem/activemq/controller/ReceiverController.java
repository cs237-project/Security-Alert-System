package com.securityalertsystem.activemq.controller;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.web.bind.annotation.RestController;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;


@RestController
public class ReceiverController {
        private static final String url = "tcp://127.0.0.1:61616";

        public void createConsumer(String queueName) throws Exception{
            // ConnectionFactory
            ConnectionFactory connectionFactory=new ActiveMQConnectionFactory(url);
            // Connection
            Connection connection = connectionFactory.createConnection();
            // Start connection
            connection.start();
            // Create session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // Create destination
            Destination destination=session.createQueue(queueName);
            // Create consumer
            MessageConsumer consumer=session.createConsumer(destination);
            // Create a listener
            consumer.setMessageListener(new MessageListener() {
                public void onMessage(Message message) {
                    TextMessage textMessage=(TextMessage)message;
                    try{
                        System.out.println("message received"+textMessage.getText());
                    } catch (JMSException e){
                        e.printStackTrace();
                    }
                }
            });
        }

}
