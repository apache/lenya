/*
 * PublicationTask.java
 *
 * Created on 6. Mai 2003, 18:08
 */

package org.apache.lenya.cms.ant;

import java.io.File;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.task.AntTask;
import org.apache.tools.ant.Task;

/**
 *
 * @author  andreas
 */
public abstract class PublicationTask
    extends Task {
    
    /** Creates a new instance of PublicationTask */
    public PublicationTask() {
    }
    
    protected File getPublicationDirectory() {
        return new File(getProject().getProperty(AntTask.PUBLICATION_DIRECTORY));
    }
    
    protected String getPublicationId() {
        return getProject().getProperty(AntTask.PUBLICATION_ID);
    }
    
    protected File getServletContext() {
        return new File(getProject().getProperty(AntTask.SERVLET_CONTEXT_PATH));
    }
    
}
