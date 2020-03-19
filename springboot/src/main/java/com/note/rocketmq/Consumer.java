package com.note.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;

public class Consumer {

    /**
     * PushConsumer用法，感觉消息从RocketMQ服务器推到应用客户端。
     * 实际PushConsumer内部是使用长轮询Pull方法从Broker拉消息，然后再回调用户Listener方法。
     */
    public static void main(String[] args) throws MQClientException {
        /**
         * 一个应用创建一个Consumer，由应用来维护此对象，可以设置为全局对象或者单例
         * 注意：ConsumerGroupName需要由应用来保证唯一
         */
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("ConsumerGroupName");
        consumer.setNamesrvAddr("192.168.106.101:9876;192.168.106.102:9876");

        /**
         * 订阅指定topic下tags分别等于TagA或tagC或TagD
         */
        consumer.subscribe("TopicTest1", "TagA || TagC || TagD");

        /**
         * 订阅指定topic下所有消息<br>
         * 注意：一个consumer对象可以订阅多个topic
         */
        consumer.subscribe("TopicTest2", "*");

        /**
         * 设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费
         * 如果非第一次启动，那么按照上次消费的位置继续消费
         */
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            System.out.println(Thread.currentThread().getName() + " Receive New Messages:" + msgs);

            MessageExt msg = msgs.get(0);
            System.out.println(new String(msg.getBody()));
            if (msg.getTopic().equals("TopicTest1")) {
                //执行TopicTest1的消费逻辑
                if (msg.getTags() != null && msg.getTags().equals("TagA")) {
                    //执行TagA的消费
                    System.out.println("执行TagA的消费");
                } else if (msg.getTags() != null && msg.getTags().equals("TagC")) {
                    //执行TagC的消费
                    System.out.println("执行TagC的消费");
                } else if (msg.getTags() != null && msg.getTags().equals("TagD")) {
                    //执行TagD的消费
                    System.out.println("执行TagD的消费");
                }
            } else if (msg.getTopic().equals("TopicTest2")) {
                //执行TopicTest2的消费逻辑
                System.out.println("执行TopicTest2的消费逻辑");
            } else if (msg.getTopic().equals("TopicTest3")) {
                System.out.println("执行TopicTest3的消费逻辑");
            }

            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });

        /**
         * Consumer对象在使用之前必须调用start初始化，初始化一次即可
         */
        consumer.start();
        System.out.println("Consumer Started.");
    }
}