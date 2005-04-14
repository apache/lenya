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

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.ResourcesManager;
import org.apache.tools.ant.BuildException;

/**
 * Resource publisher.
 * 
 * @version $Id$
 */
public class ResourcePublisher extends PublicationTask {

    private String documentId;

    /**
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException {

        ResourcesManager resMgr = null;
        try {
            resMgr = (ResourcesManager) getServiceManager().lookup(ResourcesManager.ROLE);
            Document authoringDocument = getIdentityMap().get(getPublication(),
                    Publication.AUTHORING_AREA,
                    this.documentId);
            Document liveDocument = getIdentityMap().get(getPublication(),
                    Publication.LIVE_AREA,
                    this.documentId);
            resMgr.copyResources(authoringDocument, liveDocument);

        } catch (Exception e) {
            throw new BuildException(e);
        }
        finally {
            if (resMgr != null) {
                getServiceManager().release(resMgr);
            }
        }
    }

    /**
     * Returns the document ID.
     * @return A document ID.
     */
    public String getDocumentId() {
        return this.documentId;
    }

    /**
     * Sets the document ID.
     * @param _documentId A document ID.
     */
    public void setDocumentId(String _documentId) {
        this.documentId = _documentId;
    }

}