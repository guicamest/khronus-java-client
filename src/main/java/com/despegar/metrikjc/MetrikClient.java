package com.despegar.metrikjc;


public class MetrikClient {
    private final String[] hosts;
    private Long sendIntervalMillis = 5000L;
    private Integer maximumMeasures = 1000000;
    
    private Buffer buffer;

    public static class Builder {
	protected String[] hosts;
	protected Long sendIntervalMillis;
	protected Integer maximumMeasures;

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
	
	public Builder withMaximumMeasures(Integer max) {
	    this.maximumMeasures = max;
	    return this;
	}

	public MetrikClient build() {
	    return new MetrikClient(this);
	}
    }

    private MetrikClient(Builder builder) {
	this.hosts = builder.hosts;
	this.sendIntervalMillis = builder.sendIntervalMillis;
	this.maximumMeasures = builder.maximumMeasures;
	
	this.buffer = new Buffer(this.maximumMeasures, this.sendIntervalMillis, this.hosts);
    }

    public void recordTime(String metricName, Long time) {
	this.recordTime(metricName, time, System.currentTimeMillis());
    }

    public void recordTime(String metricName, Long time, Long timestamp) {
	buffer.add(new Measure(metricName, time, timestamp, MetricType.TIMER));
    }
    
    public void incrementCounter(String metricName) {
	this.incrementCounter(metricName, System.currentTimeMillis());
    }

    public void incrementCounter(String metricName, Long timestamp) {
	this.incrementCounter(metricName, timestamp, 1L);
    }

    public void incrementCounter(String metricName, Long timestamp, Long counts) {
	buffer.add(new Measure(metricName, counts, timestamp, MetricType.COUNTER));
    }
    
}
