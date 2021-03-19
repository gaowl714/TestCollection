package partition;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.utils.Utils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ConDemoPartition implements Partitioner {
    private final AtomicInteger counter = new AtomicInteger();

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
        int sum = partitions.size();
        //如果键为空则轮询所有分区后插入，与defaultPartition（轮询可用分区）不一样
        if (null == keyBytes) {
            return counter.getAndIncrement() % sum;
        } else {
            return Utils.toPositive(Utils.murmur2(keyBytes)) % sum;
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}
