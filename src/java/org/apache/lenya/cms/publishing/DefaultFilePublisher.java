/*
 * $Id: DefaultFilePublisher.java,v 1.7 2003/03/04 17:46:35 gregor Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 wyona. All rights reserved.
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
 *    by wyona (http://www.wyona.org)"
 *
 * 4. The name "wyona" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@wyona.org
 *
 * 5. Products derived from this software may not be called "wyona" nor may "wyona"
 *    appear in their names without prior written permission of wyona.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by wyona (http://www.wyona.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY wyona "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. wyona WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL wyona BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF wyona HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. wyona WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Wyona includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.lenya.cms.publishing;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.log4j.Category;
import org.lenya.cms.task.ExecutionException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.StringTokenizer;


/**
 * Describe class <code>DefaultFilePublisher</code> here. The following task parameters must be provided:<br/
 * ><code><strong>publication-id</strong></code>: the absolute path of this publication<br/
 * ><code><strong>authoring-path</strong></code>: the authoring path<br/
 * ><code><strong>tree-authoring-path</strong></code>: the location of the <code>tree.xml</code> file<br/
 * ><code><strong>live-path</strong></code>: the live path<br/
 * ><code><strong>tree-live-path</strong></code>: the location of the <code>tree.xml</code> file<br/
 * ><code><strong>replication-path</strong></code>: the replication path, where sources are waiting
 * for replication<br/
 * ><code><strong>sources</strong></code>: a comma-separated list of files to publish<br/>
 *
 * @author <a href="mailto:andreas.hartmann@wyona.org">Andreas Hartmann</a>
 * @author <a href="mailto:michael.wechner@wyona.org">Michael Wechner</a>
 */
public class DefaultFilePublisher extends AbstractFilePublisher {
    static Category log = Category.getInstance(DefaultFilePublisher.class);
    public static final String PARAMETER_SOURCES = "sources";

    /**
     * Default implementation of <code>publish</code> which simply copies the files from the
     * absoluteAuthoringPath to the absoluteLivePath.
     *
     * @param publicationPath DOCUMENT ME!
     * @param authoringPath DOCUMENT ME!
     * @param treeAuthoringPath DOCUMENT ME!
     * @param livePath DOCUMENT ME!
     * @param treeLivePath DOCUMENT ME!
     * @param replicationPath DOCUMENT ME!
     * @param sources DOCUMENT ME!
     */
    public void publish(String publicationPath, String authoringPath, String treeAuthoringPath,
        String livePath, String treeLivePath, String replicationPath, String[] sources)
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
                throw new PublishingException("Document not published: Source file (" + sourceFile + ") not found!", fnfe);
            } catch (IOException ioe) {
                throw new PublishingException("Document not published: " + sourceFile + " " + destinationFile, ioe);
            }
        }

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
     *
     */
    public void execute(String contextPath)
            throws ExecutionException {
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
                getParameters().getParameter(PublishingEnvironment.PARAMETER_LIVE_PATH),
                getParameters().getParameter(PublishingEnvironment.PARAMETER_TREE_LIVE_PATH),
                getParameters().getParameter(PublishingEnvironment.PARAMETER_REPLICATION_PATH),
                sources);
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }
}
