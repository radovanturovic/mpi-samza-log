package samza.streaming.task;

import org.apache.log4j.Logger;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.StreamTask;
import org.apache.samza.task.TaskCoordinator;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
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

		String trimmedLine;
		try {
			for (String line : lines) {
				trimmedLine = line.trim();
				if (trimmedLine.startsWith("Processing")) {
					processDate(trimmedLine, productionLog);
				} else if (trimmedLine.startsWith("Parameters")) {
					processJson(trimmedLine, productionLog);
				} else if (trimmedLine.startsWith("Completed")) {
					processLink(trimmedLine, productionLog);
				}
			}
			collector.send(new OutgoingMessageEnvelope(OUTPUT_STREAM, objectMapper.writeValueAsString(productionLog)));
		} catch (IOException e) {
			log.error("Error while producing json string: {}", e);
		}
	}

	private void processDate(String line, ProductionLog productionLog) {
		try {
			final String ip = line.substring(line.indexOf("("), line.indexOf(")"));
			final String[] parts = ip.split(" ");
			productionLog.putParameter("id", parts[1]);
			productionLog.setDate(parts[2] + parts[3]);
		} catch (Exception e) {

		}
	}

	private void processLink(String line, ProductionLog productionLog) {
		try {
			final int index = line.indexOf("[");
			final String response = line.substring(line.indexOf("|") + 2, index).trim();
			final String link = line.substring(index + 1, line.length() - 1);
			productionLog.putParameter("link", link);
			productionLog.setResponse(response);
		} catch (Exception e) {

		}
	}

	private void processJson(String line, ProductionLog productionLog) {
		try {
			final String jsonString = line.substring(line.indexOf("{") + 1, line.indexOf("}"));
			for (String keyValuePair : jsonString.split(",")) {
				String[] splitted = keyValuePair.trim().split("=>");
				productionLog.putParameter(splitted[0].trim().substring(1, splitted[0].length() - 1),
						splitted[1].substring(1, splitted[1].length() - 1));
			}
		} catch (Exception e) {

		}
	}
}
