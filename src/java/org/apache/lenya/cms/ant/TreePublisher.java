/*
 * TreePublisher.java
 *
 * Created on 7. Mai 2003, 18:00
 */

package org.apache.lenya.cms.ant;

import org.apache.lenya.cms.publishing.PublishingException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 *
 * @author  edith
 */
public class TreePublisher
    extends PublicationTask {
    
    /** Creates a new instance of TreePublisher */
    public TreePublisher() {
    }
    
    private String absoluteTreeLivePath;

    protected String getAbsoluteTreeLivePath() {
        return absoluteTreeLivePath;
    }
    
    public void setAbsoluteTreeLivePath(String absoluteTreeLivePath) {
        this.absoluteTreeLivePath = absoluteTreeLivePath;
    }
    
    private String absoluteTreeAuthoringPath;
    
    protected String getAbsoluteTreeAuthoringPath() {
        return absoluteTreeAuthoringPath;
    }
    
    protected void setAbsoluteTreeAuthoringPath() {
        this.absoluteTreeAuthoringPath = absoluteTreeAuthoringPath;
    }
    
    public void publish(String absoluteTreeAuthoringPath, String absoluteTreeLivePath)
        throws PublishingException {
    }
    
    public void execute()
        throws BuildException {
            
        try {
            log("Absolute Tree Authoring Path: " + getAbsoluteTreeAuthoringPath());
            log("Absolute Tree Live Path: " + getAbsoluteTreeLivePath());

            publish (
                getAbsoluteTreeAuthoringPath(),
                getAbsoluteTreeLivePath());
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }

}
