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

/* $Id: ResourcePublishingEnvironment.java,v 1.8 2004/03/01 16:18:18 gregor Exp $  */

package org.apache.lenya.cms.publishing;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.log4j.Category;


/**
 * DOCUMENT ME!
 *
 */
public class ResourcePublishingEnvironment extends PublishingEnvironment {
    private static Category log = Category.getInstance(ResourcePublishingEnvironment.class);
    public static final String PARAMETER_RESOURCE_AUTHORING_PATH = "resources-authoring-path";
    public static final String PARAMETER_RESOURCE_LIVE_PATH = "resources-live-path";
    private String resourceAuthoringPath;
    private String resourceLivePath;

    /**
     * Creates a new ResourcePublishingEnvironment object.
     *
     * @param contextPath DOCUMENT ME!
     * @param publicationId DOCUMENT ME!
     */
    public ResourcePublishingEnvironment(String contextPath, String publicationId) {
        super(contextPath, publicationId);
    }

    /**
     * Creates a new ResourcePublishingEnvironment object.
     *
     * @param publicationPath DOCUMENT ME!
     */
    public ResourcePublishingEnvironment(String publicationPath) {
        super(publicationPath);
    }

    /**
     * DOCUMENT ME!
     *
     * @param configuration DOCUMENT ME!
     *
     * @throws org.apache.avalon.framework.configuration.ConfigurationException DOCUMENT ME!
     */
    public void configure(Configuration configuration)
        throws ConfigurationException {
        super.configure(configuration);

        // authoring
        setResourceAuthoringPath(configuration.getChild("authoring").getChild("resource")
                                              .getAttribute("href"));

        // live
        setResourceLivePath(configuration.getChild("live").getChild("resource").getAttribute("href"));
        log.debug("CONFIGURATION:\nresource authoring path=" + getResourceAuthoringPath() +
            "\nresource live path=" + getResourceLivePath());
    }

    /**
     * Get the live resource path.
     *
     * @return a <code>String</code> value
     */
    public String getResourceLivePath() {
        return resourceLivePath;
    }

    /**
     * Set the live resource path.
     *
     * @param path a <code>String</code> value
     */
    protected void setResourceLivePath(String path) {
        resourceLivePath = path;
    }

    /**
     * Get the authoring resource path.
     *
     * @return a <code>String</code> value
     */
    public String getResourceAuthoringPath() {
        return resourceAuthoringPath;
    }

    /**
     * Set the authoring resource path.
     *
     * @param path a <code>String</code> value
     */
    protected void setResourceAuthoringPath(String path) {
        resourceAuthoringPath = path;
    }
}
