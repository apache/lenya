/*
 * PublicationTask.java
 *
 * Created on 6. Mai 2003, 18:08
 */

package org.apache.lenya.cms.ant;

import java.io.File;
import java.io.IOException;

import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.cms.task.AntTask;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Abstract base class for publication-dependent Ant tasks.
 * It requires some project parameters that are set by the AntTask.
 * @author  <a href="mailto:andreas@apache.org"/>
 */
public abstract class PublicationTask extends Task {

    /** Creates a new instance of PublicationTask */
    public PublicationTask() {
    }

    /**
     * Returns the publication directory.
     */
    protected File getPublicationDirectory() {
        return new File(getProject().getProperty(AntTask.PUBLICATION_DIRECTORY));
    }

    /**
     * Returns the publication ID.
     */
    protected String getPublicationId() {
        return getProject().getProperty(AntTask.PUBLICATION_ID);
    }

    /**
     * Returns the servlet context (e.g., <code>tomcat/webapp/lenya</code>)
     */
    protected File getServletContext() {
        return new File(getProject().getProperty(AntTask.SERVLET_CONTEXT_PATH));
    }

    protected Publication getPublication()
        throws BuildException {
        try {
            return PublicationFactory.getPublication(
                getPublicationId(),
                getServletContext().getCanonicalPath());
        } catch (IOException e) {
            throw new BuildException(e);
        }
    }

    protected void assertString(String string) {
        assert string != null && !string.equals(""); 
    }

}
