/*
 * AbstractTask.java
 *
 * Created on November 7, 2002, 1:12 PM
 */

package org.wyona.cms.task;

import java.util.StringTokenizer;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.log4j.Category;

/**
 *
 * @author  ah
 */
public abstract class AbstractTask
    implements Task {
    
    static Category log = Category.getInstance(AbstractTask.class);

    private Parameters parameters = new Parameters();
    
    public Parameters getParameters() {
        Parameters params = new Parameters();
        params = params.merge(parameters);
        return params;
    }
    
    public void parameterize(Parameters parameters) {
        log.debug("Initializing parameters");
        for (int i = 0; i < parameters.getNames().length; i++) {
            log.debug("Setting parameter " + parameters.getNames()[i] + " to " +
                parameters.getParameter(parameters.getNames()[i], "default"));
        }
        this.parameters = this.parameters.merge(parameters);
    }
    
    private String label = "default task";
    
    /** Return the label to be displayed.
     *
     */
    public String getLabel() {
        return label;
    }    
    
    public void setLabel(String label) {
        this.label = label;
    }
    
}
