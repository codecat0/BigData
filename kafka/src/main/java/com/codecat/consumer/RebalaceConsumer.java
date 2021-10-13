package com.codecat.consumer;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;

import java.util.*;

public class RebalaceConsumer {
    private static Map<TopicPartition, Long> currentOffset = new HashMap<>();
    public static void main(String[] args) {
        // 创建配置信息
        Properties props = new Properties();
        // Kafka集群
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "hadoop102:9092");
        // 消费者组
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "codecat");
        // 关闭自动提交offset
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        // Key Value的反序列化类
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        // 创建一个消费者
        Consumer<String, String> consumer = new KafkaConsumer<>(props);
        // 订阅主题
        consumer.subscribe(Arrays.asList("first"), new ConsumerRebalanceListener() {
            // 该方法在Reblance之前调用
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> collection) {
                commitOffset(currentOffset);
            }

            // 该方法在Reblance之后调用
            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> collection) {
                currentOffset.clear();
                for (TopicPartition partition : collection) {
                    // 定位到最近提交的offset位置继续消费
                    consumer.seek(partition, getOffset(partition));
                }
            }
        });

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("offset = %d, key = %s, value = %s \n", record.offset(), record.key(), record.value());
                currentOffset.put(new TopicPartition(record.topic(), record.partition()), record.offset());
            }
        }
    }

    // 获取某分区的最新offset
    private static long getOffset(TopicPartition partition) {
        return 0;
    }

    // 提交该消费者所有分区的offset
    private static void commitOffset(Map<TopicPartition, Long> currentOffset) {
    }
}
