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
package org.apache.lenya.cms.janitor;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.apache.cocoon.processing.ProcessInfoProvider;
import org.apache.cocoon.spring.configurator.WebAppContextUtils;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.observation.AbstractRepositoryListener;
import org.apache.lenya.cms.observation.DocumentEvent;
import org.apache.lenya.cms.observation.RepositoryEvent;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.repository.RepositoryManager;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;

/**
 * The content janitor cleans up empty directories after a document is removed.
 */
public class ContentJanitor extends AbstractRepositoryListener {
    
    private RepositoryManager repositoryManager;

    public void eventFired(RepositoryEvent repoEvent) {
        
        if (!(repoEvent instanceof DocumentEvent)) {
            return;
        }
        DocumentEvent event = (DocumentEvent) repoEvent;
        
        if (!event.getDescriptor().equals(DocumentEvent.REMOVED)) {
            return;
        }
        
        try {
            ProcessInfoProvider process = (ProcessInfoProvider) WebAppContextUtils
                    .getCurrentWebApplicationContext().getBean(ProcessInfoProvider.ROLE);
            HttpServletRequest request = process.getRequest();
            Session session = RepositoryUtil.getSession(getRepositoryManager(), request);
            DocumentFactory factory = DocumentUtil.createDocumentFactory(session);
            Publication pub = factory.getPublication(event.getPublicationId());
            File contentFile = pub.getContentDirectory(event.getArea());
            String contentUri = contentFile.toURI().toString();
            SourceUtil.deleteEmptyCollections(contentUri, this.manager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setRepositoryManager(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    public RepositoryManager getRepositoryManager() {
        return repositoryManager;
    }

}
