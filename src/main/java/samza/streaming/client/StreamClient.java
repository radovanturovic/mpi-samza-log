package samza.streaming.client;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class StreamClient {

	private static final int STARTING_OFFSET = 100000;
	private static final int MAX_NUMBER = 100000;

	private static final Logger log = Logger.getLogger(StreamClient.class);
	private static final BlockingQueue<String> queue = new LinkedBlockingQueue<>(MAX_NUMBER);
	private boolean active;

	public StreamClient(String source) {

		StringBuilder stringBuilder = new StringBuilder();
		active = true;
		try (BufferedReader br = new BufferedReader(new FileReader(source))) {
			String line;
			int currentLine = 0;
			while ((line = br.readLine()) != null) {
				if (++currentLine < STARTING_OFFSET) {
					continue;
				}
				if (line.isEmpty()) {
					try {
						queue.put(stringBuilder.toString());
					} catch (InterruptedException ie) {
						log.error("Error trying to send message to queue {}", ie);
					} finally {
						stringBuilder.setLength(0);
					}
					if (queue.remainingCapacity() < 10) {
						br.close();
						break;
					}
				} else {
					final int index = line.indexOf("]") + 1;
					stringBuilder
							.append(index > 0 && line.startsWith("[") ? line.substring(index) + "\n" : line + "\n");
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		active = false;
	}

	public void stream(final LinkProcessor linkProcessor) {
		while (!queue.isEmpty() && active) {
			try {
				linkProcessor.processLink(queue.take());
			} catch (InterruptedException e) {
				log.error("Exception in stream: {}", e);
			}
		}
	}

	public interface LinkProcessor {
		void processLink(String link);
	}

}
