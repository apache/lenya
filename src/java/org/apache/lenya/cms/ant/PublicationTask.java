/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

/* $Id$  */

package org.apache.lenya.cms.ant;

import java.io.File;
import java.io.IOException;

import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Log4JLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.site.tree.SiteTree;
import org.apache.lenya.cms.site.tree.TreeSiteManager;
import org.apache.lenya.cms.task.AntTask;
import org.apache.log4j.Category;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Abstract base class for publication-dependent Ant tasks. It requires some project parameters that
 * are set by the AntTask.
 */
public abstract class PublicationTask extends Task {
    /** Creates a new instance of PublicationTask */
    public PublicationTask() {
    }

    /**
     * Returns the publication directory.
     * 
     * @return a the path to the publication directory as a <code>File</code>
     */
    protected File getPublicationDirectory() {
        return new File(getProject().getProperty(AntTask.PUBLICATION_DIRECTORY));
    }

    /**
     * Return the context prefix.
     * 
     * @return the context-prefix
     */
    protected String getContextPrefix() {
        return getProject().getProperty(AntTask.CONTEXT_PREFIX);
    }

    /**
     * Returns the publication ID.
     * 
     * @return the publication-id
     */
    protected String getPublicationId() {
        return getProject().getProperty(AntTask.PUBLICATION_ID);
    }

    /**
     * Returns the servlet context (e.g., <code>tomcat/webapp/lenya</code>)
     * 
     * @return the servlet-context
     */
    protected File getServletContext() {
        return new File(getProject().getProperty(AntTask.SERVLET_CONTEXT_PATH));
    }

    /**
     * Returns the document identity map.
     * @return An identity map.
     */
    protected DocumentIdentityMap getIdentityMap() {
        if (this.identityMap == null) {
            try {
                PublicationFactory factory = PublicationFactory.getInstance(new ConsoleLogger());
                Publication publication = factory.getPublication(getPublicationId(),
                        getServletContext().getCanonicalPath());
                this.identityMap = new DocumentIdentityMap(publication);
            } catch (IOException e) {
                throw new BuildException(e);
            } catch (PublicationException e) {
                throw new BuildException(e);
            }
        }
        return this.identityMap;
    }

    private DocumentIdentityMap identityMap;

    /**
     * Get the publication
     * 
     * @return the publication
     * 
     * @throws BuildException if the publication could not be found
     */
    protected Publication getPublication() throws BuildException {
        return getIdentityMap().getPublication();
    }

    /**
     * Utility method for assertion that a string is != null and != ""
     * 
     * @param string the string to check
     */
    protected void assertString(String string) {
        assert (string != null) && !string.equals("");
    }

    protected SiteTree getSiteTree(String area) {
        SiteTree tree;
        try {
            SiteManager manager = getPublication().getSiteManager(getIdentityMap());
            if (!(manager instanceof TreeSiteManager)) {
                throw new RuntimeException("Only supported for site trees.");
            }
            tree = ((TreeSiteManager) manager).getTree(area);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return tree;
    }

}