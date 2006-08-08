/*
 * Copyright  1999-2005 The Apache Software Foundation
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
package org.apache.lenya.cms.lucene;

import java.util.Arrays;

import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.observation.ObservationRegistry;
import org.apache.lenya.cms.observation.RepositoryEvent;
import org.apache.lenya.cms.observation.RepositoryListener;
import org.apache.lenya.cms.publication.Document;

/**
 * Index updater which updates the index when a document changes.
 */
public class IndexUpdater extends AbstractLogEnabled implements RepositoryListener, Startable,
        Serviceable, ThreadSafe {

    public void documentChanged(RepositoryEvent event) {
        updateIndex("index", event);
    }

    public void documentRemoved(RepositoryEvent event) {
        updateIndex("delete", event);
    }

    protected void updateIndex(String operation, RepositoryEvent event) {

        String uri = null;
        try {
            String[] formats = event.getResourceType().getFormats();
            if (Arrays.asList(formats).contains("luceneIndex")) {
                String docString = event.getPublicationId() + "/" + event.getArea() + "/"
                        + event.getUuid() + "/" + event.getLanguage() + event.getDocumentUrl();
                uri = "cocoon://modules/lucene/" + operation + "-document/" + docString;
                SourceUtil.readDOM(uri, this.manager);
            } else {
                getLogger().info(
                        "Document [" + event.getDocumentUrl()
                                + "] is not being indexed because resource type ["
                                + event.getResourceType().getName()
                                + "] does not support indexing!");
            }
        } catch (Exception e) {
            getLogger().error("Invoking indexing failed for URL [" + uri + "]: ", e);
            throw new RuntimeException(e);
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

}
