package com.despegar.khronus.jclient;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class JsonSerializerTest {
    private static Properties prop = new Properties();
	
	
    @BeforeClass
    public static void setup() throws Exception {
	String propFileName = "test.properties";

	InputStream inputStream = JsonSerializerTest.class
		.getResourceAsStream(propFileName);

	if (inputStream != null) {
	    prop.load(inputStream);
	} else {
	    throw new FileNotFoundException("property file '" + propFileName
		    + "' not found in the classpath");
	}
    }
    
    @Test
    public void toJson_oneMetric_returnsValidJson() {
        JsonSerializer instance = new JsonSerializer(5000l, "demoApp");

        Collection<Measure> measures = new ArrayList<Measure>();
        measures.add(new Timer("responseTime", 1234l, 11111l));
        measures.add(new Timer("responseTime", 456l, 11111l));

        String json = instance.serialize(measures);

        Assert.assertEquals(prop.getProperty("toJson_oneMetric_returnsValidJson"), json);
    }
    
    @Test
    public void toJson_oneMetricWithTwoRecords_returnsValidJson() {
        JsonSerializer instance = new JsonSerializer(5000l, "demoApp");

        Collection<Measure> measures = new ArrayList<Measure>();
        measures.add(new Timer("responseTime", 1234l, 11111l));
        measures.add(new Timer("responseTime", 456l, 153453l));

        String json = instance.serialize(measures);
        

        Assert.assertEquals(prop.getProperty("toJson_oneMetricWithTwoRecords_returnsValidJson"), json);
    }

    @Test
    public void toJson_twoMetrics_returnsValidJson() {
        JsonSerializer instance = new JsonSerializer(5000l, null);

        Collection<Measure> measures = new ArrayList<Measure>();
        measures.add(new Timer("responseTime", 1234l, 11111l));
        measures.add(new Timer("responseTime", 456l, 11111l));
        measures.add(new Timer("totalTime", 1234l, 11111l));
        measures.add(new Timer("totalTime", 456l, 11111l));


        String json = instance.serialize(measures);

        Assert.assertEquals(prop.getProperty("toJson_twoMetrics_returnsValidJson"), json);
    }

    @Test
    public void toJson_withCountersAndTimers_returnValidJson() {
        JsonSerializer instance = new JsonSerializer(5000l, null);

        Collection<Measure> measures = new ArrayList<Measure>();
        measures.add(new Timer("responseTime", 1234l, 11111l));
        measures.add(new Timer("responseTime", 456l, 11111l));
        measures.add(new Counter("count200", 1234l, 11111l));
        measures.add(new Counter("count200", 456l, 11111l));

        String json = instance.serialize(measures);

        Assert.assertEquals(prop.getProperty("toJson_withCountersAndTimers_returnValidJson"), json);
    }


}
