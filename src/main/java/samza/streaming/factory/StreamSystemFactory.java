package samza.streaming.factory;

import org.apache.samza.SamzaException;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.system.SystemAdmin;
import org.apache.samza.system.SystemConsumer;
import org.apache.samza.system.SystemFactory;
import org.apache.samza.system.SystemProducer;
import org.apache.samza.util.SinglePartitionWithoutOffsetsSystemAdmin;

import samza.streaming.client.*;


public class StreamSystemFactory implements SystemFactory {


    public SystemConsumer getConsumer(String systemName, Config config, MetricsRegistry registry) {
        return new StreamConsumer(new StreamClient(), systemName, registry);
    }

    public SystemProducer getProducer(String systemName, Config config, MetricsRegistry registry) {
        throw new SamzaException("Producing not supported");
    }

    public SystemAdmin getAdmin(String systemName, Config config) {
        return new SinglePartitionWithoutOffsetsSystemAdmin();
    }

}
