package samza.streaming.task;

import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.InitableTask;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.StreamTask;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import samza.model.ProductionLog;
import samza.util.DBManager;

public class DataPersistTask implements StreamTask, InitableTask{

	private static final SystemStream OUTPUT_STREAM = new SystemStream("kafka", "persist-raw");
	private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
    	objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	    
	@Override
	public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		final String json = (String) envelope.getMessage();
        collector.send(new OutgoingMessageEnvelope(OUTPUT_STREAM, json));
        ProductionLog log = objectMapper.readValue(json, ProductionLog.class);
        DBManager.INSTANCE.insert(log);
	}

	@Override
	public void init(Config arg0, TaskContext arg1) throws Exception {
	    DBManager.INSTANCE.connect("MPI-Logs", "Logs");
		DBManager.INSTANCE.wrap(ProductionLog.class);
	}

}
