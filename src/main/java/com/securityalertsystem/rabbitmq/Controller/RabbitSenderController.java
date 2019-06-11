package com.securityalertsystem.rabbitmq.Controller;



import com.securityalertsystem.Service.MessageService;
import com.securityalertsystem.common.Response;
import com.securityalertsystem.entity.AlertMessage;
import com.securityalertsystem.rabbitmq.producer.RabbitAlertSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/rabbitmq/messageSender")
public class RabbitSenderController {

    static String TYPE = "";
    static double latitude;
    static double longitude;

    @Autowired
    private RabbitAlertSender alertSender;

    @Autowired
    public MessageService messageService;


    private static String happenTime = new Date().toString();
    private static long sendTime;



    @RequestMapping(value="/send")
    public Response sendAlerts(){
        sendTime = System.currentTimeMillis();
        RabbitReceiverController.averageTime=new HashMap<>();
        RabbitReceiverController.receivedMessages = new ArrayList<>();
        messageService.sendAlertNearbyforJson(TYPE,happenTime,alertSender,sendTime);
        messageService.sendAlertMidforJson(TYPE,happenTime,alertSender,sendTime);
        messageService.sendAlertFarawayforJson(TYPE,happenTime,alertSender,sendTime);
        return Response.createBySuccessMessage("Messages Sent Successfully");
    }
    @RequestMapping(value="/create/{type}")
    public Response createAlerts(@PathVariable(name = "type") String type){
        TYPE = type;
        latitude = 50+Math.random()*30;
        longitude = 50+Math.random()*30;
        List<Double> location = Arrays.asList(latitude,longitude);
        return Response.createBySuccess("Messages Created Successfully",location);
    }

}
