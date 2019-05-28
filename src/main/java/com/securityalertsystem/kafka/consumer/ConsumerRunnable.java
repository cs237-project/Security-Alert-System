package com.securityalertsystem.kafka.consumer;

import com.securityalertsystem.Service.MessageService;
import com.securityalertsystem.entity.AlertMessage;
import com.securityalertsystem.kafka.controller.KafkaReceiverController;
import com.securityalertsystem.kafka.controller.KafkaSenderController;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;


import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ConsumerRunnable implements Runnable{




    MessageService messageService = new MessageService();
    private final KafkaConsumer<String, String> consumer;

    public ConsumerRunnable(String brokerList, String groupId, String topic) {
         Properties props = new Properties();
        props.put("bootstrap.servers", brokerList);
        props.put("group.id", groupId);
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        this.consumer = new KafkaConsumer<>(props);
        //System.out.println("creating consumer");
        consumer.subscribe(Arrays.asList(topic));
   }

     @Override
     public void run() {
        boolean flag = false;

        KafkaReceiverController.consumerCount++;
        while (true) {
            Duration duration = Duration.ofMillis(1000);
            ConsumerRecords<String, String> records = consumer.poll(duration);
            //System.out.println(records.count());
            for (ConsumerRecord<String, String> record : records) {
//                System.out.println(Thread.currentThread().getName() + " consumed " + record.partition() +
//                         "th message with offset: " + record.offset());
//                receiverController.receivedMessages.add(messageService.transferMessage(0,
//                        0,record.value()));
                String topic = record.topic();
                System.out.println("Topic:"+topic);
                AlertMessage message = messageService.transferMessage(topic,record.value());
                KafkaReceiverController.receivedMessages.add(message);
                int priority = Integer.valueOf(topic.substring(record.topic().length()-1));
                if(!KafkaReceiverController.averageTime.containsKey(priority)){
                    KafkaReceiverController.averageTime.put(priority,message.getReceivedTime());
                }else{
                    long prev = KafkaReceiverController.averageTime.get(priority);
                    KafkaReceiverController.averageTime.put(priority,prev+message.getReceivedTime());
                }
                flag = true;
                break;
            }
            if(flag){
                consumer.unsubscribe();
                consumer.close();
                break;
            }
         }
    }
}
