package org.apache.lenya.cms.cocoon.components.source.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceFactory;
import org.apache.excalibur.source.SourceNotFoundException;

public class VirtualSourceFactory implements SourceFactory, ThreadSafe {
    static private Map sources = new HashMap();
    static private Random random = new Random();
    public Source getSource(String plocation, Map parameters) throws SourceNotFoundException {
        String location = plocation;
        // System.out.println("VS LOC=" + location);
        int pos = location.lastIndexOf("/");
        if ((pos > -1) && (pos + 1 < location.length()))
            location = location.substring(pos + 1);
        pos = location.lastIndexOf(":");
        if ((pos > -1) && (pos + 1 < location.length()))
            location = location.substring(pos + 1);
        if (sources.containsKey(location))
            return (Source) sources.get(location);
        throw new SourceNotFoundException("VirtualSource not found: " + location);
    }
    public void release(Source source1) {
        sources = null;
    }
    static public String addSource(Source source) {
        return addSource(reserve(), source);
    }
    static public String addSource(String key, Source source) {
        sources.put(key, source);
        return key;
    }
    static public synchronized String reserve() {
        String key = Integer.toString(random.nextInt(1000)) + getDateString();
        while (hasKey(key))
            key = key + Integer.toString(random.nextInt(1000));
        sources.put(key, null);
        return key;
    }
    static public void releaseSource(String key) {
        if (hasKey(key))
            sources.remove(key);
    }
    static public boolean hasKey(String key) {
        return sources.containsKey(key);
    }
    static private String getDateString() {
        return Long.toString(new java.util.Date().getTime());
    }
}