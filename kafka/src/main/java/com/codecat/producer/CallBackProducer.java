package com.codecat.producer;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;

public class CallBackProducer {
    public static void main(String[] args) {
        // 1. 创建配置信息
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "hadoop102:9092");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 1);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        // 2. 创建生产者对象
        Producer<String, String> producer = new KafkaProducer<String, String>(props);

        // 3. 发送数据
        for (int i = 0; i < 10; i++) {
            producer.send(new ProducerRecord<>("first", "codecat--" + i), new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    if (e == null) {
                        System.out.println(recordMetadata.partition() + "--success->" + recordMetadata.offset());
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }


        // 4. 关闭资源
        producer.close();
    }
}
