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

import org.apache.cocoon.processing.ProcessInfoProvider;
import org.apache.cocoon.spring.configurator.WebAppContextUtils;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.observation.AbstractRepositoryListener;
import org.apache.lenya.cms.observation.RepositoryEventDescriptor;
import org.apache.lenya.cms.observation.DocumentEventSource;
import org.apache.lenya.cms.observation.RepositoryEvent;
import org.apache.lenya.cms.publication.DocumentIdentifier;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.Repository;
//import org.apache.lenya.cms.publication.Session;
import org.apache.lenya.cms.repository.Session;

/**
 * The content janitor cleans up empty directories after a document is removed.
 */
public class ContentJanitor extends AbstractRepositoryListener {
    
    private Repository repository;
    private SourceResolver sourceResolver;

    public void eventFired(RepositoryEvent repoEvent) {
        
        if (!(repoEvent.getDescriptor() instanceof RepositoryEventDescriptor)) {
            return;
        }
        DocumentEventSource source = (DocumentEventSource) repoEvent.getSource();
        DocumentIdentifier id = source.getIdentifier();
        
        if (repoEvent.getDescriptor() != RepositoryEventDescriptor.REMOVED) {
            return;
        }
        
        try {
            ProcessInfoProvider process = (ProcessInfoProvider) WebAppContextUtils
                    .getCurrentWebApplicationContext().getBean(ProcessInfoProvider.ROLE);
            Session session = this.repository.getSession(process.getRequest());
            Publication pub = session.getPublication(id.getPublicationId());
            String contentUri = pub.getContentUri(id.getArea());
            SourceUtil.deleteEmptyCollections(contentUri, this.sourceResolver);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public void setSourceResolver(SourceResolver sourceResolver) {
        this.sourceResolver = sourceResolver;
    }

}
