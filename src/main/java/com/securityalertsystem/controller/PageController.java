package com.securityalertsystem.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {
    @RequestMapping("/index")
    public String indexPage(){
        return "index";
    }
    @RequestMapping("kafka")
    public String kafkaPage(){
        return "kafka";
    }
    @RequestMapping("activemq")
    public String activemqPage(){
        return "activemq";
    }
    @RequestMapping("rabbitmq")
    public String rabbitmqPage(){
        return "rabbitmq";
    }

}