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
package org.apache.lenya.cms.site.usecases;

import java.util.ArrayList;
import java.util.List;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentIdentityMap;

/**
 * Utility methods for site usecases.
 * 
 * @version $Id:$
 */
public class SiteUtility {

    /**
     * Checks if a document can be created. This is the case if the document ID
     * is valid and the document does not yet exist.
     * @param parent The parent of the document.
     * @param nodeId The node ID.
     * @param language The language.
     * @return An array of error messages. The array is empty if the document
     *         can be created.
     * @throws DocumentBuildException if an error occurs.
     * @throws DocumentException if an error occurs.
     */
    public String[] canCreate(Document parent, String nodeId, String language)
            throws DocumentBuildException, DocumentException {

        List errorMessages = new ArrayList();
        String newDocumentId = parent.getId() + "/" + nodeId;

        DocumentIdentityMap identityMap = parent.getIdentityMap();

        if (nodeId.equals("")) {
            errorMessages.add("The document ID is required.");
        } else if (nodeId.indexOf("/") > -1) {
            errorMessages.add("The document ID may not contain a slash ('/').");
        } else if (identityMap.getFactory().isValidDocumentId(newDocumentId)) {
            Document newDocument = identityMap.getFactory().get(parent.getArea(),
                    newDocumentId,
                    language);

            if (newDocument.exists()) {
                errorMessages.add("A document with this ID already exists.");
            }
        } else {
            errorMessages.add("This document ID is not valid.");
        }

        return (String[]) errorMessages.toArray(new String[errorMessages.size()]);
    }

}