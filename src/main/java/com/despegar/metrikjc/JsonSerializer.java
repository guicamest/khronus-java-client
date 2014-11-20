package com.despegar.metrikjc;

import java.util.List;
import java.util.Map;

public class JsonSerializer {

    public static String toJson(Map<String, Map<Long, List<Long>>> timers, Map<String, Map<Long, List<Long>>> counters) {
	StringBuffer json = new StringBuffer("{ \"metrics\": [");
	
	int nMetrics = timers.size();
	for (Map.Entry<String, Map<Long, List<Long>>> timer : timers.entrySet()) {
	    nMetrics--;
	    
	    json.append(String.format("{ \"name\":\"%s\", \"mtype\":\"%s\", \"measurements\":[",timer.getKey(), "timer"));
	    
	    //measures
	    int nMeasures = timer.getValue().size();
	    for (Map.Entry<Long,List<Long>> measures : timer.getValue().entrySet()) {
		nMeasures--;
		
		json.append(String.format("{ \"ts\":%d, \"values\": [%s] }", measures.getKey(), toString(measures.getValue())));
		
		json.append(addSeparator(nMeasures));
	    }
	    
	    json.append(addSeparator(nMetrics));
	}
	
	
	return json.toString();
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

    private static String addSeparator(int count) {
	if (count != 0) {
	    return ",";
	} else {
	    return " ]}";
	}
    }
    
}
