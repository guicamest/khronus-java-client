package com.despegar.khronus.jclient.buffer;

import com.despegar.khronus.jclient.Measure;

/**
 * Dummy to avoid buffering and do nothing. 
 *
 */
public class DummyBuffer implements Buffer {

    @Override
    public void add(Measure measure) {
    }

    @Override
    public void shutdown() {
    }

}
