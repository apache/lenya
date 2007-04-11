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

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.repository.RepositoryItem;
import org.apache.lenya.cms.repository.RepositoryItemFactory;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.util.Assert;

/**
 * Factory for sitetree objects.
 * 
 * @version $Id$
 */
public class DocumentStoreFactory extends AbstractLogEnabled implements RepositoryItemFactory {

    protected ServiceManager manager;

    /**
     * Ctor.
     * @param manager The service manager.
     * @param logger The logger.
     */
    public DocumentStoreFactory(ServiceManager manager, Logger logger) {
        this.manager = manager;
        ContainerUtil.enableLogging(this, logger);
    }

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

        Assert.isTrue("key [" + key + "] is invalid!", snippets.length == 3);

        String publicationId = snippets[0];
        String areaName = snippets[1];
        String uuid = snippets[2];
        DocumentStore store;
        try {
            DocumentFactory factory = DocumentUtil.createDocumentFactory(this.manager, session);
            Publication publication = factory.getPublication(publicationId);
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
        DocumentManager docManager = null;
        try {
            Area authoring = publication.getArea(Publication.AUTHORING_AREA);
            Document authoringDoc = authoring.getDocument(uuid, lang);
            docManager = (DocumentManager) this.manager.lookup(DocumentManager.ROLE);
            docManager.copyToArea(authoringDoc, areaName);
        } finally {
            if (docManager != null) {
                this.manager.release(docManager);
            }
        }
    }

    public boolean isSharable() {
        return false;
    }

}