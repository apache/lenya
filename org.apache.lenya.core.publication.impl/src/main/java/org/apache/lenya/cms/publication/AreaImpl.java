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

import org.apache.cocoon.spring.configurator.WebAppContextUtils;
import org.apache.commons.lang.Validate;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.repository.NodeFactory;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.repository.SessionHolder;
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
    private NodeFactory nodeFactory;
    //florent : private Session session;

    /**
     * @param session The factory.
     * @param pub The publication.
     * @param name The area name.
     */
    /* florent public AreaImpl(Session session, NodeFactory nodeFactory, Publication pub, String name) {
        this.session = session;*/
    public AreaImpl(NodeFactory nodeFactory, Publication pub, String name) {
        this.pub = pub;
        this.name = name;
        this.nodeFactory = nodeFactory;
    }

    public boolean contains(String uuid, String language) {
        Validate.notNull(uuid);
        Validate.notNull(language);
        if (uuid.equals("") || language.equals("")) {
            return false;
        }
        // check site structure first (performance)
        if (getSite().containsByUuid(uuid, language)) {
            return true;
        } else {
        	return false;
        	//florent : comment as is create dependencie to document-impl and seems not useful as the containsbyUuid has to give the result
        	/*
            String sourceUri = DocumentImpl.getSourceURI(pub, name, uuid, language);
            try {
                org.apache.lenya.cms.repository.Session repoSession = ((SessionHolder) this.session)
                        .getRepositorySession();
                Node node = (Node) repoSession.getRepositoryItem(getNodeFactory(), sourceUri);
                return node.exists();
            } catch (RepositoryException e) {
                throw new RuntimeException(e);
            }*/
        }
    }

    protected NodeFactory getNodeFactory() {
        return this.nodeFactory;
    }

    public Document getDocument(String uuid, String language) throws ResourceNotFoundException {
        return getDocumentFactory().get(getPublication(), getName(), uuid, language);
    }

    public Document getDocument(String uuid, String language, int revision)
            throws ResourceNotFoundException {
        return getDocumentFactory().get(getPublication(), getName(), uuid, language, revision);
    }

    protected DocumentFactory getDocumentFactory() {
        SessionImpl sessionImpl = (SessionImpl) this.session;
        DocumentFactory factory = sessionImpl.getDocumentFactory();
        return factory;
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
            try {
                siteManager = (SiteManager) WebAppContextUtils.getCurrentWebApplicationContext()
                        .getBean(SiteManager.ROLE + "/" + getPublication().getSiteManagerHint());
                this.site = siteManager.getSiteStructure(getPublication(), getName());
            } catch (Exception e) {
                throw new RuntimeException(e);
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
