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

package org.apache.lenya.cms.publication;

import java.util.List;

import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.tree.SiteTreeNode;

/**
 * @version $Id$
 */
public final class PublicationHelper {

    private Publication publication;

    /**
     * Ctor.
     * @param publication The publication.
     */
    public PublicationHelper(Publication publication) {
        this.publication = publication;
    }

    /**
     * Returns all documents of a publication.
     * @param area
     * @param language
     * @return An array of document.
     * @throws DocumentException
     * @see Document
     * @see Publication
     */
    public Document[] getAllDocuments(String area, String language) throws DocumentException {
        try {
            List allNodes = getPublication().getSiteTree(area).getNode("/").preOrder();
            Document[] documents = new Document[allNodes.size()-1];
        
            for(int i=1; i<allNodes.size(); i++) {
                documents[i-1] = DefaultDocumentBuilder.getInstance().createDocument(getPublication(), area,
                        ((SiteTreeNode)allNodes.get(i)).getAbsoluteId(), language);
            }
            return documents;
        } catch(SiteException e) {
            throw new DocumentException("Can not access sitetree to get document ids.", e);
        } catch(DocumentBuildException e) {
            throw new DocumentException("Can not build document from id obtained from sitetree.", e);
        }        
    }

    /**
     * @return Returns the publication.
     */
    public Publication getPublication() {
        return publication;
    }
}