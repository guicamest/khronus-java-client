package com.despegar.khronus.jclient;

public class Counter extends Measure {

    public Counter(String metricName, long value, long timestamp) {
        super(metricName, value, timestamp);
    }

    @Override
    public MetricType getType() {
        return MetricType.COUNTER;
    }

}
