package com.despegar.khronus.jclient;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Stores a list of metrics in memory and periodically sends them to Khronus.
 */
public class Buffer {
    private static final Logger LOG = LoggerFactory.getLogger(Buffer.class);
    /**
     * Metrics stored in memory
     */
    private final LinkedBlockingQueue<Measure> measures;
    /**
     * Flush periodically the queue and send the metrics to Khronus cluster
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


    public Buffer(KhronusConfig config) {
        this.measures = new LinkedBlockingQueue<Measure>(config.getMaximumMeasures());
        this.sender = new Sender(config);
        this.jsonSerializer = new JsonSerializer(config.getSendIntervalMillis(), config.getApplicationName());

        BasicThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("KhronusClientSender").build();
        this.executor = Executors.newScheduledThreadPool(1, threadFactory);
        this.executor.scheduleWithFixedDelay(send(), config.getSendIntervalMillis(), config.getSendIntervalMillis(), TimeUnit.MILLISECONDS);

        LOG.debug("Buffer to store metrics created [MaximumMeasures: {}; SendIntervalMillis: {}]",
                config.getMaximumMeasures(), config.getSendIntervalMillis());
    }

    public void add(Measure measure) {
        if (!measures.offer(measure)) {
            LOG.warn("Could not add measure because the buffer is full. Measure discarted: "+measure.getMetricName());
        }
    }

    /**
     * Flush periodically the queue and send the metrics to Khronus cluster
     */
    private Runnable send() {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    if (!measures.isEmpty()){
                        LOG.debug("Sending metrics to Khronus...");
                        Collection<Measure> copiedMeasures = new ArrayList<Measure>();
                        measures.drainTo(copiedMeasures);
    
                        String json = jsonSerializer.serialize(copiedMeasures);
    
                        LOG.trace("Json to be posted to Khronus: {}", json);
    
                        sender.send(json);
                        LOG.debug("Metrics sent successfully to Khronus");
                    }
                } catch (Throwable reason) {
                    LOG.warn("Error sending metrics to Khronus", reason);
                }
            }
        };
    }

}
