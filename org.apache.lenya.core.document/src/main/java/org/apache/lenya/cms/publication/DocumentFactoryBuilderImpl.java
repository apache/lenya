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

import org.apache.cocoon.util.AbstractLogEnabled;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.metadata.MetaDataCache;
import org.apache.lenya.cms.repository.NodeFactory;

/**
 * Document factory builder implementation.
 */
public class DocumentFactoryBuilderImpl extends AbstractLogEnabled implements
        DocumentFactoryBuilder {

    private PublicationManager pubManager;
    private MetaDataCache metaDataCache;
    private SourceResolver sourceResolver;
    private NodeFactory nodeFactory;
    private ResourceTypeResolver resourceTypeResolver;

    public DocumentFactory createDocumentFactory(Session session) {
        DocumentFactoryImpl factory = new DocumentFactoryImpl(session);
        factory.setMetaDataCache(getMetaDataCache());
        factory.setPublicationManager(getPublicationManager());
        factory.setSourceResolver(getSourceResolver());
        factory.setNodeFactory(this.nodeFactory);
        factory.setResourceTypeResolver(this.resourceTypeResolver);
        return factory;
    }

    public void setPublicationManager(PublicationManager pubManager) {
        this.pubManager = pubManager;
    }

    protected PublicationManager getPublicationManager() {
        return this.pubManager;
    }

    public void setMetaDataCache(MetaDataCache metaDataCache) {
        this.metaDataCache = metaDataCache;
    }

    protected MetaDataCache getMetaDataCache() {
        return metaDataCache;
    }

    public SourceResolver getSourceResolver() {
        return sourceResolver;
    }

    public void setSourceResolver(SourceResolver sourceResolver) {
        this.sourceResolver = sourceResolver;
    }

    public void setNodeFactory(NodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    public void setResourceTypeResolver(ResourceTypeResolver resourceTypeResolver) {
        this.resourceTypeResolver = resourceTypeResolver;
    }

}
