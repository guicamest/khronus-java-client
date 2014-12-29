package com.despegar.khronus.jclient;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

public class JsonSerializerTest {
    @Test
    public void toJson_oneMetric_returnsValidJson() {
        JsonSerializer instance = new JsonSerializer(5000l, "demoApp");

        Collection<Measure> measures = new ArrayList<Measure>();
        measures.add(new Timer("responseTime", 1234l, 11111l));
        measures.add(new Timer("responseTime", 456l, 11111l));

        String json = instance.serialize(measures);

        Assert.assertEquals("{ \"metrics\": [{ \"name\":\"demoApp:responseTime\", \"mtype\":\"timer\", \"measurements\":[{ \"ts\":11111, \"values\": [1234,456] }]}]}", json);
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

        Assert.assertEquals("{ \"metrics\": [{ \"name\":\"totalTime\", \"mtype\":\"timer\", \"measurements\":[{ \"ts\":11111, \"values\": [1234,456] }]},{ \"name\":\"responseTime\", \"mtype\":\"timer\", \"measurements\":[{ \"ts\":11111, \"values\": [1234,456] }]}]}", json);
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


        Assert.assertEquals("{ \"metrics\": [{ \"name\":\"responseTime\", \"mtype\":\"timer\", \"measurements\":[{ \"ts\":11111, \"values\": [1234,456] }]},{ \"name\":\"count200\", \"mtype\":\"counter\", \"measurements\":[{ \"ts\":11111, \"values\": [1234,456] }]}]}", json);
    }


}
