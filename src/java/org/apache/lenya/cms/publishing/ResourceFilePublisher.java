/*
 * $Id: ResourceFilePublisher.java,v 1.3 2003/05/30 20:59:11 andreas Exp $
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
package org.apache.lenya.cms.publishing;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.excalibur.io.FileUtil;

import org.apache.log4j.Category;
import org.apache.lenya.cms.task.ExecutionException;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
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

    protected void publishResources(String publicationPath,
				    String resourcesAuthoringPath,
				    String resourcesLivePath,
				    String[] sources)
	throws PublishingException {

        String absoluteResourceAuthoringPath = publicationPath + resourcesAuthoringPath;
        String absoluteResourceLivePath = publicationPath + resourcesLivePath;

	log.debug("Publishing resources from " + absoluteResourceAuthoringPath
		  + " to " + absoluteResourceLivePath);

        for (int index = 0; index < sources.length; index++) {
            File sourceDir = new File(absoluteResourceAuthoringPath +
				      FileUtil.getPath(sources[index]));
            File destinationDir = new File(absoluteResourceLivePath +
					   FileUtil.getPath(sources[index]));

	    if (sourceDir == null || !sourceDir.isDirectory()) {
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
			log.debug("COPY\nresource: " + resourceSources[j]
				  + "\nto " + destinationDir);
		    } catch (FileNotFoundException fnfe) {
			throw new PublishingException("Resource not published: Source file ("
						      + resourceSources[j] + ") not found!", fnfe);
		    } catch (IllegalArgumentException iae) {
			throw new PublishingException("Resource not published: Live resources path ("
						      + destinationDir + ")is not a directory ");
		    } catch (IOException ioe) {
			throw new PublishingException("Resource not published: "
						      + resourceSources[j] + " "
						      + destinationDir, ioe);
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
    public void execute(String contextPath)
	throws ExecutionException {
        try {
            String publicationId = getParameters().getParameter(PARAMETER_PUBLICATION_ID);

            Parameters taskParameters = new Parameters();

            ResourcePublishingEnvironment environment =
		new ResourcePublishingEnvironment(contextPath, publicationId);

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
