package com.securityalertsystem.activemq.controller;

import com.securityalertsystem.Service.MessageService;
import com.securityalertsystem.activemq.producer.ActiveAlertSender;
import com.securityalertsystem.Service.MessageService.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/activemq/messageSender")
public class SenderController{

    static String TYPE = "";
    static double latitude;
    static double longitude;

    @Autowired
    private ActiveAlertSender alertSender;
    @Autowired
    public MessageService messageService;



    private static String happenTime = new Date().toString();
    private static long sendTime;

    @RequestMapping("/send")
    public void sendMsg(String msg) {

        sendTime = System.currentTimeMillis();
        messageService.sendAlertNearby(TYPE,happenTime,alertSender,sendTime);
        messageService.sendAlertMid(TYPE,happenTime,alertSender,sendTime);
        messageService.sendAlertFaraway(TYPE,happenTime,alertSender,sendTime);
        System.out.println("msg发送成功");
    }

    @RequestMapping(value="/create/{type}")
    public String createAlerts(@PathVariable(name = "type") String type){
        TYPE = type;
        latitude = 45+Math.random()*30;
        longitude = 40+Math.random()*30;
        return "Messages Created Successfully";
    }


}
