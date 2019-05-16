package com.securityalertsystem.kafka.consumer;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

/**
 *    线程池做业务处理，   将kakfa接收消息和业务分离开来
 */
@Slf4j
class ConsumerThread implements Runnable {
    private ConsumerRecords<String, String> records;

    private final Gson gson = new Gson();

    public ConsumerThread(ConsumerRecords<String, String> records) {
        this.records = records;
    }

    @Override
    public void run() {
        for (ConsumerRecord<String, String> record : records) {
            log.info(gson.toJson(record));
            System.out.println("当前线程:" + Thread.currentThread() + ","
                    + "偏移量:" + record.offset() + "," + "主题:"
                    + record.topic() + "," + "分区:" + record.partition()
                    + "," + "获取的消息:" + record.value());

        }
    }
}