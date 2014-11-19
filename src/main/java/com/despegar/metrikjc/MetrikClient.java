package com.despegar.metrikjc;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class MetrikClient {
    private final String[] hosts;
    private final Long sendIntervalMillis;
    private final ScheduledExecutorService executor;
    
    private AtomicReference<ConcurrentLinkedQueue<Measure>> measures;

    public static class Builder {
	protected String[] hosts;
	protected Long sendIntervalMillis;

	/**
	 * @param hosts
	 *            comma separated host:port
	 */
	public Builder withHosts(String hosts) {
	    this.hosts = hosts.split(",");
	    return this;
	}

	public Builder withSendIntervalMillis(Long interval) {
	    this.sendIntervalMillis = interval;
	    return this;
	}

	public MetrikClient build() {
	    return new MetrikClient(this);
	}
    }

    private MetrikClient(Builder builder) {
	this.hosts = builder.hosts;
	this.sendIntervalMillis = builder.sendIntervalMillis;
	
	measures = new AtomicReference<ConcurrentLinkedQueue<Measure>>(new ConcurrentLinkedQueue<Measure>());
	
	this.executor = Executors.newScheduledThreadPool(1);
	this.executor.scheduleWithFixedDelay(sendMetrics(), this.sendIntervalMillis, this.sendIntervalMillis, TimeUnit.MILLISECONDS);
    }
    
    public Runnable sendMetrics(){
	ConcurrentLinkedQueue<Measure> old = measures.getAndSet(new ConcurrentLinkedQueue<Measure>());
	for (Measure measure : old) {
	    
	}
	
	return null;
    }

    public void recordTime(String metricName, Long time) {
	this.recordTime(metricName, time, System.currentTimeMillis());
    }

    public void recordTime(String metricName, Long time, Long timestamp) {
	measures.get().add(new Measure(metricName, time, timestamp, (byte) 0));
    }
    
    public void incrementCounter(String metricName) {
	this.incrementCounter(metricName, System.currentTimeMillis());
    }

    public void incrementCounter(String metricName, Long timestamp) {
	this.incrementCounter(metricName, timestamp, 1L);
    }

    public void incrementCounter(String metricName, Long timestamp, Long counts) {
	measures.get().add(new Measure(metricName, counts, timestamp, (byte) 0));
    }

}
