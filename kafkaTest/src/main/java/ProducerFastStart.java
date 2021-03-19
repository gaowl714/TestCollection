import interceptor.ProDemoInterceptor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import partition.ConDemoPartition;

import java.util.Properties;
import java.util.concurrent.Future;

public class ProducerFastStart {
    public static final String brokerList = "101.200.58.48:9092";
    public static final String topic = "topic-demo";

    public static void main(String[] args) {
        System.out.println(1/0);
        Properties properties = new Properties();
        //指定kafka服务器地址，多个地址用逗号分开
        properties.put("bootstrap.servers", brokerList);
        //指定键值序列化方式
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        //发生异常后重试10次
        properties.put(ProducerConfig.RETRIES_CONFIG, 10);
        //指定分区器
        properties.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, ConDemoPartition.class.getName());
        //指定拦截器
        properties.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, ProDemoInterceptor.class.getName());
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, "Hello kafka!");
        try {
            Future<RecordMetadata> send = producer.send(record);
            RecordMetadata recordMetadata = send.get();
            System.out.println(recordMetadata.partition());
            System.out.println(recordMetadata.topic());
            System.out.println(recordMetadata.offset());
            System.out.println(recordMetadata.timestamp());
            //带回调函数的发送信息
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    exception.printStackTrace();
                } else {
                    System.out.println(metadata.partition());
                    System.out.println(metadata.topic());
                    System.out.println(metadata.offset());
                    System.out.println(metadata.timestamp());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            producer.close();
        }
    }
}
