package com.securityalertsystem.rabbitmq.Controller;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.securityalertsystem.Service.MessageService;
import com.securityalertsystem.entity.AlertMessage;
import com.securityalertsystem.entity.Client;
import com.securityalertsystem.repository.ClientRepository;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static com.securityalertsystem.rabbitmq.Controller.RabbitSenderController.TYPE;
import static com.securityalertsystem.rabbitmq.Controller.RabbitSenderController.latitude;
import static com.securityalertsystem.rabbitmq.Controller.RabbitSenderController.longitude;


@Component
@RestController
@RequestMapping("/rabbitmq/messageReceiver")
public class RabbitReceiverController {
    @Autowired
    ClientRepository clientRepository;

    @Autowired
    MessageService messageService;


    private List<String> receivedMessages = new ArrayList<>();
    private List<Integer> high_client = new ArrayList<>();
    private List<Integer> mid_client = new ArrayList<>();
    private List<Integer> low_client = new ArrayList<>();


    private void onAlertMessage(String exchangeName,int clientId,int priority) throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        String queueName = "queue"+clientId;
        channel.exchangeDeclare(exchangeName,"fanout",true);
        channel.queueDeclare(queueName, true, false, true, null); //durable, automatically deleted
        channel.queueBind(queueName,exchangeName,"");

        DeliverCallback deliverCallback = (consumerTag, delivery)->{
            AlertMessage message =  SerializationUtils.deserialize(delivery.getBody());

            receivedMessages.add(messageService.transferMessage(clientId,priority,message));
        };
        channel.basicConsume(queueName,true,deliverCallback,consumerTag->{});

    }


    @RequestMapping("/createQueue")
    public String createQueue(){
        List<Client> clients = clientRepository.findAll();
        if(clients.size()==0){
            return "Need get clients information. Please input url \"/getClients\"";
        }
        if(TYPE.equals("")){
            return "There is no Message";
        }
        messageService.calPriority(clients,high_client,mid_client,low_client,
               latitude, longitude, TYPE);
        for(int id:high_client){
            try {
                onAlertMessage("alert-exchange0",id,0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for(int id:mid_client){
            try {
                onAlertMessage("alert-exchange1",id,1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for(int id:low_client){
            try {
                onAlertMessage("alert-exchange2",id,2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "Queue Created!";
    }

    @RequestMapping("/getMsg")
    public List<String> getMsg(){
//        StringBuilder sb = new StringBuilder();
//
//        if(receivedMessages.size()>0){
//            for(String receivedMessage:receivedMessages){
//                sb.append(receivedMessage);
//            }
//        }
//        return sb.toString();
        return receivedMessages;
    }
}
