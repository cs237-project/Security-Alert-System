package com.securityalertsystem.kafka.controller;


import com.securityalertsystem.Service.MessageService;
import com.securityalertsystem.entity.Client;
import com.securityalertsystem.kafka.consumer.ConsumerGroup;
import com.securityalertsystem.repository.ClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Slf4j
@Component
@RestController
@RequestMapping("/kafka/messageReceiver")
public class KafkaReceiverController {
    @Autowired
    ClientRepository clientRepository;

    @Autowired
    MessageService messageService;

    public static List<String> receivedMessages = new ArrayList<>();


    @RequestMapping("/createQueue")
    public String listener() {
        List<Client> clients = clientRepository.findAll();

        if(clients.size()==0){
            return "Please add clients";
        }


        List<Integer> high_client = new ArrayList<>(),mid_client=new ArrayList<>(),low_client= new ArrayList<>();
        messageService.calPriority(clients,high_client,mid_client,low_client,
                KafkaSenderController.latitude, KafkaSenderController.longitude, KafkaSenderController.TYPE);

        String brokerlist = "localhost:9092";
        ConsumerGroup consumerGroup1 = new ConsumerGroup(high_client.size(),0,"topic1",brokerlist);
        ConsumerGroup consumerGroup2 = new ConsumerGroup(mid_client.size(),high_client.size(),"topic2",brokerlist);
        ConsumerGroup consumerGroup3 = new ConsumerGroup(low_client.size(),high_client.size()+mid_client.size()
                ,"topic3",brokerlist);

        consumerGroup1.execute();
        consumerGroup2.execute();
        consumerGroup3.execute();
        return "create queue succeed";
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






