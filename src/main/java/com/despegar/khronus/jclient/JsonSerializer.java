package com.despegar.khronus.jclient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Serialize a list of timers and counters to the Khronus API 
 *
 */
public class JsonSerializer {
    private static final Logger LOG = LoggerFactory.getLogger(JsonSerializer.class);
    /**
     * Percentage of the total time to be dedicated for serialization
     */
    private static final double PERCETANGE_OFINTERVAL_FORSERIALIZATION = 0.7;
    /**
     * Batch size to check if the time assigned for serialization is consumed
     */
    private static final long BATCH_SIZE_TOCHECK_TIMEOUT = 5000;
    /**
     * Time interval in milliseconds to flush the buffer and send the
     * accumulated metrics
     */
    private final double sendIntervalMillis;
    
    private String applicationName;

    /**
     * Create the serializer.
     * 
     * @param sendIntervalMillis
     *            Time interval in milliseconds to flush the buffer and send the
     *            accumulated metrics
     * @param string 
     */
    public JsonSerializer(Long sendIntervalMillis, String applicationName) {
	this.sendIntervalMillis = sendIntervalMillis;
	this.applicationName = applicationName;
    }

    public String serialize(Collection<Measure> measures) {
	long deadlineForSerialization = getDeadlineForSerialization();
	
	Map<String, Map<Long, List<Long>>> timers = new HashMap<String, Map<Long, List<Long>>>();
	Map<String, Map<Long, List<Long>>> counters = new HashMap<String, Map<Long, List<Long>>>();
	long count = 1;
	for (Measure measure : measures) {
	    Map<String, Map<Long, List<Long>>> metrics = measure.getType() == MetricType.TIMER ? timers : counters;

	    Map<Long, List<Long>> metric = putMetricIfAbsent(metrics, measure.getMetricName());
	    List<Long> measurements = putTimestampIfAbsent(metric, measure.getTimestamp());
	    
	    measurements.add(measure.getValue());
	    
	    if (hasTimeout(deadlineForSerialization, count)){
		LOG.warn("Timeout was reached during serialization of metrics. Some will be discarted");
		break;
	    }
	    
	    count++;
	}
	
	return toJson(timers, counters);
    }
    
    
    private String toJson(Map<String, Map<Long, List<Long>>> timers, Map<String, Map<Long, List<Long>>> counters) {
	StringBuffer json = new StringBuffer("{ \"metrics\": [");
	
	json.append(serializeMetrics(timers, "timer"));

	//split timers and counters
	if (counters.size() > 0){
	    json.append(",");
	}
	
	json.append(serializeMetrics(counters, "counter"));
	
	//end metrics
	json.append("]}");
	
	
	return json.toString();
    }

    private StringBuffer serializeMetrics(Map<String, Map<Long, List<Long>>> metrics, String mtype) {
	StringBuffer json = new StringBuffer();
	int nMetrics = metrics.size();
	for (Map.Entry<String, Map<Long, List<Long>>> timer : metrics.entrySet()) {
	    nMetrics--;
	    
	    json.append(String.format("{ \"name\":\"%s\", \"mtype\":\"%s\", \"measurements\":[", getUniqueMetricName(timer.getKey()), mtype));
	    
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
    
    /**
     * Create an unique key for the specified metric
     */
    protected String getUniqueMetricName(String metricName) {
	if (StringUtils.isEmpty(this.applicationName)){
	    return metricName;
	} 
	
	return this.applicationName + ":" + metricName;
    }

    private static String getSeparator(String sep, int count) {
	if (count != 0) {
	    return sep;
	} else {
	    return "";
	}
    }
    
    private Map<Long, List<Long>> putMetricIfAbsent(Map<String, Map<Long, List<Long>>> metrics, String metricName) {
	Map<Long, List<Long>> metric = metrics.get(metricName);
	if (metric == null) {
	    metric = new HashMap<Long, List<Long>>();
	    metrics.put(metricName, metric);
	}

	return metric;
    }
    
    private List<Long> putTimestampIfAbsent(Map<Long, List<Long>> metric, Long timestamp) {
	List<Long> measures = metric.get(timestamp);
	if (measures == null) {
	    measures = new ArrayList<Long>();
	    metric.put(timestamp, measures);
	}
	
	return measures;
    }
    
    
    private boolean hasTimeout(long deadlineForSerialization, long count) {
	if (count % BATCH_SIZE_TOCHECK_TIMEOUT == 0) {
	    if (System.currentTimeMillis() > deadlineForSerialization) {
		return true;
	    }
	}

	return false;
    }

    private long getDeadlineForSerialization() {
	return System.currentTimeMillis() + new Double(sendIntervalMillis * PERCETANGE_OFINTERVAL_FORSERIALIZATION).longValue();
    }

    
}
