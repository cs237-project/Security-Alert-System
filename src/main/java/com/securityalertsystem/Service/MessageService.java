package com.securityalertsystem.Service;

import com.securityalertsystem.Constants.Constants;
import com.securityalertsystem.activemq.controller.SenderController;
import com.securityalertsystem.entity.AlertMessage;
import com.securityalertsystem.entity.Client;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;
import java.util.UUID;

@Component
public class MessageService {
    public void sendAlertNearby(String type,String happenTime,AlertSender alertSender, Long sendTime){ //Maybe 1-3 miles
        AlertMessage message = new AlertMessage();
        message.setMessageId(System.currentTimeMillis() + "$" + UUID.randomUUID().toString());
        message.setHappenTime(happenTime);
        message.setLocation("Within 3 miles");
        message.setType(type);
        message.setReceivedTime(sendTime);
        try {
            alertSender.send1(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void sendAlertMid(String type,String happenTime,AlertSender alertSender,long sendTime){ //Maybe 3-10 miles or further
        AlertMessage message = new AlertMessage();
        message.setMessageId(System.currentTimeMillis() + "$" + UUID.randomUUID().toString());
        message.setHappenTime(happenTime);
        message.setLocation("3-10 miles away");
        message.setType(type);
        message.setReceivedTime(sendTime);
        try {
            alertSender.send2(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void sendAlertFaraway(String type,String happenTime,AlertSender alertSender,long sendTime){ //Maybe 10 miles or further
        AlertMessage message = new AlertMessage();
        message.setMessageId(System.currentTimeMillis() + "$" + UUID.randomUUID().toString());
        message.setHappenTime(happenTime);
        message.setLocation("Further than 10 miles");
        message.setType(type);
        message.setReceivedTime(sendTime);
        try {
            alertSender.send3(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String transferMessage(int consumer, int priority, AlertMessage message){
        System.err.println("----------received message-----------");
        System.err.println("message ID: "+message.getMessageId());
        message.setReceivedTime(System.currentTimeMillis()-message.getReceivedTime());
        String result = "<p>"+consumer+" "+"priority="+priority+" "+
                "MessageId: "+message.getMessageId()+" "+
                "Location: "+message.getLocation()+" "+
                "Emergency Type: "+message.getType()+" "+
                "Happen Time: "+message.getHappenTime()+" " +
                "Time gap of receiving message: "+message.getReceivedTime()+"</p>";
        return result;
    }

    public void calPriority(List<Client> clients,List<Integer> group1,List<Integer> group2, List<Integer> group3,
                            double latitude,double longitude,String type){
        boolean useCurLoc = type.equals(Constants.GUNSHOT)||
                type.equals(Constants.ROBBERY)||
                type.equals(Constants.SEXASSUALT);
        for(Client client:clients){
            double distance;
            if(useCurLoc){
                distance = Math.pow(latitude-client.getLocationx(),2)+
                        Math.pow(longitude-client.getLocationy(),2);
            }else{
                distance = Math.pow(latitude-client.getAddressx(),2)+
                        Math.pow(longitude-client.getAddressy(),2);
            }

            if(distance<=40){
                group1.add(client.getClientId());
            }else if(distance>40 && distance<=100){
                group2.add(client.getClientId());
            }else{
                group3.add(client.getClientId());
            }
        }
    }
}
