package samza.streaming.factory;

import org.apache.samza.Partition;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.SystemStreamPartition;
import org.apache.samza.util.BlockingEnvelopeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import samza.streaming.client.StreamClient;

public class StreamConsumer extends BlockingEnvelopeMap implements StreamClient.LinkProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(StreamConsumer.class);

    private static final String STREAM_NAME = "startingLinks";

    private final StreamClient streamClient;
    private final String systemName;

    public StreamConsumer(final StreamClient streamClient, final String systemName, MetricsRegistry metricsRegistry) {
        super();
        this.streamClient = streamClient;
        this.systemName = systemName;
    }

    public void start() {
        streamClient.stream(this);
    }

    public void stop() {
        streamClient.stop();
    }

    @Override
    public void processLink(String link) {
        SystemStreamPartition systemStreamPartition = new SystemStreamPartition(systemName, STREAM_NAME, new Partition(0));
        try {
            put(systemStreamPartition, new IncomingMessageEnvelope(systemStreamPartition, null, null, link));
        } catch (Exception e) {
            LOG.error("Caught an exception", e);
        }
    }
}
