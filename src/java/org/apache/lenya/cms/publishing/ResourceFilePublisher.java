/*
$Id
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

import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.avalon.framework.parameters.Parameters;

import org.apache.lenya.cms.task.ExecutionException;

import org.apache.log4j.Category;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.lang.IllegalArgumentException;

import java.util.StringTokenizer;


/**
 * The <code>ResourceFilePublisher</code> is a task that extends the
 * <code>DefaultFilePublisher</code>. In addition to the default
 * publisher it also copies resource files (e.g. images) from the
 * authoring directory to another location, usually the live
 * directory.
 *
 * In addition to the parameters that have to be provided for
 * <code>DefaultFilePublisher</code> the following parameters have to
 * be specified:<br/>
 * <code><strong>resources-authoring-path</strong></code>: the
 * authoring path for resources<br/>
 * <code><strong>resources-live-path</strong></code>: the live path
 * for resources<br/>
 *
 * @author <a href="mailto:christian.egli@lenya.org">Christian Egli</a>
 */
public class ResourceFilePublisher extends DefaultFilePublisher {
    private static Category log = Category.getInstance(ResourceFilePublisher.class);

    protected void publishResources(String publicationPath, String resourcesAuthoringPath,
        String resourcesLivePath, String[] sources) throws PublishingException {
        String absoluteResourceAuthoringPath = publicationPath + resourcesAuthoringPath;
        String absoluteResourceLivePath = publicationPath + resourcesLivePath;

        log.debug("Publishing resources from " + absoluteResourceAuthoringPath + " to " +
            absoluteResourceLivePath);

        for (int index = 0; index < sources.length; index++) {
            File sourceDir = new File(absoluteResourceAuthoringPath +
                    FileUtil.getPath(sources[index]));
            File destinationDir = new File(absoluteResourceLivePath +
                    FileUtil.getPath(sources[index]));

            if ((sourceDir == null) || !sourceDir.isDirectory()) {
                // Hmm, the source dir doesn't exist. Ok, this
                // document possibly doesn't have any related
                // resources (e.g. images, etc.). Skip it.
                continue;
            }

            log.debug("Copying resources from " + sourceDir + " to " + destinationDir);

            File[] resourceSources = sourceDir.listFiles();

            for (int j = 0; j < resourceSources.length; j++) {
                if (resourceSources[j].isFile()) {
                    try {
                        // only copy resources that are files
                        FileUtil.copyFileToDirectory(resourceSources[j], destinationDir);
                        log.debug("COPY\nresource: " + resourceSources[j] + "\nto " +
                            destinationDir);
                    } catch (FileNotFoundException fnfe) {
                        throw new PublishingException("Resource not published: Source file (" +
                            resourceSources[j] + ") not found!", fnfe);
                    } catch (IllegalArgumentException iae) {
                        throw new PublishingException(
                            "Resource not published: Live resources path (" + destinationDir +
                            ")is not a directory ");
                    } catch (IOException ioe) {
                        throw new PublishingException("Resource not published: " +
                            resourceSources[j] + " " + destinationDir, ioe);
                    }
                }
            }
        }
    }

    /**
     * Implementation of <code>execute</code> (to implement the Task
     * interface) which also parses the parameters for resource
     * publishing and invokes the publish method.
     * @param contextPath a <code>String</code> value
     * @exception ExecutionException if an error occurs
     */
    public void execute(String contextPath) throws ExecutionException {
        try {
            String publicationId = getParameters().getParameter(PARAMETER_PUBLICATION_ID);

            Parameters taskParameters = new Parameters();

            ResourcePublishingEnvironment environment = new ResourcePublishingEnvironment(contextPath,
                    publicationId);

            // read default parameters from PublishingEnvironment
            taskParameters.setParameter(PublishingEnvironment.PARAMETER_AUTHORING_PATH,
                environment.getAuthoringPath());
            taskParameters.setParameter(PublishingEnvironment.PARAMETER_TREE_AUTHORING_PATH,
                environment.getTreeAuthoringPath());
            taskParameters.setParameter(ResourcePublishingEnvironment.PARAMETER_RESOURCE_AUTHORING_PATH,
                environment.getResourceAuthoringPath());
            taskParameters.setParameter(PublishingEnvironment.PARAMETER_LIVE_PATH,
                environment.getLivePath());
            taskParameters.setParameter(PublishingEnvironment.PARAMETER_TREE_LIVE_PATH,
                environment.getTreeLivePath());
            taskParameters.setParameter(ResourcePublishingEnvironment.PARAMETER_RESOURCE_LIVE_PATH,
                environment.getResourceLivePath());

            taskParameters.setParameter(PublishingEnvironment.PARAMETER_REPLICATION_PATH,
                environment.getReplicationDirectory());

            taskParameters.merge(getParameters());
            parameterize(taskParameters);

            String sourcesString = getParameters().getParameter(PARAMETER_SOURCES);
            StringTokenizer st = new StringTokenizer(sourcesString, ",");
            String[] sources = new String[st.countTokens()];
            int i = 0;

            while (st.hasMoreTokens()) {
                sources[i++] = st.nextToken();
            }

            publish(PublishingEnvironment.getPublicationPath(contextPath, publicationId),
                getParameters().getParameter(PublishingEnvironment.PARAMETER_AUTHORING_PATH),
                getParameters().getParameter(PublishingEnvironment.PARAMETER_TREE_AUTHORING_PATH),
                getParameters().getParameter(ResourcePublishingEnvironment.PARAMETER_RESOURCE_AUTHORING_PATH),
                getParameters().getParameter(PublishingEnvironment.PARAMETER_LIVE_PATH),
                getParameters().getParameter(PublishingEnvironment.PARAMETER_TREE_LIVE_PATH),
                getParameters().getParameter(ResourcePublishingEnvironment.PARAMETER_RESOURCE_LIVE_PATH),
                getParameters().getParameter(PublishingEnvironment.PARAMETER_REPLICATION_PATH),
                sources);
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }
}
