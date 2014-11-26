package com.despegar.metrikjc;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * Stores each counter or time recorded. Periodically send the accumulated data.
 * 
 * Be aware that the property "sendIntervalMillis" must be less than the
 * smallest window configured in Metrik cluster.
 * 
 * The metric name will be reported as "applicationName:metricName".
 * 
 * Only one instance per application is required.
 * 
 */
public class MetrikClient {
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
	Long sendIntervalMillis = 5000L;
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
	public Builder withSendIntervalMillis(Long interval) {
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
	public MetrikClient build() {
	    validate();
	    return new MetrikClient(this);
	}

	private void validate() {
	    if (hosts == null | hosts.length == 0) {
		throw new RuntimeException(
			"Fail to build MetrikClient. Support al least one host to connect to");
	    }
	}
    }

    private MetrikClient(Builder builder) {
	this.buffer = new Buffer(new MetrikConfig(builder.applicationName,
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
    public void recordTime(String metricName, Long time) {
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
    public void recordTime(String metricName, Long time, Long timestamp) {
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
    public void incrementCounter(String metricName, Long timestamp) {
	this.incrementCounter(metricName, timestamp, 1L);
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
    public void incrementCounter(String metricName, Long timestamp, Long counts) {
	buffer.add(new Counter(metricName, counts, timestamp));
    }



}
