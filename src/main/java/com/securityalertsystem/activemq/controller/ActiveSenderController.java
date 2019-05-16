package com.securityalertsystem.activemq.controller;

import com.securityalertsystem.Service.MessageService;
import com.securityalertsystem.activemq.producer.ActiveAlertSender;
import com.securityalertsystem.Service.MessageService.*;
import com.securityalertsystem.entity.Client;
import com.securityalertsystem.repository.ClientRepository;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/activemq/messageSender")
public class ActiveSenderController {

    static String TYPE = "";
    static double latitude;
    static double longitude;

    @Autowired
    private ActiveAlertSender alertSender;
    @Autowired
    public MessageService messageService;
    @Autowired
    ClientRepository clientRepository;



    private static String happenTime = new Date().toString();
    private static long sendTime;

    @RequestMapping("/send")
    public String sendMsg(String msg) {
        if(TYPE.equals("")){
            return "There is no message. Please create one";
        }
        List<Client> clients = clientRepository.findAll();
        if(clients.size()==0){
            return "Please add clients";
        }
        List<Integer> group1=new ArrayList<>(),group2 = new ArrayList<>(),group3 = new ArrayList<>();
        messageService.calPriority(clients,group1,group2,group3,latitude,longitude,TYPE);
        int len1 = group1.size(),len2 = group2.size(),len3=group3.size();
        sendTime = System.currentTimeMillis();
        for(int i=0;i<len1;i++){
            messageService.sendAlertNearby(TYPE,happenTime,alertSender,sendTime);
        }
        for(int i=0;i<len2;i++){
            messageService.sendAlertMid(TYPE,happenTime,alertSender,sendTime);
        }
        for(int i=0;i<len3;i++){
            messageService.sendAlertFaraway(TYPE,happenTime,alertSender,sendTime);
        }
        return "msg发送成功";
    }

    @RequestMapping(value="/create/{type}")
    public String createAlerts(@PathVariable(name = "type") String type){
        TYPE = type;
        latitude = 45+Math.random()*30;
        longitude = 40+Math.random()*30;
        return "Messages Created Successfully";
    }


}
