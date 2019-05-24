package com.securityalertsystem.kafka.controller;


import com.google.gson.Gson;
import com.securityalertsystem.Service.MessageService;
import com.securityalertsystem.entity.AlertMessage;
import com.securityalertsystem.common.ErrorCode;
import com.securityalertsystem.common.Response;
import com.securityalertsystem.entity.Client;
import com.securityalertsystem.kafka.producer.SimpleProducer;
import com.securityalertsystem.rabbitmq.producer.RabbitAlertSender;
import com.securityalertsystem.repository.ClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/kafka/messageSender")
public class KafkaSenderController {
    @Autowired
    private SimpleProducer simpleProducer;

    @Autowired
    public MessageService messageService;
    @Autowired
    ClientRepository clientRepository;


    static String TYPE = "";
    static double latitude = 0;
    static double longitude = 0;

    private static String happenTime = new Date().toString();
    private static long sendTime;


    private Gson gson = new Gson();


    @RequestMapping(value = "/send", produces = {"application/json"})
    public Response sendKafka() {
        if(TYPE.equals("")){
            return new Response(ErrorCode.EXCEPTION,"Please create message");
        }

        List<Client> clients = clientRepository.findAll();
        if(clients.size()==0){
            return new Response(ErrorCode.EXCEPTION,"Please add clients");
        }
        List<Integer> group1=new ArrayList<>(),group2 = new ArrayList<>(),group3 = new ArrayList<>();
        messageService.calPriority(clients,group1,group2,group3,latitude,longitude,TYPE);
        int len1 = group1.size(),len2 = group2.size(),len3=group3.size();
        sendTime = System.currentTimeMillis();

        try {
            for (int i = 0; i < len1; i++) {
                messageService.sendAlertNearby(TYPE, happenTime, simpleProducer, sendTime);
            }
            for (int i = 0; i < len2; i++) {
                messageService.sendAlertMid(TYPE, happenTime, simpleProducer, sendTime);
            }
            for (int i = 0; i < len3; i++) {
                messageService.sendAlertFaraway(TYPE, happenTime, simpleProducer, sendTime);
            }
            KafkaReceiverController.receivedMessages = new ArrayList<>();
            return new Response(ErrorCode.SUCCESS, "send kafka succeed");
        }catch (Exception e){
            return new Response(ErrorCode.EXCEPTION, "send kafka fail");
        }

    }

    @RequestMapping(value="/create/{type}")
    public String createAlerts(@PathVariable(name = "type") String type){
        TYPE = type;
        latitude = 45+Math.random()*30;
        longitude = 40+Math.random()*30;
        return "Messages Created Successfully";
    }

}