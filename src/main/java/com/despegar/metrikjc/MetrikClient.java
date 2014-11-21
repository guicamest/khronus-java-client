package com.despegar.metrikjc;

import org.apache.commons.lang3.StringUtils;


public class MetrikClient {
    private Buffer buffer;
    private String applicationName;

    public static class Builder {
	String[] hosts;
	Long sendIntervalMillis = 5000L;
	int maximumMeasures = 500000;
	int maxConnections = 50;
	String proxy;
	int proxyPort = 8080;
	String applicationName;

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
	
	public Builder withMaximumMeasures(int max) {
	    this.maximumMeasures = max;
	    return this;
	}
	
	public Builder withMaxConnections(int max) {
	    this.maxConnections = max;
	    return this;
	}
	
	public Builder withProxy(String proxy){
	    this.proxy = proxy;
	    return this;
	}
	
	public Builder withProxyPort(int proxyPort){
	    this.proxyPort = proxyPort;
	    return this;
	}
	
	public Builder withApplicationName(String appName){
	    this.applicationName = StringUtils.join(appName);
	    return this;
	}

	public MetrikClient build() {
	    validate();
	    return new MetrikClient(this);
	}

	private void validate() {
	    if (hosts == null | hosts.length == 0){
		throw new RuntimeException("Fail to build MetrikClient. Support al least one host to connect to");
	    }
	    
	    if (StringUtils.isEmpty(applicationName)){
		throw new RuntimeException("Fail to build MetrikClient. Support the application name");
	    }
	}
    }

    private MetrikClient(Builder builder) {
	this.applicationName = builder.applicationName;
	this.buffer = new Buffer(new MetrikConfig(builder.applicationName, builder.maximumMeasures, builder.sendIntervalMillis, builder.hosts, 
		builder.maxConnections, builder.proxy, builder.proxyPort));
    }

    public void recordTime(String metricName, Long time) {
	this.recordTime(metricName, time, System.currentTimeMillis());
    }

    public void recordTime(String metricName, Long time, Long timestamp) {
	buffer.add(new Measure(getUniqueName(metricName), time, timestamp, MetricType.TIMER));
    }
    
    public void incrementCounter(String metricName) {
	this.incrementCounter(metricName, System.currentTimeMillis());
    }

    public void incrementCounter(String metricName, Long timestamp) {
	this.incrementCounter(metricName, timestamp, 1L);
    }

    public void incrementCounter(String metricName, Long timestamp, Long counts) {
	buffer.add(new Measure(getUniqueName(metricName), counts, timestamp, MetricType.COUNTER));
    }

    private String getUniqueName(String metricName) {
	return this.applicationName+":"+metricName;
    }
    
}
