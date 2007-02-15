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
package org.apache.lenya.cms.linking;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.ResourceType;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Utility class to convert <code>lenya-document:</code> links from and to URL links.
 */
public class LinkConverter extends AbstractLogEnabled {
    
    private ServiceManager manager;

    /**
     * Creates a link converter.
     * @param manager The service manager.
     * @param logger The logger.
     */
    public LinkConverter(ServiceManager manager, Logger logger) {
        ContainerUtil.enableLogging(this, logger);
        this.manager = manager;
    }
    
    /**
     * Converts all URL-based links to UUID-based links.
     * @param doc The document to convert.
     */
    public void convertUrlsToUuids(Document doc) {
        convertUrlsToUuids(doc.getPublication(), doc);
    }

    /**
     * Converts all URL-based links to UUID-based links. The link URLs can
     * originate from a different publication.
     * @param srcPub The publication where the content comes from.
     * @param examinedDocument The document in the target publication.
     */
    public void convertUrlsToUuids(Publication srcPub, Document examinedDocument) {
        boolean linksRewritten = false;
        LinkResolver linkResolver = null;
        try {
            ResourceType type = examinedDocument.getResourceType();
            String[] xPaths = type.getLinkAttributeXPaths();

            if (xPaths.length == 0) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug(
                            "Convert links: No XPaths for resource type [" + type.getName() + "]");
                }
            } else {
                linkResolver = (LinkResolver) this.manager.lookup(LinkResolver.ROLE);
                DocumentFactory factory = examinedDocument.getFactory();

                org.w3c.dom.Document xmlDocument = DocumentHelper.readDocument(examinedDocument.getInputStream());

                for (int xPathIndex = 0; xPathIndex < xPaths.length; xPathIndex++) {
                    if (getLogger().isDebugEnabled()) {
                        getLogger()
                                .debug("Convert links: Check XPath [" + xPaths[xPathIndex] + "]");
                    }
                    NodeList nodes = XPathAPI.selectNodeList(xmlDocument, xPaths[xPathIndex]);
                    for (int nodeIndex = 0; nodeIndex < nodes.getLength(); nodeIndex++) {
                        Node node = nodes.item(nodeIndex);
                        if (node.getNodeType() != Node.ATTRIBUTE_NODE) {
                            throw new RuntimeException("The XPath [" + xPaths[xPathIndex]
                                    + "] may only match attribute nodes!");
                        }
                        Attr attribute = (Attr) node;
                        final String url = attribute.getValue();
                        if (getLogger().isDebugEnabled()) {
                            getLogger().debug("Convert links: Check URL [" + url + "]");
                        }

                        if (url.startsWith("/" + srcPub.getId() + "/" + examinedDocument.getArea()
                                + "/")) {
                            String targetPubId = examinedDocument.getPublication().getId();
                            final String webappUrl = "/" + targetPubId
                                    + url.substring(("/" + srcPub.getId()).length());
                            if (factory.isDocument(webappUrl)) {
                                Document targetDocument = factory.getFromURL(webappUrl);

                                if (getLogger().isDebugEnabled()) {
                                    getLogger().debug(
                                            "Convert links: Check webapp URL [" + webappUrl + "]");
                                }

                                Link link = new Link();
                                link.setUuid(targetDocument.getUUID());
                                attribute.setValue(link.getUri());
                                linksRewritten = true;
                            }
                        }
                    }
                }

                if (linksRewritten) {
                    DocumentHelper.writeDocument(xmlDocument, examinedDocument.getOutputStream());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error rewriting document: [" + examinedDocument + "]", e);
        } finally {
            if (linkResolver != null) {
                this.manager.release(linkResolver);
            }
        }
    }

}