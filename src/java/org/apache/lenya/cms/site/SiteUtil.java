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
package org.apache.lenya.cms.site;

import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.util.DocumentSet;

/**
 * Utility to handle site structures.
 * 
 * @version $Id$
 */
public class SiteUtil {

    private SiteUtil() {
    }

    /**
     * Returns a site structure object.
     * 
     * @param map The identity map.
     * @param publication The publication.
     * @param area The area.
     * @param manager The service manager.
     * @return A site structure.
     * @throws SiteException if an error occurs.
     */
    public static SiteStructure getSiteStructure(ServiceManager manager, DocumentIdentityMap map,
            Publication publication, String area) throws SiteException {

        SiteStructure structure = null;
        ServiceSelector selector = null;
        SiteManager siteManager = null;
        try {
            selector = (ServiceSelector) manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(publication.getSiteManagerHint());
            structure = siteManager.getSiteStructure(map, publication, area);
        } catch (Exception e) {
            throw new SiteException(e);
        } finally {
            if (selector != null) {
                if (siteManager != null) {
                    selector.release(siteManager);
                }
                manager.release(selector);
            }
        }
        return structure;
    }

    /**
     * Returns the site structure a document belongs to.
     * 
     * @param manager The service manager.
     * @param document The document.
     * @return A site structure.
     * @throws SiteException if an error occurs.
     */
    public static SiteStructure getSiteStructure(ServiceManager manager, Document document)
            throws SiteException {
        return SiteUtil.getSiteStructure(manager, document.getIdentityMap(), document
                .getPublication(), document.getArea());
    }

    /**
     * Returns a sub-site starting with a certain document, which includes the document itself and
     * all documents which require this document, including all language versions.
     * 
     * @param manager The service manager.
     * @param document The top-level document.
     * @return A document set.
     * @throws SiteException if an error occurs.
     */
    public static DocumentSet getSubSite(ServiceManager manager, Document document)
            throws SiteException {
        DocumentSet set = null;
        ServiceSelector selector = null;
        SiteManager siteManager = null;
        try {
            selector = (ServiceSelector) manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(document.getPublication()
                    .getSiteManagerHint());

            DocumentIdentityMap map = document.getIdentityMap();
            Node node = NodeFactory.getNode(document);
            set = getExistingDocuments(map, node);

            Node[] requiringNodes = siteManager.getRequiringResources(map, node);
            for (int i = 0; i < requiringNodes.length; i++) {
                set.addAll(getExistingDocuments(map, requiringNodes[i]));
            }

        } catch (Exception e) {
            throw new SiteException(e);
        } finally {
            if (selector != null) {
                if (siteManager != null) {
                    selector.release(siteManager);
                }
                manager.release(selector);
            }
        }
        return set;
    }

    /**
     * @param map An identity map.
     * @param node A node.
     * @return All existing documents belonging to the node.
     * @throws DocumentBuildException if an error occurs.
     * @throws DocumentException if an error occurs.
     */
    public static DocumentSet getExistingDocuments(DocumentIdentityMap map, Node node)
            throws DocumentBuildException, DocumentException {
        DocumentSet set = new DocumentSet();
        Document document = map.get(node.getPublication(), node.getArea(), node.getDocumentId());
        String[] languages = document.getLanguages();
        for (int i = 0; i < languages.length; i++) {
            Document version = document.getIdentityMap().getLanguageVersion(document, languages[i]);
            set.add(version);
        }
        return set;
    }

    /**
     * Sorts a document set in ascending order.
     * 
     * @param manager The service manager.
     * @param set The set.
     * @throws SiteException if an error occurs.
     */
    public static void sortAscending(ServiceManager manager, DocumentSet set) throws SiteException {

        if (set.isEmpty()) {
            return;
        }

        ServiceSelector selector = null;
        SiteManager siteManager = null;
        try {
            selector = (ServiceSelector) manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(set.getDocuments()[0].getPublication()
                    .getSiteManagerHint());
            siteManager.sortAscending(set);
        } catch (Exception e) {
            throw new SiteException(e);
        } finally {
            if (selector != null) {
                if (siteManager != null) {
                    selector.release(siteManager);
                }
                manager.release(selector);
            }
        }
    }

    /**
     * Sorts a document set in descending order.
     * 
     * @param manager The service manager.
     * @param set The set.
     * @throws SiteException if an error occurs.
     */
    public static void sortDescending(ServiceManager manager, DocumentSet set) throws SiteException {
        SiteUtil.sortAscending(manager, set);
        set.reverse();
    }

    /**
     * Replace the target documents.
     */
    public static final int MODE_REPLACE = 0;

    /**
     * Cancel the command if one of the target document(s) exists.
     */
    public static final int MODE_CANCEL = 1;

