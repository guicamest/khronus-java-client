package com.despegar.khronus.jclient;

public class Gauge extends Measure {
    public Gauge(String metricName, long value, long timestamp) {
        super(metricName, value, timestamp);
    }
    @Override
    public MetricType getType() {
        return MetricType.GAUGE;
    }
}
