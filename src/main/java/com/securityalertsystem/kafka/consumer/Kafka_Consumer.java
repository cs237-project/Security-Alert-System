package com.securityalertsystem.kafka.consumer;


import com.securityalertsystem.entity.AlertMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public final class Kafka_Consumer {
    private final KafkaConsumer<String, AlertMessage> consumer1;
    private final KafkaConsumer<String, AlertMessage> consumer2;
    private final KafkaConsumer<String, AlertMessage> consumer3;
    @Value("${kafka.consumer.servers}")
    private String servers;
    @Value("${kafka.consumer.enable.auto.commit}")
    private boolean enableAutoCommit;
    @Value("${kafka.consumer.session.timeout}")
    private String sessionTimeout;
    @Value("${kafka.consumer.auto.commit.interval}")
    private String autoCommitInterval;
    @Value("${kafka.consumer.auto.offset.reset}")
    private String autoOffsetReset;
    @Value("${kafka.consumer.concurrency}")
    private int concurrency;
    private ExecutorService executorService;

    public Kafka_Consumer() {
        Map<String, Object> propsMap = new HashMap<>();
        propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        propsMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        propsMap.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, autoCommitInterval);
        propsMap.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeout);
        propsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        propsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        propsMap.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        consumer1 = new KafkaConsumer<>(propsMap);
        consumer1.subscribe(Arrays.asList("topic1"));
        consumer2 = new KafkaConsumer<>(propsMap);
        consumer2.subscribe(Arrays.asList("topic2"));
        consumer3 = new KafkaConsumer<>(propsMap);
        consumer3.subscribe(Arrays.asList("topic3"));
    }


    public void execute() {
        executorService = Executors.newFixedThreadPool(18);
        while (true) {
            Duration duration = Duration.ofMillis(100);
            ConsumerRecords<String, AlertMessage> records1 = consumer1.poll(duration);
            ConsumerRecords<String, AlertMessage> records2 = consumer1.poll(duration);
            ConsumerRecords<String, AlertMessage> records3 = consumer1.poll(duration);
            if (null != records1 || null != records2 || null != records3) {
                executorService.submit(new ConsumerThread(records1));
                executorService.submit(new ConsumerThread(records2));
                executorService.submit(new ConsumerThread(records3));
            }
        }
    }

    public void shutdown() {
        try {
            if (consumer1 != null ||consumer2 != null || consumer3 != null) {
                consumer1.close();
                consumer2.close();
                consumer3.close();
            }

            if (executorService != null) {
                executorService.shutdown();
            }
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                System.out.println("Timeout");
            }
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}