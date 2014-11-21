package com.despegar.metrikjc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonSerializer {
    private final Map<String, Map<Long, List<Long>>> timers;
    private final Map<String, Map<Long, List<Long>>> counters;
    
    public static class Builder {
	protected Map<String, Map<Long, List<Long>>> timers = new HashMap<>();
	protected Map<String, Map<Long, List<Long>>> counters = new HashMap<>();
	
	public Builder withTimers(Map<String, Map<Long, List<Long>>> timers){
	    this.timers = timers;
	    return this;
	}
	
	public Builder withCounters(Map<String, Map<Long, List<Long>>> counters) {
	    this.counters = counters;
	    return this;
	}
	
	public JsonSerializer build() {
	    return new JsonSerializer(timers, counters);
	}
    }
    
    private JsonSerializer(Map<String, Map<Long, List<Long>>> timers, Map<String, Map<Long, List<Long>>> counters) {
	this.timers = timers;
	this.counters = counters;
    }

    public String toJson() {
	StringBuffer json = new StringBuffer("{ \"metrics\": [");
	
	json.append(serializeMetrics(this.timers, "timers"));

	//split timers and counters
	if (this.counters.size() > 0){
	    json.append(",");
	}
	
	json.append(serializeMetrics(this.counters, "counters"));
	
	//end metrics
	json.append("]}");
	
	
	return json.toString();
    }

    private StringBuffer serializeMetrics(Map<String, Map<Long, List<Long>>> metrics, String mtype) {
	StringBuffer json = new StringBuffer();
	int nMetrics = metrics.size();
	for (Map.Entry<String, Map<Long, List<Long>>> timer : metrics.entrySet()) {
	    nMetrics--;
	    
	    json.append(String.format("{ \"name\":\"%s\", \"mtype\":\"%s\", \"measurements\":[",timer.getKey(), mtype));
	    
	    //measures
	    int nMeasures = timer.getValue().size();
	    for (Map.Entry<Long,List<Long>> measures : timer.getValue().entrySet()) {
		nMeasures--;
		
		json.append(String.format("{ \"ts\":%d, \"values\": [%s] }", measures.getKey(), toString(measures.getValue())));
		
		//split timestamps
		json.append(getSeparator(",",nMeasures));
	    }
	    
	    //end measurements
	    json.append("]}");
	    
	    //split metrics
	    json.append(getSeparator(",",nMetrics));
	}
	
	return json;
    }

    private static String toString(List<Long> values) {
	StringBuffer s = new StringBuffer();
	int j = values.size();
	for (Long value : values) {
	    j--;
	    
	    s.append(value.toString());
	    
	    if (j != 0){
		s.append(",");
	    }
	}
	
	return s.toString();
    }

    private static String getSeparator(String sep, int count) {
	if (count != 0) {
	    return sep;
	} else {
	    return "";
	}
    }
    
}
