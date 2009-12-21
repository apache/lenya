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
package org.apache.lenya.cms.lucene;

import java.util.Arrays;

import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.components.search.IndexException;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.observation.DocumentEvent;
import org.apache.lenya.cms.observation.ObservationRegistry;
import org.apache.lenya.cms.observation.RepositoryEvent;
import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.ResourceType;
import org.apache.lenya.cms.repository.Session;

/**
 * Index updater implementation.
 */
public class IndexUpdaterImpl extends AbstractLogEnabled implements IndexUpdater, Startable,
        Serviceable, ThreadSafe {

    public void eventFired(RepositoryEvent repoEvent) {

        if (!(repoEvent instanceof DocumentEvent)) {
            return;
        }
        DocumentEvent event = (DocumentEvent) repoEvent;

        try {
            if (event.getDescriptor().equals(DocumentEvent.CHANGED)) {
                index(event.getSession(), event.getResourceType(), event.getPublicationId(), event
                        .getArea(), event.getUuid(), event.getLanguage());
            } else if (event.getDescriptor().equals(DocumentEvent.REMOVED)) {
                delete(event.getSession(), event.getResourceType(), event.getPublicationId(), event
                        .getArea(), event.getUuid(), event.getLanguage());
            }

        } catch (IndexException e) {
            throw new RuntimeException(e);
        }
    }

    protected void updateIndex(String operation, ResourceType resourceType, String pubId,
            String area, String uuid, String language) throws IndexException {

        String uri = null;
        try {
            String[] formats = resourceType.getFormats();
            if (Arrays.asList(formats).contains("luceneIndex")) {
                String docString = pubId + "/" + area + "/" + uuid + "/" + language;
                // + event.getDocumentUrl();
                uri = "cocoon://modules/lucene/" + operation + "-document/" + docString;
                SourceUtil.readDOM(uri, this.manager);
            } else {
                getLogger().info(
                        "Document [" + pubId + ":" + area + ":" + uuid + ":" + language
                                + "] is not being indexed because resource type ["
                                + resourceType.getName() + "] does not support indexing!");
            }
        } catch (Exception e) {
            getLogger().error("Invoking indexing failed for URL [" + uri + "]: ", e);
            throw new IndexException(e);
        }
    }

    public void start() throws Exception {
        ObservationRegistry registry = null;
        try {
            registry = (ObservationRegistry) this.manager.lookup(ObservationRegistry.ROLE);
            registry.registerListener(this);
        } finally {
            if (registry != null) {
                this.manager.release(registry);
            }
        }
    }

    public void stop() throws Exception {
    }

    private ServiceManager manager;

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    public void delete(Session session, ResourceType resourceType, String pubId, String area,
            String uuid, String language) throws IndexException {
        updateIndex("delete", resourceType, pubId, area, uuid, language);
    }

    public void index(Session session, ResourceType resourceType, String pubId, String area,
            String uuid, String language) throws IndexException {
        DocumentFactory factory = DocumentUtil.createDocumentFactory(this.manager, session);
        try {
            Publication pub = factory.getPublication(pubId);
            Area areaObj = pub.getArea(area);
            if (areaObj.contains(uuid, language)) {
                updateIndex("index", resourceType, pubId, area, uuid, language);
            } else {
                getLogger().debug(
                        "Ignoring document [" + pubId + ":" + area + ":" + uuid + ":" + language
                                + "] because it doesn't exist (anymore).");
            }
        } catch (Exception e) {
            throw new IndexException(e);
        }
    }

}
