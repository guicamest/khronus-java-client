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

public class Buffer {
    private static final double PERCETANGE_OFINTERVAL_FORSERIALIZATION = 0.7;
    private final LinkedBlockingQueue<Measure> measures;
    private final Long sendIntervalMillis;
    private final Integer maximumMeasures;
    private final ScheduledExecutorService executor;
    private final String[] hosts;

    public Buffer(Integer maximumMeasures, Long sendIntervalMillis, String[] hosts) {
	this.maximumMeasures = maximumMeasures;
	this.sendIntervalMillis = sendIntervalMillis;
	this.hosts = hosts;
	
	measures = new LinkedBlockingQueue<>(this.maximumMeasures);
	
	this.executor = Executors.newScheduledThreadPool(1);
	this.executor.scheduleWithFixedDelay(send(), this.sendIntervalMillis, this.sendIntervalMillis, TimeUnit.MILLISECONDS);
    }

    public void add(Measure measure) {
	measures.offer(measure);
    }
    
    private Runnable send(){
	long deadlineForSerialization = getDeadlineForSerialization();
	
	Collection<Measure> copiedMeasures = new ArrayList<>();
	measures.drainTo(copiedMeasures);
	
	Map<String, Map<Long, List<Long>>> timers = new HashMap<String, Map<Long, List<Long>>>();
	Map<String, Map<Long, List<Long>>> counters = new HashMap<String, Map<Long, List<Long>>>();
	for (Measure measure : copiedMeasures) {
	    Map<String, Map<Long, List<Long>>> metrics = measure.getType() == MetricType.TIMER ? timers : counters;

	    Map<Long, List<Long>> metric = putMetricIfAbsent(metrics, measure.getMetricName());
	    List<Long> measures = putTimestampIfAbsent(metric, measure.getTimestamp());
	    
	    measures.add(measure.getValue());
	    
	    if (hasTimeout(deadlineForSerialization)){
		break;
	    }
	}
	
	String json = JsonSerializer.toJson(timers, counters);
	
	return null;
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
    
    

    private boolean hasTimeout(long deadlineForSerialization) {
	if (System.currentTimeMillis() > deadlineForSerialization) {
	    return true;
	}
	
	return false;
    }

    private long getDeadlineForSerialization() {
	return System.currentTimeMillis() + new Double(sendIntervalMillis * PERCETANGE_OFINTERVAL_FORSERIALIZATION).longValue();
    }
    
    
}
