package com.despegar.khronus.jclient;

public abstract class Measure {
    private String metricName;
    private long timestamp;
    private long value;

    public Measure(String metricName, long value, long timestamp) {
	this.metricName = metricName;
	this.timestamp = timestamp;
	this.value = value;
    }

    public long getTimestamp() {
	return timestamp;
    }

    public long getValue() {
	return value;
    }

    public abstract MetricType getType();
    
    
    public String getMetricName() {
	return metricName;
    }

}
