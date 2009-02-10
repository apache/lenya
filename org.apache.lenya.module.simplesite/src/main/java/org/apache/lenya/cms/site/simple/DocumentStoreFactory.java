/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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
package org.apache.lenya.cms.site.simple;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.cocoon.util.AbstractLogEnabled;
import org.apache.commons.lang.Validate;
import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.repository.RepositoryItem;
import org.apache.lenya.cms.repository.RepositoryItemFactory;
import org.apache.lenya.cms.repository.Session;

/**
 * Factory for sitetree objects.
 * 
 * @version $Id$
 */
public class DocumentStoreFactory extends AbstractLogEnabled implements RepositoryItemFactory {

    private DocumentManager documentManager;

    /**
     * @see org.apache.lenya.cms.repository.RepositoryItemFactory#getItemType()
     */
    public String getItemType() {
        return DocumentStore.IDENTIFIABLE_TYPE;
    }

    /**
     * @see org.apache.lenya.cms.repository.RepositoryItemFactory#buildItem(org.apache.lenya.cms.repository.Session,
     *      java.lang.String)
     */
    public RepositoryItem buildItem(Session session, String key) throws RepositoryException {
        String[] snippets = key.split(":");

        Validate.isTrue(snippets.length == 3, "key invalid: ", key);

        String publicationId = snippets[0];
        String areaName = snippets[1];
        String uuid = snippets[2];
        DocumentStore store;
        try {
            org.apache.lenya.cms.publication.Session pubSession = (org.apache.lenya.cms.publication.Session) session;
            Publication publication = pubSession.getPublication(publicationId);
            Area area = publication.getArea(areaName);
            String lang = publication.getDefaultLanguage();

            if (!area.contains(uuid, lang)) {
                createAreaVersion(publication, areaName, uuid, lang);
            }

            Document doc = area.getDocument(uuid, lang);

            store = new DocumentStore(doc, getLogger());
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
        return store;
    }

    protected void createAreaVersion(Publication publication, String areaName, String uuid,
            String lang) throws PublicationException, ServiceException {
        Area authoring = publication.getArea(Publication.AUTHORING_AREA);
        Document authoringDoc = authoring.getDocument(uuid, lang);
        getDocumentManager().copyToArea(authoringDoc, areaName);
    }

    public boolean isSharable() {
        return false;
    }

    /**
     * TODO: Bean wiring
     */
    public void setDocumentManager(DocumentManager documentManager) {
        this.documentManager = documentManager;
    }

    public DocumentManager getDocumentManager() {
        return documentManager;
    }

}