package com.despegar.metrikjc;

public class MetrikConfig {
    private int maximumMeasures;
    private Long sendIntervalMillis;
    private String[] hosts;
    private int maxConnections;
    private String proxy;
    private int proxyPort;
    private String applicationName;

    public MetrikConfig(String applicationName, Integer maximumMeasures, Long sendIntervalMillis, String[] hosts, Integer maxConnections, String proxy, int proxyPort) {
	this.maximumMeasures = maximumMeasures;
	this.sendIntervalMillis = sendIntervalMillis;
	this.hosts = hosts;
	this.maxConnections = maxConnections;
	this.proxy = proxy;
	this.proxyPort = proxyPort;
	this.applicationName = applicationName;
    }

    public int getMaximumMeasures() {
	return maximumMeasures;
    }

    public void setMaximumMeasures(int maximumMeasures) {
	this.maximumMeasures = maximumMeasures;
    }

    public Long getSendIntervalMillis() {
	return sendIntervalMillis;
    }

    public void setSendIntervalMillis(Long sendIntervalMillis) {
	this.sendIntervalMillis = sendIntervalMillis;
    }

    public String[] getHosts() {
	return hosts;
    }

    public void setHosts(String[] hosts) {
	this.hosts = hosts;
    }

    public int getMaxConnections() {
	return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
	this.maxConnections = maxConnections;
    }

    public String getProxy() {
	return proxy;
    }

    public void setProxy(String proxy) {
	this.proxy = proxy;
    }

    public int getProxyPort() {
	return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
	this.proxyPort = proxyPort;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

}
