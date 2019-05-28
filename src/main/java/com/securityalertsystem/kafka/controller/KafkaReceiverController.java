package com.securityalertsystem.kafka.controller;


import com.securityalertsystem.Service.MessageService;
import com.securityalertsystem.common.Response;
import com.securityalertsystem.entity.AlertMessage;
import com.securityalertsystem.entity.Client;
import com.securityalertsystem.kafka.consumer.ConsumerGroup;
import com.securityalertsystem.repository.ClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RestController
@RequestMapping("/kafka/messageReceiver")
public class KafkaReceiverController {
    @Autowired
    ClientRepository clientRepository;

    @Autowired
    MessageService messageService;

    public static Map<Integer,Long> averageTime = new ConcurrentHashMap<>();
    public static List<AlertMessage> receivedMessages = new ArrayList<>();
    private int[] size_of_queue = new int[3];

    public static int consumerCount = 0;


    @RequestMapping("/createQueue")
    public Response listener() {
        List<Client> clients = clientRepository.findAll();

        if(clients.size()==0){
            return Response.createByErrorMessage("Need get clients information. Please input url \"/getClients\"");
        }

        if(KafkaSenderController.latitude==0 && KafkaSenderController.longitude==0){
            return Response.createByErrorMessage("There is no Message");
        }


        List<Integer> high_client = new ArrayList<>(),mid_client=new ArrayList<>(),low_client= new ArrayList<>();
        messageService.calPriority(clients,high_client,mid_client,low_client,
                KafkaSenderController.latitude, KafkaSenderController.longitude, KafkaSenderController.TYPE);
        size_of_queue[0] = high_client.size();
        size_of_queue[1] = mid_client.size();
        size_of_queue[2] = low_client.size();


        String brokerlist = "localhost:9092";
        ConsumerGroup consumerGroup1 = new ConsumerGroup(high_client.size(),0,"topic1",brokerlist);
        ConsumerGroup consumerGroup2 = new ConsumerGroup(mid_client.size(),high_client.size(),"topic2",brokerlist);
        ConsumerGroup consumerGroup3 = new ConsumerGroup(low_client.size(),high_client.size()+mid_client.size()
                ,"topic3",brokerlist);

        consumerGroup1.execute();

        consumerGroup2.execute();

        consumerGroup3.execute();



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
        System.out.println(consumerCount);
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
            averageTime.put(p,averageTime.get(p)/size_of_queue[p-1]);
        }
        return Response.createBySuccess("Get test result successfully",averageTime);
    }

}






