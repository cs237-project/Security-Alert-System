package com.securityalertsystem.activemq.controller;

import com.securityalertsystem.Service.MessageService;
import com.securityalertsystem.activemq.producer.ActiveAlertSender;
import com.securityalertsystem.Service.MessageService.*;
import com.securityalertsystem.common.Response;
import com.securityalertsystem.entity.Client;
import com.securityalertsystem.repository.ClientRepository;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

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
    public Response sendMsg(String msg) {
        if(TYPE.equals("")){
            return Response.createByErrorMessage("There is no Message");
        }
        List<Client> clients = clientRepository.findAll();
        if(clients.size()==0){
            return Response.createByErrorMessage("Need get clients information. Please input url \"/getClients\"");
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
        return Response.createBySuccessMessage("Message Sent Successfully");
    }

    @RequestMapping(value="/create/{type}")
    public Response createAlerts(@PathVariable(name = "type") String type){
        TYPE = type;
        latitude = 45+Math.random()*30;
        longitude = 40+Math.random()*30;
        List<Double> location = Arrays.asList(latitude,longitude);
        return Response.createBySuccess("Messages Created Successfully",location);
    }


}
