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

package org.apache.lenya.cms.ant;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.ResourcesManager;
import org.apache.lenya.util.FileUtil;
import org.apache.tools.ant.BuildException;

/**
 * Resource publisher.
 *
 * @version $Id:$
 */
public class ResourcePublisher extends PublicationTask {

    private String documentId;

    /**
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException {

        try {
            Document authoringDocument = getIdentityMap().get(Publication.AUTHORING_AREA, documentId);
            ResourcesManager authoringManager = new ResourcesManager(authoringDocument);

            Document liveDocument = getIdentityMap().get(Publication.LIVE_AREA, documentId);
            ResourcesManager liveManager = new ResourcesManager(liveDocument);
            
            // find all resource files and their associated meta files
            List resourcesList =
                new ArrayList(Arrays.asList(authoringManager.getResources()));
            resourcesList.addAll(
                Arrays.asList(authoringManager.getMetaFiles()));
            File[] resources =
                (File[])resourcesList.toArray(new File[resourcesList.size()]);
            File liveDirectory = liveManager.getPath();
            
            for (int i = 0; i < resources.length; i++) {
                File liveResource = new File(liveDirectory, resources[i].getName());
                String destPath = liveResource.getAbsolutePath();

                log("Copy file [" + resources[i].getAbsolutePath() + "] to [" + destPath + "]");
                FileUtil.copy(resources[i].getAbsolutePath(), destPath);
            }

        } catch (Exception e) {
            throw new BuildException(e);
        }
    }

    /**
     * Returns the document ID.
     * @return A document ID.
     */
    public String getDocumentId() {
        return documentId;
    }

    /**
     * Sets the document ID.
     * @param documentId A document ID.
     */
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

}
