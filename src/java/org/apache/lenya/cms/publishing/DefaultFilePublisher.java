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

/* $Id: DefaultFilePublisher.java,v 1.23 2004/03/01 16:18:18 gregor Exp $  */

package org.apache.lenya.cms.publishing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.lenya.cms.task.ExecutionException;
import org.apache.log4j.Category;


/**
 * The <code>DefaultFilePublisher</code> is a task that copies XML
 * source files from the authoring directory to another location,
 * usually the live directory.
 *
 * The following task parameters must be provided:<br/
 * ><code><strong>publication-id</strong></code>: the publication ID<br/
 * ><code><strong>authoring-path</strong></code>: the authoring path<br/
 * ><code><strong>tree-authoring-path</strong></code>: the location of the <code>tree.xml</code> file<br/
 * ><code><strong>live-path</strong></code>: the live path<br/
 * ><code><strong>tree-live-path</strong></code>: the location of the <code>tree.xml</code> file<br/
 * ><code><strong>replication-path</strong></code>: the replication path, where sources are waiting
 * for replication<br/
 * ><code><strong>sources</strong></code>: a comma-separated list of files to publish<br/>
 *
 *  * @deprecated use the publish ant task instead.
 */
public class DefaultFilePublisher extends AbstractFilePublisher {
    private static Category log = Category.getInstance(DefaultFilePublisher.class);
    public static final String PARAMETER_SOURCES = "sources";

    /**
     * Default implementation of <code>publish</code> which simply
     * copies the files from the absoluteAuthoringPath to the
     * absoluteLivePath.
     *
     * @param publicationPath DOCUMENT ME!
     * @param authoringPath DOCUMENT ME!
     * @param treeAuthoringPath DOCUMENT ME!
     * @param resourcesAuthoringPath path to authoring resources base directory
     * @param livePath DOCUMENT ME!
     * @param treeLivePath DOCUMENT ME!
     * @param resourcesLivePath path to live resources base directory
     * @param replicationPath DOCUMENT ME!
     * @param sources DOCUMENT ME!
     * @exception PublishingException if an error occurs
     */
    public void publish(String publicationPath, String authoringPath, String treeAuthoringPath,
        String resourcesAuthoringPath, String livePath, String treeLivePath,
        String resourcesLivePath, String replicationPath, String[] sources)
        throws PublishingException {
        log.debug("PUBLICATION: " + publicationPath);
        log.debug("CONFIGURATION:\nauthoring path=" + authoringPath + "\nlive path=" + livePath);

        // Set absolute paths
        String absoluteAuthoringPath = publicationPath + authoringPath;
        String absoluteTreeAuthoringPath = publicationPath + treeAuthoringPath;
        String absoluteLivePath = publicationPath + livePath;
        String absoluteTreeLivePath = publicationPath + treeLivePath;
        String absoluteReplicationPath = publicationPath + replicationPath;

        log.debug("DefaultFilePublisher.publish() has been called.");

        for (int index = 0; index < sources.length; index++) {
            File sourceFile = new File(absoluteAuthoringPath + sources[index]);
            File destinationFile = new File(absoluteLivePath + sources[index]);
            File destinationReplicationFile = new File(absoluteReplicationPath + sources[index]);

            try {
                copyFile(sourceFile, destinationFile);
                log.debug("Document published: " + sourceFile + " " + destinationFile);
                copyFile(sourceFile, destinationReplicationFile);
                log.debug("Document ready for replication: " + sourceFile + " " +
                    destinationReplicationFile);
            } catch (FileNotFoundException fnfe) {
                throw new PublishingException("Document not published: Source file (" + sourceFile +
                    ") not found!", fnfe);
            } catch (IOException ioe) {
                throw new PublishingException("Document not published: " + sourceFile + " " +
                    destinationFile, ioe);
            }
        }

        // Update resources
        publishResources(publicationPath, resourcesAuthoringPath, resourcesLivePath, sources);

        // Update (copy) tree
        try {
            copyFile(new File(absoluteTreeAuthoringPath), new File(absoluteTreeLivePath));
            log.debug("COPY\ntree source=" + absoluteTreeAuthoringPath + "\ntree destination=" +
                absoluteTreeLivePath);
            log.debug("Tree published");
        } catch (IOException ioe) {
            throw new PublishingException("Tree not published: " + absoluteTreeAuthoringPath + " " +
                absoluteTreeLivePath, ioe);
        }
    }

    /**
     * Default implementation of <code>execute</code> (to implement
     * the Task interface) which basically parses the parameters and
     * invokes the publish method.
     * @param contextPath a <code>String</code> value
     * @exception ExecutionException if an error occurs
     */
    public void execute(String contextPath) throws ExecutionException {
        log.debug(".execute(): Context Path: " + contextPath);

        try {
            String publicationId = getParameters().getParameter(PARAMETER_PUBLICATION_ID);

            Parameters taskParameters = new Parameters();

            PublishingEnvironment environment = new PublishingEnvironment(contextPath, publicationId);

            // read default parameters from PublishingEnvironment
            taskParameters.setParameter(PublishingEnvironment.PARAMETER_AUTHORING_PATH,
                environment.getAuthoringPath());
            taskParameters.setParameter(PublishingEnvironment.PARAMETER_TREE_AUTHORING_PATH,
                environment.getTreeAuthoringPath());
            taskParameters.setParameter(PublishingEnvironment.PARAMETER_LIVE_PATH,
                environment.getLivePath());
            taskParameters.setParameter(PublishingEnvironment.PARAMETER_TREE_LIVE_PATH,
                environment.getTreeLivePath());

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
                null, getParameters().getParameter(PublishingEnvironment.PARAMETER_LIVE_PATH),
                getParameters().getParameter(PublishingEnvironment.PARAMETER_TREE_LIVE_PATH), null,
                getParameters().getParameter(PublishingEnvironment.PARAMETER_REPLICATION_PATH),
                sources);
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

    /**
     * A template method to publish the resources. The default
     * implementation doesn't deal with resources. It simply ignores
     * them and assumes that they end up in the live directory be some
     * other means.
     *
     * @param publicationPath path to the publication
     * @param resourcesAuthoringPath authoring path for the resources
     * @param resourcesLivePath live path for the resources
     * @param sources array of docIds
     * 
     * @throws PublishingException if the publishing of resources fails
     */
    protected void publishResources(String publicationPath, String resourcesAuthoringPath,
        String resourcesLivePath, String[] sources) throws PublishingException {
    }
}
