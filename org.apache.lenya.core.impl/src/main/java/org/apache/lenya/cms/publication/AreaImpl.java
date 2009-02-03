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

import java.util.ArrayList;
import java.util.List;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.repository.NodeFactory;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.util.Assert;

/**
 * Area implementation.
 */
public class AreaImpl implements Area {

    private String name;
    private Publication pub;
    private DocumentFactory factory;
    private NodeFactory nodeFactory;
    private ServiceManager manager;

    /**
     * @param manager The service manager.
     * @param factory The factory.
     * @param pub The publication.
     * @param name The area name.
     */
    public AreaImpl(ServiceManager manager, DocumentFactory factory, Publication pub, String name) {
        this.manager = manager;
        this.factory = factory;
        this.pub = pub;
        this.name = name;
    }

    public boolean contains(String uuid, String language) {
        Assert.notNull("uuid", uuid);
        Assert.notNull("language", language);
        if (uuid.equals("") || language.equals("")) {
            return false;
        }
        // check site structure first (performance)
        if (getSite().containsByUuid(uuid, language)) {
            return true;
        } else {
            String sourceUri = DocumentImpl.getSourceURI(pub, name, uuid, language);
            try {
                Node node = (Node) getPublication().getSession().getRepositoryItem(
                        getNodeFactory(), sourceUri);
                return node.exists();
            } catch (RepositoryException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected NodeFactory getNodeFactory() {
        if (this.nodeFactory == null) {
            try {
                this.nodeFactory = (NodeFactory) this.manager.lookup(NodeFactory.ROLE);
            } catch (ServiceException e) {
                throw new RuntimeException(e);
            }
        }
        return this.nodeFactory;
    }

    public Document getDocument(String uuid, String language) throws PublicationException {
        return this.factory.get(getPublication(), getName(), uuid, language);
    }

    public String getName() {
        return this.name;
    }

    public Publication getPublication() {
        return this.pub;
    }

    private SiteStructure site;

    public SiteStructure getSite() {
        if (this.site == null) {
            SiteManager siteManager = null;
            ServiceSelector selector = null;
            try {
                selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
                siteManager = (SiteManager) selector.select(getPublication().getSiteManagerHint());
                this.site = siteManager.getSiteStructure(this.factory, getPublication(), getName());
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                if (selector != null) {
                    if (siteManager != null) {
                        selector.release(siteManager);
                    }
                    this.manager.release(selector);
                }
            }
        }
        return this.site;
    }

    public String toString() {
        return getPublication().getId() + ":" + getName();
    }

    public Document[] getDocuments() {
        SiteNode[] nodes = getSite().getNodes();
        List docs = new ArrayList();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i].getUuid() != null) {
                String[] langs = nodes[i].getLanguages();
                for (int l = 0; l < langs.length; l++) {
                    try {
                        docs.add(nodes[i].getLink(langs[l]).getDocument());
                    } catch (SiteException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return (Document[]) docs.toArray(new Document[docs.size()]);
    }

}
