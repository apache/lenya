/*
 * ServletJob.java
 *
 * Created on November 5, 2002, 4:41 PM
 */

package org.wyona.cms.scheduler;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.avalon.framework.parameters.Parameters;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.quartz.*;

/**
 *
 * @author  ah
 */
public abstract class ServletJob
    implements Job {
        
    /**
     * Creates the job data from an HTTP request.
     */
    public abstract JobDataMap createJobData(
            String servletContextPath,
            HttpServletRequest request);
    
    /**
     * Loads the job data from an XML element.
     */
    public abstract JobDetail load(Element element);

    /**
     * Saves the job data to an XML element.
     */
    public abstract Element save(JobDetail jobDetail);
        
}
