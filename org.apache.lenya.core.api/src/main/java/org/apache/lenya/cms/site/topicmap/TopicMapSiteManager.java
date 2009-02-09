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
package org.apache.lenya.cms.site.topicmap;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentLocator;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.Session;
import org.apache.lenya.cms.site.AbstractSiteManager;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteStructure;

/**
 * Site manager to support topic maps.
 */
public class TopicMapSiteManager extends AbstractSiteManager {

    public boolean requires(SiteNode dependingResource, SiteNode requiredResource)
            throws SiteException {
        return false;
    }

    public SiteNode[] getRequiringResources(SiteNode resource) throws SiteException {
        return null;
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#add(org.apache.lenya.cms.publication.Document)
     */
    public void add(String path, Document document) throws SiteException {
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#contains(org.apache.lenya.cms.publication.Document)
     */
    public boolean contains(Document resource) throws SiteException {
        return false;
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#containsInAnyLanguage(org.apache.lenya.cms.publication.Document)
     */
    public boolean containsInAnyLanguage(Document resource) throws SiteException {
        return false;
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#copy(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.cms.publication.Document)
     */
    public void copy(Document sourceDocument, Document destinationDocument) throws SiteException {
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#setVisibleInNav(org.apache.lenya.cms.publication.Document,
     *      boolean)
     */
    public void setVisibleInNav(Document document, boolean visibleInNav) throws SiteException {
    }

    public Document[] getDocuments(Publication publication, String area) throws SiteException {
        // TODO Auto-generated method stub
        return null;
    }

    public SiteStructure getSiteStructure(Publication publiation, String area) throws SiteException {
        // TODO Auto-generated method stub
        return null;
    }

    public DocumentLocator getAvailableLocator(Session session, DocumentLocator document)
            throws SiteException {
        return document;
    }

    public boolean isVisibleInNav(Document document) throws SiteException {
        return true;
    }

    public void set(String path, Document document) throws SiteException {
        // TODO Auto-generated method stub
    }

    public DocumentLocator[] getRequiredResources(Session session, DocumentLocator locator)
            throws SiteException {
        return new DocumentLocator[0];
    }

    public void move(SiteNode source, String destinationPath) throws SiteException {
        throw new SiteException("This operation is not supported by [" + getClass().getName()
                + "]!");
    }

    public void setLabel(Document document, String label) throws SiteException {
        // TODO Auto-generated method stub
    }

}
