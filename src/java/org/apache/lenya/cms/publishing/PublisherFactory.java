/*
 * PublisherFactory.java
 *
 * Created on November 6, 2002, 11:02 AM
 */

package org.wyona.cms.publishing;

import org.apache.log4j.Category;

/**
 *
 * @author  ah
 */
public class PublisherFactory {
    
    static Category log = Category.getInstance(PublisherFactory.class);

    public static Publisher createInstance(Class cl) {
        try {
            return (Publisher) cl.newInstance();
        }
        catch (Exception e) {
            log.error("Cannot load Publisher class!", e);
            return null;
        }
    }
    
}
