package com.codecat.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class CustomProducer {
    public static void main(String[] args) {
        Properties props = new Properties();

        // kafka集群，broker-list
        props.put("bootstrap.servers", "hadoop102:9092");
        // props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "hadoop:9092");

        props.put("acks", "all");

        // 重试次数
        props.put("retries", 1);

        // 批次大小
        props.put("batch.size", 16384);

        // 等待时间
        props.put("linger.ms", 1);

        // RecordAccumulator缓冲区大小
        props.put("buffer.memory", 33554432);

        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // 创建生产者对象
        Producer<String, String> producer = new KafkaProducer<>(props);

        // 发送数据
        for (int i = 0; i < 10; i++) {
            producer.send(new ProducerRecord<String, String>("first", "codecat--"+i));
        }

        // 关闭资源
        producer.close();
    }
}
