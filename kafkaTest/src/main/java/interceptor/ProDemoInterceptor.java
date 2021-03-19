package interceptor;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;

public class ProDemoInterceptor implements ProducerInterceptor<String, String> {
    private volatile long success = 0;
    private volatile long failure = 0;

    //发送前拦截
    @Override
    public ProducerRecord<String, String> onSend(ProducerRecord<String, String> record) {
        String newValue = "prefix-" + record.value();
        return new ProducerRecord<>(record.topic(), record.partition(), record.timestamp(), record.key(),
                newValue, record.headers());
    }

    //回调函数执行前拦截
    @Override
    public void onAcknowledgement(RecordMetadata recordMetadata, Exception e) {
        if (null == e) {
            success++;
        } else {
            failure++;
        }
    }

    @Override
    public void close() {
        double ratio = (double) success / (success + failure);
        System.out.println("发送成功率为：" + (ratio * 100) + "%");
    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}
