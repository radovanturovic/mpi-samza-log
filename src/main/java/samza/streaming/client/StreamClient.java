package samza.streaming.client;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class StreamClient {

    private static final int STARTING_OFFSET = 0;
    private static final int MAX_NUMBER = 100000;

    private static final Logger log = Logger.getLogger(StreamClient.class);
    private BlockingQueue<String> queue;
    private boolean active;
    private int offset;
    private int max;

    public StreamClient(String source, String offset, String max) {
        try {
            this.offset = Integer.parseInt(offset);
        } catch (NumberFormatException e) {
            log.error("Error with offset format, setting offset to default value {}", e);
            this.offset = STARTING_OFFSET;
        }
        try {
            this.max = Integer.parseInt(max);
        } catch (NumberFormatException e) {
            log.error("Error with max format, setting offset to default value {}", e);
            this.max = MAX_NUMBER;
        }
        queue = new LinkedBlockingQueue<>(this.max);
        StringBuilder stringBuilder = new StringBuilder();
        active = true;
        try (BufferedReader br = new BufferedReader(new FileReader(source))) {
            String line;
            int currentBlock = 0;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    if (++currentBlock < this.offset) {
                        continue;
                    }
                    try {
                        queue.put(stringBuilder.toString());
                    } catch (InterruptedException ie) {
                        log.error("Error trying to send message to queue {}", ie);
                    } finally {
                        stringBuilder.setLength(0);
                    }
                    if (queue.size() > this.max - 10) {
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
