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
package org.apache.lenya.cms.publication;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.repository.RepositoryItem;
import org.apache.lenya.cms.repository.RepositoryItemFactory;
import org.apache.lenya.cms.repository.Session;

/**
 * Publication factory.
 */
public class PublicationFactory implements RepositoryItemFactory {

    private ServiceManager manager;
    private PublicationConfiguration config;

    /**
     * @param manager The service manager.
     * @param config The publication configuration.
     */
    public PublicationFactory(ServiceManager manager, PublicationConfiguration config) {
        this.manager = manager;
        this.config = config;
    }

    public RepositoryItem buildItem(Session session, String key) throws RepositoryException {
        DocumentFactory factory = DocumentUtil.createDocumentFactory(this.manager, session);
        return new PublicationImpl(this.manager, factory, config);
    }

    public String getItemType() {
        return Publication.ITEM_TYPE;
    }

}