    /**
     * Change the ID of a target document if it already exists.
     */
    public static final int MODE_CHANGE_ID = 2;

    /**
     * Returns a document set that represents the transfer of a sub-site to another location.
     * 
     * @param manager The service manager.
     * @param source The source document.
     * @param target The target document.
     * @param mode The mode: {@link #MODE_REPLACE},{@link #MODE_CANCEL},{@link #MODE_CHANGE_ID}.
     * @return A map which maps source to target documents.
     * @throws SiteException if an error occurs.
     */
    public static Map getTransferedSubSite(ServiceManager manager, Document source,
            Document target, int mode) throws SiteException {
        Map map = new HashMap();
        ServiceSelector selector = null;
        SiteManager siteManager = null;
        try {
            selector = (ServiceSelector) manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(source.getPublication()
                    .getSiteManagerHint());

            DocumentSet subSite = SiteUtil.getSubSite(manager, source);
            Document[] docs = subSite.getDocuments();
            for (int i = 0; i < docs.length; i++) {
                Document targetDoc = SiteUtil.getTransferedDocument(siteManager, docs[i], source,
                        target, mode);
                if (targetDoc != null) {
                    map.put(docs[i], targetDoc);
                }
            }

        } catch (Exception e) {
            throw new SiteException(e);
        } finally {
            if (selector != null) {
                if (siteManager != null) {
                    selector.release(siteManager);
                }
                manager.release(selector);
            }
        }
        return map;
    }

    public static Document getTransferedDocument(SiteManager siteManager, Document source,
            Document baseSource, Document baseTarget, int mode) throws SiteException,
            DocumentException, DocumentBuildException {

        String targetArea = baseTarget.getArea();
        String sourceId = baseSource.getId();

        String suffix = source.getId().substring(sourceId.length());
        String targetId = baseTarget.getId() + suffix;

        Document target = source.getIdentityMap().get(baseTarget.getPublication(), targetArea,
                targetId, source.getLanguage());
        switch (mode) {
        case MODE_REPLACE:
            break;
        case MODE_CANCEL:
            if (target.exists()) {
                target = null;
            }
            break;
        case MODE_CHANGE_ID:
            target = siteManager.getAvailableDocument(target);
            break;
        }
        return target;
    }

    /**
     * Returns a document set that represents the transfer of a sub-site to another area.
     * 
     * @param manager The service manager.
     * @param source The source document.
     * @param targetArea The target area.
     * @param mode The mode: {@link #MODE_REPLACE},{@link #MODE_CANCEL},{@link #MODE_CHANGE_ID}.
     * @return A map which maps sources to targets.
     * @throws SiteException if an error occurs.
     */
    public static Map getTransferedSubSite(ServiceManager manager, Document source,
            String targetArea, int mode) throws SiteException {

        Map map = new HashMap();
        ServiceSelector selector = null;
        SiteManager siteManager = null;
        try {
            selector = (ServiceSelector) manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(source.getPublication()
                    .getSiteManagerHint());

            DocumentSet subSite = SiteUtil.getSubSite(manager, source);
            Document[] docs = subSite.getDocuments();
            for (int i = 0; i < docs.length; i++) {
                Document target = SiteUtil.getTransferedDocument(siteManager, docs[i], targetArea,
                        mode);
                if (target != null) {
                    map.put(docs[i], target);
                }
            }

        } catch (Exception e) {
            throw new SiteException(e);
        } finally {
            if (selector != null) {
                if (siteManager != null) {
                    selector.release(siteManager);
                }
                manager.release(selector);
            }
        }
        return map;
    }

    public static Document getTransferedDocument(SiteManager siteManager, Document source,
            String targetArea, int mode) throws SiteException, DocumentException,
            DocumentBuildException {
        Document target = source.getIdentityMap().getAreaVersion(source, targetArea);
        switch (mode) {
        case MODE_REPLACE:
            break;
        case MODE_CANCEL:
            if (target.exists()) {
                target = null;
            }
            break;
        case MODE_CHANGE_ID:
            target = siteManager.getAvailableDocument(target);
            break;
        }
        return target;
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#getAvailableDocument(Document)
     * @param manager The service manager.
     * @param document The document.
     * @return A document.
     * @throws SiteException if an error occurs.
     */
    public static Document getAvailableDocument(ServiceManager manager, Document document)
            throws SiteException {
        ServiceSelector selector = null;
        SiteManager siteManager = null;
        try {
            selector = (ServiceSelector) manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(document.getPublication()
                    .getSiteManagerHint());

            return siteManager.getAvailableDocument(document);
        } catch (Exception e) {
            throw new SiteException(e);
        } finally {
            if (selector != null) {
                if (siteManager != null) {
                    selector.release(siteManager);
                }
                manager.release(selector);
            }
        }
    }

}