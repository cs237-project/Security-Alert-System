package com.securityalertsystem.rabbitmq.Controller;


import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.securityalertsystem.Service.MessageService;
import com.securityalertsystem.common.Response;
import com.securityalertsystem.entity.AlertMessage;
import com.securityalertsystem.entity.Client;
import com.securityalertsystem.repository.ClientRepository;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private Map<Integer,Long> averageTime = new HashMap<>();
    private List<AlertMessage> receivedMessages = new ArrayList<>();
    private List<Integer> high_client = new ArrayList<>();
    private List<Integer> mid_client = new ArrayList<>();
    private List<Integer> low_client = new ArrayList<>();
    private  int[] size_of_queue = new int[3];


    private void onAlertMessage(String exchangeName,int clientId,int priority) throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        String queueName = "queue"+clientId;
        channel.exchangeDeclare(exchangeName,"fanout",true);
        channel.queueDeclare(queueName, true, false, true, null); //durable, automatically deleted
        channel.queueBind(queueName,exchangeName,"alert.abc");

        DeliverCallback deliverCallback = (consumerTag, delivery)->{
            String message =  new String(delivery.getBody(), "UTF-8");

            Gson gson = new Gson();
            AlertMessage alertMessage = gson.fromJson(message,AlertMessage.class);
            long timegap = System.currentTimeMillis()-alertMessage.getReceivedTime();
            if(!averageTime.containsKey(priority)){
                averageTime.put(priority,timegap);
            }else{
                long prev = averageTime.get(priority);
                averageTime.put(priority,prev+timegap);
            }
            receivedMessages.add(alertMessage);
        };
        channel.basicConsume(queueName,true,deliverCallback,consumerTag->{});

    }


    @RequestMapping("/createQueue")
    public Response createQueue(){
        List<Client> clients = clientRepository.findAll();
        if(clients.size()==0){
            return Response.createByErrorMessage("Need get clients information. Please input url \"/getClients\"");
        }
        if(TYPE.equals("")){
            return Response.createByErrorMessage("There is no Message");
        }
        messageService.calPriority(clients,high_client,mid_client,low_client,
               latitude, longitude, TYPE);
        size_of_queue[0] = high_client.size();
        size_of_queue[1] = mid_client.size();
        size_of_queue[2] = low_client.size();
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
        return Response.createBySuccessMessage("Create Queue Succeed");
    }

    @RequestMapping("/getMsg")
    public Response getMsg(){
//        StringBuilder sb = new StringBuilder();
//
//        if(receivedMessages.size()>0){
//            for(String receivedMessage:receivedMessages){
//                sb.append(receivedMessage);
//            }
//        }
//        return sb.toString();
        if(receivedMessages.size()==0){
            return Response.createByErrorMessage("There is no message received");
        }
        return Response.createBySuccess("Get messages successfully",receivedMessages);
    }

    @RequestMapping("/getResult")
    public Response getResult(){
        if(receivedMessages.size()==0){
            return Response.createByErrorMessage("There is no result");
        }
        for(int p:averageTime.keySet()){
            averageTime.put(p,averageTime.get(p)/size_of_queue[p]);
        }
        return Response.createBySuccess("Get test result successfully",averageTime);
    }

}
