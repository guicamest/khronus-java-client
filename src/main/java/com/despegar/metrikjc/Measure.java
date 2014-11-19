package com.despegar.metrikjc;

public class Measure {
    private String metricName;
    private long timestamp;
    private long value;
    private byte type;

    public Measure(String metricName, long value, long timestamp, byte type) {
	this.metricName = metricName;
	this.timestamp = timestamp;
	this.value = value;
	this.type = type;
    }

    public Long getTimestamp() {
	return timestamp;
    }

    public Long getValue() {
	return value;
    }

    public byte getType() {
	return type;
    }

    public String getMetricName() {
	return metricName;
    }

}
