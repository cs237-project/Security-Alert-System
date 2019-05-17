package com.securityalertsystem.kafka.consumer;

import java.util.ArrayList;
import java.util.List;

public class ConsumerGroup {

    private List<ConsumerRunnable> consumers;

    public ConsumerGroup(int consumerNum, int groupoffset, String topic, String brokerList){
        consumers = new ArrayList<>(consumerNum);
        for(int i=0;i<consumerNum;++i){
            ConsumerRunnable consumerThread = new ConsumerRunnable(brokerList, "group"+groupoffset+i, topic);
            consumers.add(consumerThread);
        }
    }

    public void execute(){
        for(ConsumerRunnable task:consumers){
            new Thread(task).start();
        }
    }
}
