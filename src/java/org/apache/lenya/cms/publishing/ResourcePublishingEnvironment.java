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

package org.apache.lenya.cms.publishing;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.log4j.Logger;


/**
 * Resource Publishing Environment
 *
 */
public class ResourcePublishingEnvironment extends PublishingEnvironment {
    private static Logger log = Logger.getLogger(ResourcePublishingEnvironment.class);
    /**
     * <code>PARAMETER_RESOURCE_AUTHORING_PATH</code> Authoring path parameter
     */
    public static final String PARAMETER_RESOURCE_AUTHORING_PATH = "resources-authoring-path";
    /**
     * <code>PARAMETER_RESOURCE_LIVE_PATH</code> Live path parameter
     */
    public static final String PARAMETER_RESOURCE_LIVE_PATH = "resources-live-path";
    private String resourceAuthoringPath;
    private String resourceLivePath;

    /**
     * Creates a new ResourcePublishingEnvironment object.
     * @param contextPath The context path
     * @param publicationId The publication id
     */
    public ResourcePublishingEnvironment(String contextPath, String publicationId) {
        super(contextPath, publicationId);
    }

    /**
     * Creates a new ResourcePublishingEnvironment object.
     * @param publicationPath The publication path
     */
    public ResourcePublishingEnvironment(String publicationPath) {
        super(publicationPath);
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
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
     * @return a <code>String</code> value
     */
    public String getResourceLivePath() {
        return this.resourceLivePath;
    }

    /**
     * Set the live resource path.
     * @param path a <code>String</code> value
     */
    protected void setResourceLivePath(String path) {
        this.resourceLivePath = path;
    }

    /**
     * Get the authoring resource path.
     * @return a <code>String</code> value
     */
    public String getResourceAuthoringPath() {
        return this.resourceAuthoringPath;
    }

    /**
     * Set the authoring resource path.
     * @param path a <code>String</code> value
     */
    protected void setResourceAuthoringPath(String path) {
        this.resourceAuthoringPath = path;
    }
}
