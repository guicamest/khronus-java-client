package com.despegar.metrikjc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class JsonSerializerTest {

    @Test
    public void toJson_oneMetric_returnsValidJson() {
	Map<String, Map<Long, List<Long>>> timers = new HashMap<String, Map<Long,List<Long>>>();
	
	timers.put("responseTime", createMeasure());
	
	String json = new JsonSerializer.Builder().withTimers(timers).build().toJson();
		
	Assert.assertEquals("{ \"metrics\": [{ \"name\":\"responseTime\", \"mtype\":\"timer\", \"measurements\":[{ \"ts\":11111, \"values\": [1234,456] }]}]}", json);
    }
    
    @Test
    public void toJson_twoMetrics_returnsValidJson() {
	Map<String, Map<Long, List<Long>>> timers = new HashMap<String, Map<Long,List<Long>>>();
	
	timers.put("responseTime", createMeasure());
	
	timers.put("totalTime", createMeasure());
	
	String json = new JsonSerializer.Builder().withTimers(timers).build().toJson();
	
	Assert.assertEquals("{ \"metrics\": [{ \"name\":\"totalTime\", \"mtype\":\"timer\", \"measurements\":[{ \"ts\":11111, \"values\": [1234,456] }]},{ \"name\":\"responseTime\", \"mtype\":\"timer\", \"measurements\":[{ \"ts\":11111, \"values\": [1234,456] }]}]}", json);
    }
    
    @Test
    public void toJson_withCountersAndTimers_returnValidJson() {
	Map<String, Map<Long, List<Long>>> timers = new HashMap<String, Map<Long,List<Long>>>();
	timers.put("responseTime", createMeasure());
	
	Map<String, Map<Long, List<Long>>> counters = new HashMap<String, Map<Long,List<Long>>>();
	counters.put("count200", createMeasure());
	
	String json = new JsonSerializer.Builder().withTimers(timers).withCounters(counters).build().toJson();
	
	
	Assert.assertEquals("{ \"metrics\": [{ \"name\":\"responseTime\", \"mtype\":\"timer\", \"measurements\":[{ \"ts\":11111, \"values\": [1234,456] }]},{ \"name\":\"count200\", \"mtype\":\"counter\", \"measurements\":[{ \"ts\":11111, \"values\": [1234,456] }]}]}",json);
    }
    
    private Map<Long, List<Long>> createMeasure() {
	Map<Long, List<Long>> measures = new HashMap<>();
	List<Long> values = new ArrayList<>();
	values.add(1234l);
	values.add(456l);
	
	measures.put(11111l, values);
	return measures;
    }
    
}
