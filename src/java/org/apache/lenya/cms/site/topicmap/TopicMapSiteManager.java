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
package org.apache.lenya.cms.site.topicmap;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.site.AbstractSiteManager;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteStructure;

/**
 * Site manager to support topic maps.
 */
public class TopicMapSiteManager extends AbstractSiteManager {

    /**
     * @see org.apache.lenya.cms.site.SiteManager#requires(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.cms.publication.Document)
     */
    public boolean requires(Document dependingResource, Document requiredResource)
            throws SiteException {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#getRequiredResources(org.apache.lenya.cms.publication.Document)
     */
    public Document[] getRequiredResources(Document resource) throws SiteException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#getRequiringResources(org.apache.lenya.cms.publication.Document)
     */
    public Document[] getRequiringResources(Document resource) throws SiteException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#add(org.apache.lenya.cms.publication.Document)
     */
    public void add(Document document) throws SiteException {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#contains(org.apache.lenya.cms.publication.Document)
     */
    public boolean contains(Document resource) throws SiteException {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#containsInAnyLanguage(org.apache.lenya.cms.publication.Document)
     */
    public boolean containsInAnyLanguage(Document resource) throws SiteException {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#copy(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.cms.publication.Document)
     */
    public void copy(Document sourceDocument, Document destinationDocument) throws SiteException {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#delete(org.apache.lenya.cms.publication.Document)
     */
    public void delete(Document document) throws SiteException {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#getLabel(org.apache.lenya.cms.publication.Document)
     */
    public String getLabel(Document document) throws SiteException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#setLabel(org.apache.lenya.cms.publication.Document,
     *      java.lang.String)
     */
    public void setLabel(Document document, String label) throws SiteException {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#getDocuments(org.apache.lenya.cms.publication.DocumentIdentityMap,
     *      org.apache.lenya.cms.publication.Publication, java.lang.String)
     */
    public Document[] getDocuments(DocumentIdentityMap map, Publication publication, String area)
            throws SiteException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#getSiteStructure(org.apache.lenya.cms.publication.DocumentIdentityMap, org.apache.lenya.cms.publication.Publication, java.lang.String)
     */
    public SiteStructure getSiteStructure(DocumentIdentityMap map, Publication publiation, String area) throws SiteException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#getAvailableDocument(org.apache.lenya.cms.publication.Document)
     */
    public Document getAvailableDocument(Document document) throws SiteException {
        return document;
    }

}