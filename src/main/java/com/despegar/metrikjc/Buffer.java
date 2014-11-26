package com.despegar.metrikjc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stores a list of metrics in memory and periodically sends them to Metrik.
 *
 */
public class Buffer {
    private static final Logger LOG = LoggerFactory.getLogger(Buffer.class);
    /**
     * Metrics stored in memory
     */
    private final LinkedBlockingQueue<Measure> measures;
    /**
     * Flush periodically the queue and send the metrics to Metrik cluster
     */
    private final ScheduledExecutorService executor;
    /**
     * Http client wrapper
     */
    private final Sender sender;
    /**
     * json serializer
     */
    private JsonSerializer jsonSerializer;
    
    

    public Buffer(MetrikConfig config) {
	this.measures = new LinkedBlockingQueue<>(config.getMaximumMeasures());
	this.sender = new Sender(config);
	this.jsonSerializer = new JsonSerializer(config.getSendIntervalMillis());
	
	this.executor = Executors.newScheduledThreadPool(1);
	this.executor.scheduleWithFixedDelay(send(), config.getSendIntervalMillis(), config.getSendIntervalMillis(), TimeUnit.MILLISECONDS);
	
	LOG.debug("Buffer to store metrics created [MaximumMeasures: %d; SendIntervalMillis: %d]",
		config.getMaximumMeasures(),config.getSendIntervalMillis());
    }

    public void add(Measure measure) {
	measures.offer(measure);
    }

    /**
     * Flush periodically the queue and send the metrics to Metrik cluster
     */
    private Runnable send() {
	return new Runnable() {
	    @Override
	    public void run() {
		LOG.debug("Starting new tick to send metrics");
		Collection<Measure> copiedMeasures = new ArrayList<>();
		measures.drainTo(copiedMeasures);
		
		String json = jsonSerializer.serialize(copiedMeasures);
		
		LOG.trace("Json to be posted to Metrik: {}", json);
		
		sender.send(json);
	    }
	};
    }
    
}
