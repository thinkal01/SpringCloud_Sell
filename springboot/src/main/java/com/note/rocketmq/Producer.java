package com.note.rocketmq;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

public class Producer {

    public static void main(String[] args) throws MQClientException {
        /**
         * 一个应用创建一个Producer，由应用来维护此对象，可以设置为全局对象或单例
         * 注意：ProducerGroupName需要由应用来保证唯一
         * ProducerGroup这个概念发送普通消息时作用不大，但是发送分布式事务消息时比较关键，
         * 因为服务器会查这个Group下的任意一个Producer
         */
        DefaultMQProducer producer = new DefaultMQProducer("ProducerGroupName");

        /**
         * Producer对象在使用之前必须要调用start初始化，初始化一次即可
         * 注意：切记不可以在每次发送消息时，都调用start方法。
         */
        producer.setNamesrvAddr("192.168.106.101:9876;192.168.106.102:9876");

        /**
         * 默认情况下，一台服务器只能启动一个Producer或Consumer实例，所以如果需要在
         * 一台服务器启动多个实例，需要设置实例的名称。
         */
        producer.setInstanceName("producer");

        producer.start();

        /**
         * Producer对象可以发送多个topic，多个tag的消息。
         * 注意：send方法是同步调用，只要不抛异常就表示成功。但是发送成功也可会有多种状态，
         * 例如消息写入Master成功，但是Slave不成功，这种情况消息属于成功，但是对于个别应用如果对消息可靠性要求极高，
         * 需要对这种情况做处理。另外，消息可能会存在发送失败的情况，失败重试由应用来处理。
         */
        try {
            Message msg = new Message("TopicTest1",// topic
                    "TagA",// tag
                    "OrderID001",// key
                    ("Hello MetaQ").getBytes());// body
            SendResult sendResult = producer.send(msg);
            System.out.println(sendResult);

            msg = new Message("TopicTest2",// topic
                    "TagB",// tag
                    "OrderID0034",// key
                    ("Hello MetaQ").getBytes());// body
            sendResult = producer.send(msg);
            System.out.println(sendResult);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * 应用退出时，要调用shutdown来清理资源，关闭网络连接，从MetaQ服务器上注销自己
         * 注意：建议应用在JBoss、Tomcat等容器的退出钩子里调用shutdown方法。
         */
        producer.shutdown();
    }
}