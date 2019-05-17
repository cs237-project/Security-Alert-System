package com.securityalertsystem.kafka.consumer;

import com.securityalertsystem.Service.MessageService;
import com.securityalertsystem.entity.AlertMessage;
import com.securityalertsystem.kafka.controller.KafkaReceiverController;
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
        props.put("enable.auto.commit", "true");        //本例使用自动提交位移
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        this.consumer = new KafkaConsumer<>(props);
        System.out.println("creating consumer");
         consumer.subscribe(Arrays.asList(topic));   // 本例使用分区副本自动分配策略
   }

     @Override
     public void run() {

        while (true) {
            Duration duration = Duration.ofMillis(200);
            ConsumerRecords<String, String> records = consumer.poll(duration);   // 本例使用200ms作为获取超时时间
            for (ConsumerRecord<String, String> record : records) {
                // 这里面写处理消息的逻辑，本例中只是简单地打印消息
//                System.out.println(Thread.currentThread().getName() + " consumed " + record.partition() +
//                         "th message with offset: " + record.offset());
//                receiverController.receivedMessages.add(messageService.transferMessage(0,
//                        0,record.value()));
                KafkaReceiverController.receivedMessages.add(messageService.transferMessage(record.topic(),record.value()));
            }
         }
    }
}
