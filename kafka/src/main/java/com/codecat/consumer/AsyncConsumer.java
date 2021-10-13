package com.codecat.consumer;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

public class AsyncConsumer {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "hadoop102:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "codecat");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        Consumer<String, String> consumer = new KafkaConsumer<>(props);

        consumer.subscribe(Arrays.asList("first"));

        while (true) {
            ConsumerRecords<String, String> consumerRecords = consumer.poll(100);
            for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
                System.out.printf("offset = %d, key = %s, value = %s\n", consumerRecord.offset(), consumerRecord.key(), consumerRecord.value());
            }
            // 异步提交
            consumer.commitAsync(new OffsetCommitCallback() {
                @Override
                public void onComplete(Map<TopicPartition, OffsetAndMetadata> map, Exception e) {
                    if (e != null) {
                        System.err.println("Commit failed for" + map);
                    }
                }
            });
        }
    }
}
