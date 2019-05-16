package com.securityalertsystem.activemq.controller;

import com.securityalertsystem.Service.MessageService;
import com.securityalertsystem.entity.AlertMessage;
import com.securityalertsystem.entity.Client;
import com.securityalertsystem.repository.ClientRepository;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.ArrayList;
import java.util.List;



@RestController
@RequestMapping("/activemq/messageReceiver")
public class ActiveReceiverController {
    private static final String url = "tcp://127.0.0.1:61616";


    @Autowired
    ClientRepository clientRepository;

    @Autowired
    MessageService messageService;


    private List<String> receivedMessages = new ArrayList<>();
    private List<Integer> high_client = new ArrayList<>();
    private List<Integer> mid_client = new ArrayList<>();
    private List<Integer> low_client = new ArrayList<>();

        @RequestMapping("/createQueue")
        public String createConsumer(String queueName) throws Exception{

            List<Client> clients = clientRepository.findAll();
            if(clients.size()==0){
                return "Need get clients information. Please input url \"/getClients\"";
            }
            if(ActiveSenderController.TYPE.equals("")){
                return "There is no Message";
            }
            messageService.calPriority(clients,high_client,mid_client,low_client,
                    ActiveSenderController.latitude, ActiveSenderController.longitude, ActiveSenderController.TYPE);
            for(int id:high_client){
                try {
                    onAlertMessage(id,0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for(int id:mid_client){
                try {
                    onAlertMessage(id,1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for(int id:low_client){
                try {
                    onAlertMessage(id,2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return "Create Queue Succeed";
        }


    private void onAlertMessage(int clientId,int priority) throws Exception{
        // ConnectionFactory
        ActiveMQConnectionFactory connectionFactory=new ActiveMQConnectionFactory(url);
        connectionFactory.setTrustAllPackages(true);
        // Connection
        Connection connection = connectionFactory.createConnection();

        // Start connection
        connection.start();
        // Create session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Create destination
        Destination destination=session.createQueue("queue"+priority);
        // Create consumer
        MessageConsumer consumer=session.createConsumer(destination);

        // Create a listener
        consumer.setMessageListener(new MessageListener() {
            public void onMessage(Message message) {
                ActiveMQObjectMessage objectMessage = (ActiveMQObjectMessage) message;
                try{
                    AlertMessage alertMessage = (AlertMessage)objectMessage.getObject();
                    receivedMessages.add(messageService.transferMessage(clientId,priority,alertMessage));
                } catch (JMSException e){
                    e.printStackTrace();
                }
            }
        });

    }

    @RequestMapping("/getMsg")
    public String getMsg(){
            StringBuilder sb = new StringBuilder();

        if(receivedMessages.size()>0){
            for(String receivedMessage:receivedMessages){
                sb.append(receivedMessage);
            }
        }
        return sb.toString();
    }

}
