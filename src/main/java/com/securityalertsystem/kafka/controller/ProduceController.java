package com.securityalertsystem.kafka.controller;


import com.google.gson.Gson;
import com.securityalertsystem.Service.MessageService;
import com.securityalertsystem.entity.AlertMessage;
import com.securityalertsystem.common.ErrorCode;
import com.securityalertsystem.common.Response;
import com.securityalertsystem.kafka.producer.SimpleProducer;
import com.securityalertsystem.rabbitmq.producer.RabbitAlertSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Date;


@Slf4j
@RestController
@RequestMapping("/kafka")
public class ProduceController {
    @Autowired
    private SimpleProducer simpleProducer;

    @Autowired
    public MessageService messageService;


    static String TYPE = "";
    static double latitude;
    static double longitude;

    private static String happenTime = new Date().toString();
    private static long sendTime;


    private Gson gson = new Gson();


    @RequestMapping(value = "/send", method = RequestMethod.POST, produces = {"application/json"})
    public Response sendKafka(@RequestBody AlertMessage message) {
        try {
            log.info("kafka message={}", gson.toJson(message));
            sendTime = System.currentTimeMillis();
            messageService.sendAlertNearby(TYPE,happenTime,simpleProducer,sendTime);
            messageService.sendAlertMid(TYPE,happenTime,simpleProducer,sendTime);
            messageService.sendAlertFaraway(TYPE,happenTime,simpleProducer,sendTime);
            log.info("send kafka successfully.");
            return new Response(ErrorCode.SUCCESS, "send kafka succeed");
        } catch (Exception e) {
            log.error("send kafka fail", e);
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