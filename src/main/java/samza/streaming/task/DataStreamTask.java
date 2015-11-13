package samza.streaming.task;

import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.StreamTask;
import org.apache.samza.task.TaskCoordinator;

public class DataStreamTask implements StreamTask {

    private static final SystemStream OUTPUT_STREAM = new SystemStream("kafka", "links-raw");

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) {
        final String logLine = (String) envelope.getMessage();
        collector.send(new OutgoingMessageEnvelope(OUTPUT_STREAM, logLine));
    }
}
