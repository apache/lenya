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

package org.apache.lenya.cms.publication.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.site.SiteException;

/**
 * @version $Id$
 */
public final class PublicationHelper {

    private Publication publication;

    /**
     * Ctor.
     * @param _publication The publication.
     */
    public PublicationHelper(Publication _publication) {
        this.publication = _publication;
    }

    /**
     * Returns all documents of a publication in a certain language.
     * @param map The identity map.
     * @param area The area.
     * @param language The language.
     * @return An array of document.
     * @throws DocumentException
     * @see Document
     * @see Publication
     */
    public Document[] getAllDocuments(DocumentIdentityMap map, String area, String language)
            throws DocumentException {
        try {
            Document[] allDocuments = getPublication().getSiteManager().getDocuments(map,
                    this.publication,
                    area);

            List documents = new ArrayList();

            for (int i = 0; i < allDocuments.length; i++) {
                Document doc = allDocuments[i];
                Document languageDoc = doc.getIdentityMap().getFactory().get(getPublication(),
                        doc.getArea(),
                        doc.getId(),
                        language);
                documents.add(languageDoc);
            }

            return (Document[]) documents.toArray(new Document[documents.size()]);
        } catch (SiteException e) {
            throw new DocumentException("Can not access sitetree to get document ids.", e);
        } catch (DocumentBuildException e) {
            throw new DocumentException("Can not build document from id obtained from sitetree.", e);
        }
    }

    /**
     * @return Returns the publication.
     */
    public Publication getPublication() {
        return this.publication;
    }
}