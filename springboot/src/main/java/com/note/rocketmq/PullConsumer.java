package com.note.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.PullResult;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PullConsumer {
    // 每个队列消费的offset
    private static final Map<MessageQueue, Long> OFFSET_TABLE = new HashMap<>();

    public static void main(String[] args) {
        DefaultMQPullConsumer consumer = new DefaultMQPullConsumer("consumerGroup");
        consumer.setInstanceName(UUID.randomUUID().toString());
        // 广播
        consumer.setMessageModel(MessageModel.BROADCASTING);
        consumer.setNamesrvAddr("192.168.106.101:9876;192.168.106.102:9876");

        try {
            consumer.start();
            Set<MessageQueue> mqs = consumer.fetchSubscribeMessageQueues("qch_20170706");
            for (MessageQueue mq : mqs) {
                System.out.println("Consume from the queue: " + mq + "%n");
                pullMessage(consumer, mq);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    从mq中拉取消息
     */
    private static void pullMessage(DefaultMQPullConsumer consumer, MessageQueue mq) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        while (true) {
            PullResult pullResult = consumer.pullBlockIfNotFound(mq, null, getMessageQueueOffset(mq), 32);
            System.out.printf("%s%n", pullResult);
            putMessageQueueOffset(mq, pullResult.getNextBeginOffset());
            switch (pullResult.getPullStatus()) {
                case FOUND:
                case NO_MATCHED_MSG:
                case OFFSET_ILLEGAL:
                    break;
                case NO_NEW_MSG:
                    return;
                default:
                    break;
            }
        }
    }

    private static long getMessageQueueOffset(MessageQueue mq) {
        Long offset = OFFSET_TABLE.get(mq);
        if (offset != null)
            return offset;
        return 0;
    }

    private static void putMessageQueueOffset(MessageQueue mq, long offset) {
        OFFSET_TABLE.put(mq, offset);
    }
}