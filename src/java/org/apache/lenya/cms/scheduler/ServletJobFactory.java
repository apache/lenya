/*
 * ServletJobFactory.java
 *
 * Created on November 8, 2002, 2:08 PM
 */

package org.wyona.cms.scheduler;

import org.apache.log4j.Category;

/**
 *
 * @author  ah
 */
public class ServletJobFactory {

    static Category log = Category.getInstance(ServletJobFactory.class);

    public static ServletJob createJob(String jobClassName) {
        try {
            Class cl = Class.forName(jobClassName);
            return createJob(cl);
        }
        catch (Exception e) {
            log.error("Cannot create Job instance: " + e);
            return null;
        }
    }
    
    public static ServletJob createJob (Class cl) {
        try {
            ServletJob job = (ServletJob) cl.newInstance();
            return job;
        }
        catch (Exception e) {
            log.error("Cannot create Job instance: " + e);
            return null;
        }
    }
    
    
}
