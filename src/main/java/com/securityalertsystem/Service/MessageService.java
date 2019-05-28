package com.securityalertsystem.Service;

import com.google.gson.Gson;
import com.securityalertsystem.Constants.Constants;
import com.securityalertsystem.entity.AlertMessage;
import com.securityalertsystem.entity.Client;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Math.sqrt;

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

    public void sendAlertNearbyforJson(String type,String happenTime,AlertSender alertSender, Long sendTime){ //Maybe 1-3 miles
        AlertMessage message = new AlertMessage();
        message.setMessageId(System.currentTimeMillis() + "$" + UUID.randomUUID().toString());
        message.setHappenTime(happenTime);
        message.setLocation("Within 3 miles");
        message.setType(type);
        message.setReceivedTime(sendTime);
        Gson gson = new Gson();
        String s = gson.toJson(message);
        try {
            alertSender.send1(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void sendAlertMidforJson(String type,String happenTime,AlertSender alertSender,long sendTime){ //Maybe 3-10 miles or further
        AlertMessage message = new AlertMessage();
        message.setMessageId(System.currentTimeMillis() + "$" + UUID.randomUUID().toString());
        message.setHappenTime(happenTime);
        message.setLocation("3-10 miles away");
        message.setType(type);
        message.setReceivedTime(sendTime);
        Gson gson = new Gson();
        String s = gson.toJson(message);
        try {
            alertSender.send2(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void sendAlertFarawayforJson(String type,String happenTime,AlertSender alertSender,long sendTime){ //Maybe 10 miles or further
        AlertMessage message = new AlertMessage();
        message.setMessageId(System.currentTimeMillis() + "$" + UUID.randomUUID().toString());
        message.setHappenTime(happenTime);
        message.setLocation("Further than 10 miles");
        message.setType(type);
        message.setReceivedTime(sendTime);
        Gson gson = new Gson();
        String s = gson.toJson(message);
        try {
            alertSender.send3(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public String transferMessage(int consumer, int priority, AlertMessage message){
//        System.err.println("----------received message-----------");
//        System.err.println("message ID: "+message.getMessageId());
//        message.setReceivedTime(System.currentTimeMillis()-message.getReceivedTime());
//        String result = consumer+" "+"priority="+priority+" "+
//                "MessageId: "+message.getMessageId()+" "+
//                "Location: "+message.getLocation()+" "+
//                "Emergency Type: "+message.getType()+" "+
//                "Happen Time: "+message.getHappenTime()+" ";
//        return result;
//    }
    public AlertMessage transferMessage(String topic,String message){
        //{"messageId":"1558071957842$cabcf621-1a58-4346-bacf-56292f2cc214","type":"flooding","location":"3-10 miles away","happenTime":"Thu May 16 22:45:33 PDT 2019","receivedTime":1558071957842}
        System.err.println("----------received message-----------");
        long curTime = System.currentTimeMillis();
        message = message.substring(1,message.length()-1);
        String[] elements = message.split(",");
        Map<String,String> map = new HashMap<>();
        for(String element:elements){
            String[] pair = element.split(":");
            map.put(pair[0],pair[1]);
        }
        System.err.println("message ID: "+map.get("\"messageId\""));
        System.err.println(topic);
        long gap = curTime-Long.valueOf(map.get("\"receivedTime\""));
        map.put("\"receivedTime\"",String.valueOf(gap));
        AlertMessage result = new AlertMessage();
        result.setType(map.get("\"type\""));
        result.setMessageId(map.get("\"messageId\""));
        result.setLocation(map.get("\"location\""));
        result.setHappenTime(map.get("\"happenTime\""));
        result.setReceivedTime(Long.valueOf(map.get("\"receivedTime\"")));

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
                distance = sqrt(Math.pow(latitude-client.getLocationx(),2)+
                        Math.pow(longitude-client.getLocationy(),2));
            }else{
                distance = sqrt(Math.pow(latitude-client.getAddressx(),2)+
                        Math.pow(longitude-client.getAddressy(),2));
            }

            if(distance<=15){
                group1.add(client.getClientId());
            }else if(distance>15 && distance<=20){
                group2.add(client.getClientId());
            }else{
                group3.add(client.getClientId());
            }
        }
    }
}
