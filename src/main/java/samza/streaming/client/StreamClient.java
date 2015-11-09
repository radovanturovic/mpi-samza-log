package samza.streaming.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class StreamClient {
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<String>(100);
    private boolean active;

    public StreamClient() {
    	try {
    		// Parsiranje log fajla
			queue.put("test");
			active = true;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }

    public void stop() {
    	active = false;
    }

    public void stream(final LinkProcessor linkProcessor) {
        while (! queue.isEmpty() && active){
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
