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

/* $Id: ResourceFilePublisher.java,v 1.8 2004/03/01 16:18:18 gregor Exp $  */

package org.apache.lenya.cms.publishing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.lenya.cms.task.ExecutionException;
import org.apache.log4j.Category;


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
 * @deprecated use the publish ant task instead.
 */
public class ResourceFilePublisher extends DefaultFilePublisher {
    private static Category log = Category.getInstance(ResourceFilePublisher.class);

	/**
	 *  (non-Javadoc)
	 * @see org.apache.lenya.cms.publishing.DefaultFilePublisher#publishResources(java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
	 */
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
                            ") is not a directory ");
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
