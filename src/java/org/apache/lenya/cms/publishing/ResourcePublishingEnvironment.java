/*
 * $Id: ResourcePublishingEnvironment.java,v 1.2 2003/03/27 13:25:39 gregor Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.lenya.cms.publishing;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.log4j.Category;



/**
 * DOCUMENT ME!
 *
 */
public class ResourcePublishingEnvironment extends PublishingEnvironment {
    static Category log = Category.getInstance(ResourcePublishingEnvironment.class);

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
	setResourceAuthoringPath(configuration.getChild("authoring").getChild("resource").getAttribute("href"));

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
