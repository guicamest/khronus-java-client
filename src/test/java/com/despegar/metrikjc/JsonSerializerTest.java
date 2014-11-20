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
	
	Map<Long, List<Long>> measures = new HashMap<>();
	List<Long> values = new ArrayList<>();
	values.add(1234l);
	values.add(456l);
	
	measures.put(11111l, values);
	
	timers.put("responseTime", measures);
	
	String json = JsonSerializer.toJson(timers, null);
	
	Assert.assertEquals("{ \"metrics\": [{ \"name\":\"responseTime\", \"mtype\":\"timer\", \"measurements\":[{ \"ts\":11111, \"values\": [1234,456] } ]} ]}", json);
    }
    
    @Test
    public void toJson_twoMetrics_returnsValidJson() {
	Map<String, Map<Long, List<Long>>> timers = new HashMap<String, Map<Long,List<Long>>>();
	
	Map<Long, List<Long>> measures = new HashMap<>();
	List<Long> values = new ArrayList<>();
	values.add(1234l);
	values.add(456l);
	
	measures.put(11111l, values);
	
	timers.put("responseTime", measures);
	
	timers.put("totalTime", measures);
	
	String json = JsonSerializer.toJson(timers, null);
	System.out.println(json);
	
	Assert.assertEquals("{ \"metrics\": [{ \"name\":\"totalTime\", \"mtype\":\"timer\", \"measurements\":[{ \"ts\":11111, \"values\": [1234,456] } ]},"
		+ "{ \"name\":\"responseTime\", \"mtype\":\"timer\", \"measurements\":[{ \"ts\":11111, \"values\": [1234,456] } ]} ]}", json);
    }
}
