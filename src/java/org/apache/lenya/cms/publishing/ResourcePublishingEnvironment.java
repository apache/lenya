/*
$Id: ResourcePublishingEnvironment.java,v 1.7 2004/02/02 02:50:39 stefano Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
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
