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
package org.apache.lenya.cms.site;

import java.util.HashSet;
import java.util.Set;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentBuilder;
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
            String path = locator.getPath();
            String language = locator.getLanguage();
            return site.contains(path) && site.getNode(path).hasLink(language);
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