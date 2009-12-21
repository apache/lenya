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
package org.apache.lenya.cms.export;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.linking.LinkConverter;
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.ResourceType;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.cms.site.tree.DefaultSiteTree;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Element;

/**
 * Import content.
 */
public class Importer extends AbstractLogEnabled {

    private ServiceManager manager;

    /**
     * Ctor.
     * @param manager The service manager.
     * @param logger The logger.
     */
    public Importer(ServiceManager manager, Logger logger) {
        this.manager = manager;
        enableLogging(logger);
    }

    /**
     * Imports content into an area.
     * @param area The area.
     * @param path The path containing the content.
     * @throws Exception if an error occurs.
     */
    public void importContent(Area area, String path) throws Exception {
        importContent(area.getPublication(), area, path);
    }

    /**
     * Imports content from a different publication into an area.
     * @param srcPub The source publication.
     * @param area The area.
     * @param path The path containing the content.
     * @throws Exception if an error occurs.
     */
    public void importContent(Publication srcPub, Area area, String path) throws Exception {
        getLogger().info("Importing documents into area [" + area + "]");

        String baseUri = "file://" + path;
        String sitetreeUri = baseUri + "/sitetree.xml";

        org.w3c.dom.Document xml = SourceUtil.readDOM(sitetreeUri, this.manager);
        NamespaceHelper helper = new NamespaceHelper(DefaultSiteTree.NAMESPACE_URI, "", xml);

        Element siteElement = xml.getDocumentElement();
        importChildren(area, helper, siteElement, baseUri, "");

        convertLinks(srcPub, area);
    }

    protected void importElement(Area area, NamespaceHelper helper, Element element,
            String baseUri, String parentPath) {
        String path = parentPath + "/" + element.getAttribute("id");

        boolean visible = true;
        String visibleString = element.getAttribute("visibleinnav");
        if (visibleString != null && !visibleString.equals("")) {
            visible = Boolean.valueOf(visibleString).booleanValue();
        }

        Element[] labelElements = helper.getChildren(element, "label");
        for (int i = 0; i < labelElements.length; i++) {
            importDocument(area, labelElements[i], baseUri, path, visible);
        }
        importChildren(area, helper, element, baseUri, path);
    }

    protected void importDocument(Area area, Element element, String baseUri, String path,
            boolean visibleInNav) {
        String language = element.getAttribute("xml:lang");
        String navigationTitle = DocumentHelper.getSimpleElementText(element);

        String contentUri = baseUri + path + "/index_" + language;
        String metaUri = contentUri + ".meta";

        DocumentManager docManager = null;
        ServiceSelector selector = null;
        ResourceType resourceType = null;
        SourceResolver resolver = null;
        try {

            org.w3c.dom.Document xml = SourceUtil.readDOM(metaUri, this.manager);
            NamespaceHelper helper = new NamespaceHelper(
                    "http://apache.org/cocoon/lenya/page-envelope/1.0", "", xml);
            Element metaElement = helper.getFirstChild(xml.getDocumentElement(), "meta");
            Element internalElement = helper.getFirstChild(metaElement, "internal");
            Element resourceTypeElement = helper.getFirstChild(internalElement, "resourceType");
            String resourceTypeName = DocumentHelper.getSimpleElementText(resourceTypeElement);

            Element mimeTypeElement = helper.getFirstChild(internalElement, "mimeType");
            String mimeType = DocumentHelper.getSimpleElementText(mimeTypeElement);

            selector = (ServiceSelector) this.manager.lookup(ResourceType.ROLE + "Selector");
            resourceType = (ResourceType) selector.select(resourceTypeName);

            docManager = (DocumentManager) this.manager.lookup(DocumentManager.ROLE);
            Document newDoc;
            SiteStructure site = area.getSite();
            if (!site.contains(path) || site.getNode(path).getLanguages().length == 0) {
                newDoc = docManager.add(area.getPublication().getFactory(), resourceType,
                        contentUri, area.getPublication(), area.getName(), path, language, "xml",
                        navigationTitle, visibleInNav);
                newDoc.setMimeType(mimeType);
            } else {
                SiteNode node = site.getNode(path);
                Document doc = node.getLink(node.getLanguages()[0]).getDocument();
                newDoc = docManager.addVersion(doc, area.getName(), language, true);
                resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
                SourceUtil.copy(resolver, contentUri, newDoc.getOutputStream());
                newDoc.getLink().setLabel(navigationTitle);
            }

            String dcNamespace = "http://purl.org/dc/elements/1.1/";

            Element dcElement = helper.getFirstChild(metaElement, "dc");
            NamespaceHelper dcHelper = new NamespaceHelper(dcNamespace, "dc", xml);
            Element[] dcElements = dcHelper.getChildren(dcElement);

            MetaData meta = newDoc.getMetaData(dcNamespace);
            for (int i = 0; i < dcElements.length; i++) {
                String key = dcElements[i].getLocalName();
                String value = DocumentHelper.getSimpleElementText(dcElements[i]);
                meta.setValue(key, value);
            }
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (docManager != null) {
                this.manager.release(docManager);
            }
            if (selector != null) {
                this.manager.release(selector);
            }
            if (resolver != null) {
                this.manager.release(resolver);
            }
        }
    }

    protected void importChildren(Area area, NamespaceHelper helper, Element element,
            String baseUri, String path) {
        Element[] elements = helper.getChildren(element, "node");
        for (int i = 0; i < elements.length; i++) {
            importElement(area, helper, elements[i], baseUri, path);
        }
    }

    protected void convertLinks(Publication srcPub, Area area) {
        Document[] docs = area.getDocuments();
        for (int i = 0; i < docs.length; i++) {
            LinkConverter converter = new LinkConverter(this.manager, getLogger());
            converter.convertUrlsToUuids(srcPub, docs[i], false);
        }
    }

}
