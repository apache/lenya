/*
 * JobDataMapWrapper.java
 *
 * Created on November 13, 2002, 10:18 AM
 */

package org.wyona.cms.scheduler;

import org.apache.avalon.framework.parameters.Parameters;
import org.quartz.JobDataMap;

/**
 *
 * @author  ah
 */
public class JobDataMapWrapper {
    
    public static final String SEPARATOR = ".";
    
    public JobDataMapWrapper(String prefix) {
        this(new JobDataMap(), prefix);
    }
    
    /** Creates a new instance of JobDataMapWrapper */
    public JobDataMapWrapper(JobDataMap map, String prefix) {
        this.map = map;
        this.prefix = prefix;
    }

    private JobDataMap map;
    private String prefix;
    
    public String getPrefix() {
        return prefix;
    }
    
    public JobDataMap getMap() {
        return map;
    }
    
    public Parameters getParameters() {
        Parameters parameters = new Parameters();
        String names[] = (String[]) map.keySet().toArray(new String[map.size()]);
        for (int i = 0; i < names.length; i++) {
            if (names[i].startsWith(prefix + SEPARATOR)) {
                parameters.setParameter(
                        getShortName(prefix, names[i]),
                        map.getString(names[i]));
            }
        }
        return parameters;
    }

    public void put(String key, String value) {
        map.put(getFullName(prefix, key), value);
    }
    
    public String get(String key) {
        String names[] = (String[]) map.keySet().toArray(new String[map.size()]);
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            if (name.equals(getFullName(prefix, key))) {
                return map.getString(names[i]);
            }
        }
        return null;
    }
    
    public static String getFullName(String prefix, String key) {
        return prefix + SEPARATOR + key;
    }
    
    public static String getShortName(String prefix, String key) {
        return key.substring(prefix.length() + SEPARATOR.length());
    }
    
}
