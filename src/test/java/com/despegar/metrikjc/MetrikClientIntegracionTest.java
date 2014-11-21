package com.despegar.metrikjc;

import java.util.Random;

import org.junit.Test;

public class MetrikClientIntegracionTest {

    @Test
    public void addMeasures_randomTimers_sendMetrics() throws Exception {
	MetrikClient client = new MetrikClient.Builder()
		.withHosts("10.2.7.101:1173,host1:1111")
		.withMaxConnections(180)
		.withMaximumMeasures(50000)
		.withSendIntervalMillis(5000l)
		.withApplicationName("metrikClient")
		.build();
	
	Random random = new Random();
	
	while(true){
	    client.incrementCounter("demoCounter");
	    client.recordTime("demoTimer", new Long(random.nextInt(10000)).longValue());
	    Thread.sleep(500l);
	}
    }
}
