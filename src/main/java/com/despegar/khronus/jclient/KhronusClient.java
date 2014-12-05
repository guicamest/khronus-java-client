package com.despegar.khronus.jclient;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * Stores each counter or time recorded. Periodically send the accumulated data.
 * 
 * Be aware that the property "sendIntervalMillis" must be less than the
 * smallest window configured in Khronus cluster.
 * 
 * The metric name will be reported as "applicationName:metricName".
 * 
 * Only one instance per application is required.
 * 
 */
public class KhronusClient {
    private static final long DEFAULT_INCREMENT = 1L;
    
    private Buffer buffer;

    /**
     * Helper to build the client.
     * 
     * The properties "hosts" and "applicationName" are required.
     */
    public static class Builder {
	/**
	 * Comma separated host:port
	 */
	String[] hosts;
	/**
	 * Time interval in milliseconds to flush the buffer and send the
	 * accumulated metrics
	 */
	long sendIntervalMillis = 5000L;
	/**
	 * Maximum number of measures to be stored in memory within intervals
	 */
	int maximumMeasures = 500000;
	/**
	 * Maximun number of connections per host
	 */
	int maxConnections = 50;
	/**
	 * Application name
	 */
	String applicationName;

	/**
	 * @param hosts
	 *            comma separated host:port
	 */
	public Builder withHosts(String hosts) {
	    this.hosts = hosts.split(",");
	    return this;
	}

	/**
	 * @param interval
	 *            Time interval in milliseconds to flush the buffer and send
	 *            the accumulated metrics
	 */
	public Builder withSendIntervalMillis(long interval) {
	    this.sendIntervalMillis = interval;
	    return this;
	}

	/**
	 * @param max
	 *            Maximum number of measures to be stored in memory within
	 *            intervals
	 */
	public Builder withMaximumMeasures(int max) {
	    this.maximumMeasures = max;
	    return this;
	}

	/**
	 * @param max
	 *            Maximun number of connections per host
	 */
	public Builder withMaxConnections(int max) {
	    this.maxConnections = max;
	    return this;
	}

	/**
	 * @param appName
	 *            Application name
	 */
	public Builder withApplicationName(String appName) {
	    this.applicationName = StringUtils.join(appName);
	    return this;
	}

	/**
	 * Create an instance of a Client. One per application is recommended
	 * 
	 * @return an new instance of Client
	 */
	public KhronusClient build() {
	    validate();
	    return new KhronusClient(this);
	}

	private void validate() {
	    if (hosts == null | hosts.length == 0) {
		throw new RuntimeException(
			"Fail to build KhronusClient. Support al least one host to connect to");
	    }
	}
    }

    private KhronusClient(Builder builder) {
	this.buffer = new Buffer(new KhronusConfig(builder.applicationName,
		builder.maximumMeasures, builder.sendIntervalMillis,
		builder.hosts, builder.maxConnections));
    }

    /**
     * Record the duration of an event. The timestamp at which happened is
     * assumed to be now.
     * 
     * @param metricName
     *            unique name within application
     * @param time
     *            duration in milliseconds
     */
    public void recordTime(String metricName, long time) {
	this.recordTime(metricName, time, System.currentTimeMillis());
    }

    /**
     * Record the duration of an event specifying the timestamp at which
     * happened.
     * 
     * @param metricName
     *            unique name within application
     * @param time
     *            duration in milliseconds
     * @param timestamp
     *            when the event happened in milliseconds since epoch
     */
    public void recordTime(String metricName, long time, long timestamp) {
	buffer.add(new Timer(metricName, time, timestamp));
    }

    /**
     * Increment by one the value of the specified metric
     * 
     * @param metricName
     *            unique name within application
     */
    public void incrementCounter(String metricName) {
	this.incrementCounter(metricName, System.currentTimeMillis());
    }

    /**
     * Increment by one the value of the specified metric specifying the
     * timestamp at which happened
     * 
     * @param metricName
     *            unique name within application
     * @param timestamp
     *            when the event happened in milliseconds since epoch
     */
    public void incrementCounter(String metricName, long timestamp) {
	this.incrementCounter(metricName, timestamp, DEFAULT_INCREMENT);
    }

    /**
     * Increment by n the value of the specified metric specifying the timestamp
     * at which happened
     * 
     * @param metricName
     *            unique name within application
     * @param timestamp
     *            when the event happened in milliseconds since epoch
     * @param counts
     *            number of times to be incremented
     */
    public void incrementCounter(String metricName, long timestamp, long counts) {
	buffer.add(new Counter(metricName, counts, timestamp));
    }



}
