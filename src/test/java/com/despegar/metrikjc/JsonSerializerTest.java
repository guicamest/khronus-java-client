package com.despegar.metrikjc;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

public class JsonSerializerTest {
    @Test
    public void toJson_oneMetric_returnsValidJson() {
	JsonSerializer instance = new JsonSerializer(5000l);
	
	Collection<Measure> measures = new ArrayList<>();
	measures.add(new Measure("responseTime", 1234l, 11111l, MetricType.TIMER));
	measures.add(new Measure("responseTime", 456l, 11111l, MetricType.TIMER));
	
	String json = instance.serialize(measures);
		
	Assert.assertEquals("{ \"metrics\": [{ \"name\":\"responseTime\", \"mtype\":\"timer\", \"measurements\":[{ \"ts\":11111, \"values\": [1234,456] }]}]}", json);
    }
    
    @Test
    public void toJson_twoMetrics_returnsValidJson() {
	JsonSerializer instance = new JsonSerializer(5000l);
	
	Collection<Measure> measures = new ArrayList<>();
	measures.add(new Measure("responseTime", 1234l, 11111l, MetricType.TIMER));
	measures.add(new Measure("responseTime", 456l, 11111l, MetricType.TIMER));
	measures.add(new Measure("totalTime", 1234l, 11111l, MetricType.TIMER));
	measures.add(new Measure("totalTime", 456l, 11111l, MetricType.TIMER));
	
	
	String json = instance.serialize(measures);
	
	Assert.assertEquals("{ \"metrics\": [{ \"name\":\"totalTime\", \"mtype\":\"timer\", \"measurements\":[{ \"ts\":11111, \"values\": [1234,456] }]},{ \"name\":\"responseTime\", \"mtype\":\"timer\", \"measurements\":[{ \"ts\":11111, \"values\": [1234,456] }]}]}", json);
    }
    
    @Test
    public void toJson_withCountersAndTimers_returnValidJson() {
	JsonSerializer instance = new JsonSerializer(5000l);
	
	Collection<Measure> measures = new ArrayList<>();
	measures.add(new Measure("responseTime", 1234l, 11111l, MetricType.TIMER));
	measures.add(new Measure("responseTime", 456l, 11111l, MetricType.TIMER));
	measures.add(new Measure("count200", 1234l, 11111l, MetricType.COUNTER));
	measures.add(new Measure("count200", 456l, 11111l, MetricType.COUNTER));
	
	String json =  instance.serialize(measures);
	
	
	Assert.assertEquals("{ \"metrics\": [{ \"name\":\"responseTime\", \"mtype\":\"timer\", \"measurements\":[{ \"ts\":11111, \"values\": [1234,456] }]},{ \"name\":\"count200\", \"mtype\":\"counter\", \"measurements\":[{ \"ts\":11111, \"values\": [1234,456] }]}]}",json);
    }

    
}
