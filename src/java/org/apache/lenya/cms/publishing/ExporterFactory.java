/*
 * ExporterFactory.java
 *
 * Created on November 6, 2002, 11:03 AM
 */

package org.wyona.cms.publishing;

import org.apache.log4j.Category;

/**
 *
 * @author  ah
 */
public class ExporterFactory {
    
    static Category log = Category.getInstance(ExporterFactory.class);

    public static Exporter createInstance(Class cl) {
        try {
            return (Exporter) cl.newInstance();
        }
        catch (Exception e) {
            log.error("Cannot load Exporter class!", e);
            return null;
        }
    }
    
}
