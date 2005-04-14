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
package org.apache.lenya.cms.publication;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.cocoon.source.RepositorySource;
import org.apache.lenya.cms.metadata.MetaDataManager;
import org.apache.lenya.cms.metadata.MetaDataOwner;
import org.apache.lenya.cms.metadata.dublincore.DublinCoreProxy;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.transaction.Transactionable;

/**
 * A resource (asset).
 * 
 * @version $Id:$
 */
public class Resource implements MetaDataOwner {

    private Document document;
    private String name;
    private ServiceManager manager;

    /**
     * Ctor.
     * @param document The document the resource belongs to.
     * @param name The name.
     * @param manager The service manager.
     */
    public Resource(Document document, String name, ServiceManager manager) {
        this.document = document;
        this.name = name;
        this.manager = manager;
    }

    /**
     * @return The document.
     */
    public Document getDocument() {
        return document;
    }

    /**
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * @see org.apache.lenya.cms.metadata.MetaDataOwner#getMetaData()
     */
    public MetaDataManager getMetaData() {
        return new DublinCoreProxy(getMetaSourceURI(), this.manager);
    }

    /**
     * @return The source URI.
     */
    public String getSourceURI() {
        String srcUri = getResourcesURI() + "/" + getName();
        return srcUri;
    }

    /**
     * @return The meta source URI.
     */
    public String getMetaSourceURI() {
        return getSourceURI() + ResourcesManager.RESOURCES_META_SUFFIX;
    }

    /**
     * @return The directory URI where the resources are located.
     */
    public String getBaseURI() {
        String resourcesUri = getResourcesURI().substring("lenya://".length());
        return "context://" + resourcesUri;
    }

    protected String getResourcesURI() {
        String pubId = document.getPublication().getId();
        String resourcesUri = "lenya://lenya/pubs/" + pubId + "/"
                + ResourcesManager.RESOURCES_PREFIX + "/" + document.getArea() + document.getId();
        return resourcesUri;
    }

    /**
     * @return The repository nodes that represent this resource.
     */
    public Transactionable[] getRepositoryNodes() {
        Node[] nodes = new Node[2];
        SourceResolver resolver = null;
        RepositorySource documentSource = null;
        RepositorySource metaSource = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            documentSource = (RepositorySource) resolver.resolveURI(getSourceURI());
            metaSource = (RepositorySource) resolver.resolveURI(getMetaSourceURI());
            nodes[0] = documentSource.getNode();
            nodes[1] = metaSource.getNode();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (resolver != null) {
                if (documentSource != null) {
                    resolver.release(documentSource);
                }
                if (metaSource != null) {
                    resolver.release(metaSource);
                }
                this.manager.release(resolver);
            }
        }
        return nodes;
    }

}