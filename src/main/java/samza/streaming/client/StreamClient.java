package samza.streaming.client;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;

public class StreamClient {
    private static final Logger log = Logger.getLogger(StreamClient.class);
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<String>(100);
    private boolean active;

    public StreamClient(String source) {
        StringBuilder stringBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(source), Charset.defaultCharset())) {
            stream.filter(e -> e != null || !e.isEmpty()).forEach(e -> {
                if (e.startsWith("\n")) {
                    try {
                        queue.put(stringBuilder.toString());
                    } catch (InterruptedException ie) {
                        log.error("Error trying to send message to queue {}", ie);
                    } finally {
                        stringBuilder.setLength(0);
                    }
                } else {
                    final int index = e.indexOf("]");
                    stringBuilder.append(index > 0 && e.startsWith("[") ? e.substring(index).trim() : e.trim());
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        /* --for testing purposes
        active = true;
    	try {
			queue.put("test");
		} catch (InterruptedException e) {
			log.error("Some random error :D {}", e);
		}
		*/
    }

    public void stop() {
    	active = false;
    }

    public void stream(final LinkProcessor linkProcessor) {
        while (!queue.isEmpty() && active){
        	try {
				linkProcessor.processLink(queue.take());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
    }

    public interface LinkProcessor {
        void processLink(String link);
    }

}
