package com.note.rocketmq;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

import java.util.UUID;

public class ProducerTest {
    private static DefaultMQProducer producer = null;

    public static void main(String[] args) {
        try {
            ProducerStart();
            for (int i = 0; i < 10; i++) {
                String msg = "hello rocketmq " + i + "----" + UUID.randomUUID().toString();
                SendMessage("qch_20170706", msg);
                System.out.print(msg + "\n");
            }
        } finally {
            // 停止
            producer.shutdown();
        }
    }

    private static boolean ProducerStart() {
        producer = new DefaultMQProducer("pro_qch_test");
        producer.setNamesrvAddr("192.168.106.101:9876;192.168.106.102:9876");
        producer.setInstanceName(UUID.randomUUID().toString());

        try {
            producer.start();
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private static boolean SendMessage(String topic, String str) {
        Message msg = new Message(topic, str.getBytes());
        try {
            producer.send(msg);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}