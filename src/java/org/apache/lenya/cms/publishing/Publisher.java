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

/* $Id: Publisher.java,v 1.12 2004/03/01 16:18:18 gregor Exp $  */

package org.apache.lenya.cms.publishing;


/**
 * A Publisher is used to copy XML sources from the authoring server to the pending server.
 */
public interface Publisher {
    /**
     * Publish a document.
     *
     * @param publicationPath path to the publication
     * @param authoringPath path to the authoring directory
     * @param treeAuthoringPath path to the authoring tree
     * @param resourcesAuthoringPath path to the authoring resources
     * @param resourcesLivePath path to the live resources
     * @param livePath path to the live directory
     * @param treeLivePath path to the live tree
     * @param replicationPath path to the replication directory
     * @param sources array of xml sources to be published
     * @exception PublishingException if an error occurs
     */
    void publish(String publicationPath, String authoringPath, String treeAuthoringPath,
        String resourcesAuthoringPath, String livePath, String treeLivePath,
        String resourcesLivePath, String replicationPath, String[] sources)
        throws PublishingException;
}
