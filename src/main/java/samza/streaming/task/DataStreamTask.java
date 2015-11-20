package samza.streaming.task;

import org.apache.log4j.Logger;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.StreamTask;
import org.apache.samza.task.TaskCoordinator;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import samza.model.ProductionLog;

import java.io.IOException;

public class DataStreamTask implements StreamTask {
    private static final Logger log = Logger.getLogger(DataStreamTask.class);
    private static final SystemStream OUTPUT_STREAM = new SystemStream("kafka", "links-raw");

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) {
        final String logLines = (String) envelope.getMessage();
        if (logLines.toLowerCase().contains("error"))
            return;

        final String[] lines = logLines.split("\n");
        ProductionLog productionLog = new ProductionLog();

        for (String line : lines) {
            if (line.startsWith("Processing")) {
                processDate(line, productionLog);
            } else if (line.startsWith("Parameters")) {
                processJson(line, productionLog);
            } else if (line.startsWith("Completed")) {
                processLink(line, productionLog);
            }
        }

        try {
            collector.send(new OutgoingMessageEnvelope(OUTPUT_STREAM, objectMapper.writeValueAsString(productionLog)));
        } catch (IOException e) {
            log.error("Error while producing json string: {}", e);
        }
    }

    private void processDate(String line, ProductionLog productionLog) {
        final String ip = line.substring(line.indexOf("("), line.indexOf(")"));
        final String[] parts = ip.split(" ");
        productionLog.putParameter("id", parts[1]);
        productionLog.setDate(parts[2] + parts[3]);
    }

    private void processLink(String line, ProductionLog productionLog) {
        final int index = line.indexOf("[");
        final String response = line.substring(line.indexOf("|"), index).trim();
        final String link = line.substring(index, line.length() - 1);
        productionLog.putParameter("link", link);
        productionLog.putParameter("response", response);
    }

    private void processJson(String line, ProductionLog productionLog) {
        final String jsonString = line.substring(line.indexOf("{") + 1, line.indexOf("}"));
        for (String keyValuePair : jsonString.split(",")) {
            String[] splitted = keyValuePair.trim().split("=>");
            productionLog.putParameter(splitted[0], splitted[1]);
        }
    }
}
