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
package org.apache.lenya.cms.publication;

import java.util.ArrayList;
import java.util.List;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.SiteStructure;

/**
 * Area implementation.
 */
public class AreaImpl implements Area {

    private String name;
    private Publication pub;
    private DocumentFactory factory;
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
        String sourceUri = DocumentImpl.getSourceURI(pub, name, uuid, language);
        Node node = DocumentImpl.getRepositoryNode(this.manager, this.factory, sourceUri);
        try {
            return node.exists();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    public Document getDocument(String uuid, String language) throws PublicationException {
        if (!contains(uuid, language)) {
            throw new PublicationException("The area [" + this + "] doesn't contain the document ["
                    + uuid + ":" + language + "].");
        }
        return this.factory.get(getPublication(), getName(), uuid, language);
    }

    public String getName() {
        return this.name;
    }

    public Publication getPublication() {
        return this.pub;
    }

    public SiteStructure getSite() {
        SiteManager siteManager = null;
        ServiceSelector selector = null;
        try {
            selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(getPublication().getSiteManagerHint());
            return siteManager.getSiteStructure(this.factory, getPublication(), getName());
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
    
    public String toString() {
        return getPublication().getId() + ":" + getName();
    }

    public Document[] getDocuments() {
        SiteNode[] nodes = getSite().getNodes();
        List docs = new ArrayList();
        for (int i = 0; i < nodes.length; i++) {
            String[] langs = nodes[i].getLanguages();
            for (int l = 0; l < langs.length; l++) {
                try {
                    docs.add(nodes[i].getLink(langs[l]).getDocument());
                } catch (SiteException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return (Document[]) docs.toArray(new Document[docs.size()]);
    }

}
