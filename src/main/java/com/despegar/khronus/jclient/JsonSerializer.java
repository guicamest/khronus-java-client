package com.despegar.khronus.jclient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
	
	Map<String, Collection<Measure>> groupedByMetric = groupByMetric(measures);
	
	StringBuilder json = new StringBuilder("{ \"metrics\": [");
	long count = 0;
	for (Entry<String, Collection<Measure>> metric : groupedByMetric.entrySet()) {
	    count++;
	    
	    json.append(toJson(metric.getKey(), metric.getValue()));
	    if (count != groupedByMetric.size()){
		//split metrics
		json.append(",");
	    }
		
	    
	    if (hasTimeout(deadlineForSerialization, count)){
		LOG.warn("Timeout was reached during serialization of metrics. Some will be discarted");
		break;
	    }
	    
	}
	
	return json.append("]}").toString();
    }
    
    
    private Map<String, Collection<Measure>> groupByMetric(Collection<Measure> measures) {
	Map<String, Collection<Measure>> groupedByMetric = new HashMap<String, Collection<Measure>>();
	for (Measure measure : measures) {
	    Collection<Measure> measuresByMetric = groupedByMetric.get(measure.getMetricName());
	    if (measuresByMetric == null){
		measuresByMetric = new ArrayList<Measure>();
		groupedByMetric.put(measure.getMetricName(), measuresByMetric);
	    }
	    
	    measuresByMetric.add(measure);
	}
	
	return groupedByMetric;
    }

    private String toJson(String metricName, Collection<Measure> measures) {
	StringBuilder json = new StringBuilder();
	
	json.append(String.format("{ \"name\":\"%s\", \"mtype\":\"%s\", \"measurements\":[", getUniqueMetricName(metricName), measures.iterator().next().getType()));
	
	int nMeasures = 0;
	for (Measure measure : measures) {
	    nMeasures++;
	    json.append(String.format("{ \"ts\":%d, \"values\": [%s] }", measure.getTimestamp(), measure.getValue()));
	    //split timestamps
	    if (nMeasures != measures.size()){
		json.append(",");
	    }
	}
	
	//end measurements
	json.append("]}");
	
	return json.toString();
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
