package com.despegar.metrikjc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Buffer {
    private static final Logger LOG = LoggerFactory.getLogger(Buffer.class);
    private static final double PERCETANGE_OFINTERVAL_FORSERIALIZATION = 0.7;
    private static final long BATCH_SIZE_TOCHECK = 5000;
    private final LinkedBlockingQueue<Measure> measures;
    private final ScheduledExecutorService executor;
    private final Sender sender;
    private final double sendIntervalMillis;
    

    public Buffer(MetrikConfig config) {
	this.measures = new LinkedBlockingQueue<>(config.getMaximumMeasures());
	this.sender = new Sender(config);
	this.sendIntervalMillis = config.getSendIntervalMillis();
	
	this.executor = Executors.newScheduledThreadPool(1);
	this.executor.scheduleWithFixedDelay(send(), config.getSendIntervalMillis(), config.getSendIntervalMillis(), TimeUnit.MILLISECONDS);
	
	LOG.debug("Buffer to store metrics created [MaximumMeasures: %d; SendIntervalMillis: %d]",
		config.getMaximumMeasures(),config.getSendIntervalMillis());
    }

    public void add(Measure measure) {
	measures.offer(measure);
    }

    private Runnable send() {
	return new Runnable() {
	    @Override
	    public void run() {
		LOG.debug("Starting new tick to send metrics");
		long deadlineForSerialization = getDeadlineForSerialization();
		
		Collection<Measure> copiedMeasures = new ArrayList<>();
		measures.drainTo(copiedMeasures);
		
		Map<String, Map<Long, List<Long>>> timers = new HashMap<String, Map<Long, List<Long>>>();
		Map<String, Map<Long, List<Long>>> counters = new HashMap<String, Map<Long, List<Long>>>();
		long count = 1;
		for (Measure measure : copiedMeasures) {
		    Map<String, Map<Long, List<Long>>> metrics = measure.getType() == MetricType.TIMER ? timers : counters;

		    Map<Long, List<Long>> metric = putMetricIfAbsent(metrics, measure.getMetricName());
		    List<Long> measures = putTimestampIfAbsent(metric, measure.getTimestamp());
		    
		    measures.add(measure.getValue());
		    
		    if (hasTimeout(deadlineForSerialization, count)){
			LOG.warn("Timeout was reached during serialization of metrics. Some will be discarted");
			break;
		    }
		    
		    count++;
		}
		
		String json = new JsonSerializer.Builder().withCounters(counters).withTimers(timers).build().toJson();
		
		LOG.trace("Json to be posted to Metrik: %s", json);
		
		sender.send(json);
	    }
	};
    }
    
    private Map<Long, List<Long>> putMetricIfAbsent(Map<String, Map<Long, List<Long>>> metrics, String metricName) {
	Map<Long, List<Long>> metric = metrics.get(metricName);
	if (metric == null) {
	    metric = new HashMap<Long, List<Long>>();
	    metrics.put(metricName, metric);
	}

	return metric;
    }
    
    private List<Long> putTimestampIfAbsent(Map<Long, List<Long>> metric, Long timestamp) {
	List<Long> measures = metric.get(timestamp);
	if (measures == null) {
	    measures = new ArrayList<Long>();
	    metric.put(timestamp, measures);
	}
	
	return measures;
    }
    
    
    private boolean hasTimeout(long deadlineForSerialization, long count) {
	if (count % BATCH_SIZE_TOCHECK == 0) {
	    if (System.currentTimeMillis() > deadlineForSerialization) {
		return true;
	    }
	}

	return false;
    }

    private long getDeadlineForSerialization() {
	return System.currentTimeMillis() + new Double(sendIntervalMillis * PERCETANGE_OFINTERVAL_FORSERIALIZATION).longValue();
    }
    
}
