package samza.streaming.client;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;

public class StreamClient {
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<String>(100);
    private boolean active;

    public StreamClient(String source) {
    	/*
        try (Stream<String> stream = Files.lines(Paths.get(source), Charset.defaultCharset())) {
            stream.filter(e -> e != null || !e.isEmpty() || !e.startsWith("[")).forEach(e -> {
                try {
                    queue.put(e.substring(e.indexOf(']')));
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        active = true;
    	try {
			queue.put("test");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
