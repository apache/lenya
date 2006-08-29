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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentLocator;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.URLInformation;

/**
 * Utility to handle site structures.
 * 
 * @version $Id$
 */
public class SiteUtil {

    private SiteUtil() {
    }

    /**
     * Returns a sub-site starting with a certain node, which includes the node itself and all nodes
     * which require this node, in preorder.
     * 
     * @param manager The service manager.
     * @param node The top-level document.
     * @return A document set.
     * @throws SiteException if an error occurs.
     */
    public static NodeSet getSubSite(ServiceManager manager, SiteNode node) throws SiteException {
        ServiceSelector selector = null;
        SiteManager siteManager = null;
        SiteNode[] subsite;
        try {
            selector = (ServiceSelector) manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(node.getStructure()
                    .getPublication()
                    .getSiteManagerHint());

            DocumentFactory map = node.getStructure().getPublication().getFactory();
            Set nodes = new HashSet();
            nodes.add(node);

            SiteNode[] requiringNodes = siteManager.getRequiringResources(map, node);
            for (int i = 0; i < requiringNodes.length; i++) {
                nodes.add(requiringNodes[i]);
            }

            subsite = (SiteNode[]) nodes.toArray(new SiteNode[nodes.size()]);
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
        return new NodeSet(manager, subsite);
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
            DocumentLocator target, int mode) throws SiteException {
        Map map = new HashMap();
        ServiceSelector selector = null;
        SiteManager siteManager = null;
        try {
            selector = (ServiceSelector) manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(source.getPublication()
                    .getSiteManagerHint());

            NodeSet subSite = SiteUtil.getSubSite(manager, source.getLink().getNode());
            for (NodeIterator i = subSite.ascending(); i.hasNext(); ) {
                SiteNode node = i.next();
                String[] languages = node.getLanguages();
                for (int l = 0; l < languages.length; l++) {
                    Document doc = node.getLink(languages[l]).getDocument();
                    DocumentLocator targetLoc = SiteUtil.getTransferedDocument(siteManager,
                            doc,
                            source,
                            target,
                            mode);
                    if (targetLoc != null) {
                        map.put(doc, targetLoc);
                    }
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

    public static DocumentLocator getTransferedDocument(SiteManager siteManager, Document source,
            Document baseSource, DocumentLocator baseTarget, int mode) throws SiteException,
            DocumentException, DocumentBuildException {

        String targetArea = baseTarget.getArea();
        String baseSourcePath = siteManager.getSiteStructure(baseSource.getFactory(),
                baseSource.getPublication(),
                baseSource.getArea())
                .getByUuid(baseSource.getUUID(), baseSource.getLanguage())
                .getNode()
                .getPath();

        SiteStructure sourceSite = siteManager.getSiteStructure(source.getFactory(),
                source.getPublication(),
                source.getArea());
        String sourcePath = sourceSite.getByUuid(source.getUUID(), source.getLanguage())
                .getNode()
                .getPath();
        String suffix = sourcePath.substring(baseSourcePath.length());
        String targetPath = baseTarget.getPath() + suffix;

        DocumentLocator target = DocumentLocator.getLocator(baseTarget.getPublicationId(),
                targetArea,
                targetPath,
                source.getLanguage());
        switch (mode) {
        case MODE_REPLACE:
            break;
        case MODE_CANCEL:
            if (sourceSite.contains(target.getPath())) {
                target = null;
            }
            break;
        case MODE_CHANGE_ID:
            target = siteManager.getAvailableLocator(source.getFactory(), target);
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

            NodeSet subsite = SiteUtil.getSubSite(manager, source.getLink().getNode());
            for (NodeIterator i = subsite.ascending(); i.hasNext(); ) {
                SiteNode node = i.next();
                String[] langs = node.getLanguages();
                for (int l = 0; l < langs.length; l++) {
                    Document doc = node.getLink(langs[l]).getDocument();
                    DocumentLocator target = SiteUtil.getTransferedDocument(siteManager,
                            doc,
                            targetArea,
                            mode);
                    if (target != null) {
                        map.put(doc, target);
                    }
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

    public static DocumentLocator getTransferedDocument(SiteManager siteManager, Document source,
            String targetArea, int mode) throws SiteException, DocumentException,
            DocumentBuildException {
        DocumentLocator target = source.getLocator().getAreaVersion(targetArea);
        switch (mode) {
        case MODE_REPLACE:
            break;
        case MODE_CANCEL:
            SiteStructure site = siteManager.getSiteStructure(source.getFactory(),
                    source.getPublication(),
                    target.getArea());
            if (site.contains(target.getPath())) {
                target = null;
            }
            break;
        case MODE_CHANGE_ID:
            target = siteManager.getAvailableLocator(source.getFactory(), target);
            break;
        }
        return target;
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#getAvailableLocator(DocumentFactory, DocumentLocator)
     * @param manager The service manager.
     * @param factory The factory.
     * @param locator The locator.
     * @return A document.
     * @throws SiteException if an error occurs.
     */
    public static DocumentLocator getAvailableLocator(ServiceManager manager,
            DocumentFactory factory, DocumentLocator locator) throws SiteException {
        ServiceSelector selector = null;
        SiteManager siteManager = null;
        try {
            selector = (ServiceSelector) manager.lookup(SiteManager.ROLE + "Selector");
            Publication pub = factory.getPublication(locator.getPublicationId());
            siteManager = (SiteManager) selector.select(pub.getSiteManagerHint());
            return siteManager.getAvailableLocator(factory, locator);
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

    public static boolean contains(ServiceManager manager, DocumentFactory factory,
            DocumentLocator locator) throws SiteException {
        SiteManager siteManager = null;
        ServiceSelector selector = null;

        try {
            selector = (ServiceSelector) manager.lookup(SiteManager.ROLE + "Selector");
            Publication pub = factory.getPublication(locator.getPublicationId());
            String siteManagerHint = pub.getSiteManagerHint();
            siteManager = (SiteManager) selector.select(siteManagerHint);
            SiteStructure site = siteManager.getSiteStructure(factory, pub, locator.getArea());
            return site.contains(locator.getPath());
        } catch (SiteException e) {
            throw e;
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

    public static boolean isDocument(ServiceManager manager, DocumentFactory factory,
            String webappUrl) throws SiteException {

        URLInformation info = new URLInformation(webappUrl);
        DocumentBuilder builder = null;
        ServiceSelector selector = null;
        try {
            Publication pub = factory.getPublication(info.getPublicationId());
            if (pub.exists()) {
                selector = (ServiceSelector) manager.lookup(DocumentBuilder.ROLE + "Selector");
                builder = (DocumentBuilder) selector.select(pub.getDocumentBuilderHint());
                if (builder.isDocument(webappUrl)) {
                    DocumentLocator locator = builder.getLocator(factory, webappUrl);
                    return contains(manager, factory, locator);
                }
            }
            return false;
        } catch (SiteException e) {
            throw e;
        } catch (Exception e) {
            throw new SiteException(e);
        } finally {
            if (selector != null) {
                if (builder != null) {
                    selector.release(builder);
                }
                manager.release(selector);
            }
        }
    }

    public static Document getDocument(ServiceManager manager, DocumentFactory factory,
            String webappUrl) throws SiteException {

        DocumentLocator locator = getLocator(manager, factory, webappUrl);
        if (contains(manager, factory, locator)) {
            try {
                return factory.get(locator);
            } catch (DocumentBuildException e) {
                throw new SiteException(e);
            }
        } else {
            throw new SiteException("No document for webapp URL [" + webappUrl + "]");
        }
    }

    public static DocumentLocator getLocator(ServiceManager manager, DocumentFactory factory,
            String webappUrl) throws SiteException {
        URLInformation info = new URLInformation(webappUrl);
        DocumentBuilder builder = null;
        ServiceSelector selector = null;
        try {
            Publication pub = factory.getPublication(info.getPublicationId());
            selector = (ServiceSelector) manager.lookup(DocumentBuilder.ROLE + "Selector");
            builder = (DocumentBuilder) selector.select(pub.getDocumentBuilderHint());
            return builder.getLocator(factory, webappUrl);

        } catch (SiteException e) {
            throw e;
        } catch (Exception e) {
            throw new SiteException(e);
        } finally {
            if (selector != null) {
                if (builder != null) {
                    selector.release(builder);
                }
                manager.release(selector);
            }
        }
    }

    public static boolean contains(ServiceManager manager, Document document) throws SiteException {
        SiteManager siteManager = null;
        ServiceSelector selector = null;

        try {
            selector = (ServiceSelector) manager.lookup(SiteManager.ROLE + "Selector");
            String siteManagerHint = document.getPublication().getSiteManagerHint();
            siteManager = (SiteManager) selector.select(siteManagerHint);
            return siteManager.contains(document);
        } catch (SiteException e) {
            throw e;
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

    public static boolean contains(ServiceManager manager, DocumentFactory factory,
            Publication pub, String area, String path) throws SiteException {
        SiteManager siteManager = null;
        ServiceSelector selector = null;

        try {
            selector = (ServiceSelector) manager.lookup(SiteManager.ROLE + "Selector");
            String siteManagerHint = pub.getSiteManagerHint();
            siteManager = (SiteManager) selector.select(siteManagerHint);
            return siteManager.getSiteStructure(factory, pub, area).contains(path);
        } catch (ServiceException e) {
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