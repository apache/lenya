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
package org.apache.lenya.cms.site.tree2;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.repository.RepositoryItem;
import org.apache.lenya.cms.repository.RepositoryItemFactory;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.repository.SharedItemStore;
import org.apache.lenya.cms.site.tree.SiteTree;

/**
 * Factory for sitetree objects.
 * 
 * @version $Id: SiteTreeFactory.java 179568 2005-06-02 09:27:26Z jwkaltz $
 */
public class SiteTreeFactory extends AbstractLogEnabled implements RepositoryItemFactory {

    protected ServiceManager manager;

    /**
     * Ctor.
     * @param manager The service manager.
     * @param logger The logger.
     */
    public SiteTreeFactory(ServiceManager manager, Logger logger) {
        this.manager = manager;
        ContainerUtil.enableLogging(this, logger);
    }

    public RepositoryItem buildItem(Session session, String key) throws RepositoryException {
        String[] snippets = key.split(":");
        String publicationId = snippets[0];
        String areaName = snippets[1];
        SiteTree tree;
        SharedItemStore store = null;
        try {
            DocumentFactory factory = DocumentUtil.createDocumentFactory(this.manager, session);
            Publication publication = factory.getPublication(publicationId);
            Area area = publication.getArea(areaName);
            store = (SharedItemStore) this.manager.lookup(SharedItemStore.ROLE);

            Session storeSession = store.getSession();
            if (session.isModifiable() || session == storeSession) {
                tree = new SiteTreeImpl(this.manager, area, getLogger());
            } else {
                tree = new DelegatingSiteTree(this.manager, area, this, storeSession, key);
            }
        } catch (Exception e) {
            throw new RepositoryException(e);
        } finally {
            if (store != null) {
                this.manager.release(store);
            }
        }
        return tree;
    }

    public String getItemType() {
        return SiteTree.IDENTIFIABLE_TYPE;
    }

}