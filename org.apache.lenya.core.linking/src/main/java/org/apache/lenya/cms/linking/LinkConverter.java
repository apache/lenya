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

import org.apache.avalon.framework.service.ServiceException;
import org.apache.cocoon.processing.ProcessInfoProvider;
import org.apache.cocoon.spring.configurator.WebAppContextUtils;
import org.apache.cocoon.util.AbstractLogEnabled;
import org.apache.commons.logging.Log;
import org.apache.lenya.cms.publication.Document;
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

    /**
     * Creates a link converter.
     * @param logger The logger.
     */
    public LinkConverter(Log logger) {
        setLogger(logger);
    }

    /**
     * Converts all URL-based links to UUID-based links.
     * @param doc The document to convert.
     * @param useContextPath If the request's context path should be considered.
     */
    /*Florent : remove because document.getPublication create a cyclic dependency between document and publication
     * use usecase.getpublicationId in the next method
     * TODO : remove comment when ok
     */
    /*public void convertUrlsToUuids(Document doc, boolean useContextPath) {
        convertUrlsToUuids(doc.getPublication(), doc, useContextPath);
    }*/

    /**
     * Converts all URL-based links to UUID-based links. The link URLs can originate from a
     * different publication.
     * @param srcPub The publication where the content comes from.
     * @param examinedDocument The document in the target publication.
     * @param useContextPath If the request's context path should be considered.
     */
    public void convertUrlsToUuids(Publication srcPub, Document examinedDocument,
            boolean useContextPath) {
        boolean linksRewritten = false;
        try {

            String prefix = useContextPath ? getContextPath() : "";

            ResourceType type = examinedDocument.getResourceType();
            String[] xPaths = type.getLinkAttributeXPaths();

            if (xPaths.length == 0) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug(
                            "Convert links: No XPaths for resource type [" + type.getName() + "]");
                }
            } else {
                //florent : remove cause of cyclic dependency document - publication
            		//Publication pub = examinedDocument.getPublication();
            		//LinkRewriter incomingRewriter = new IncomingLinkRewriter(pub);
            	LinkRewriter incomingRewriter = new IncomingLinkRewriter(srcPub);
                //florent : session is not still accessible throw document, so use publication instead
            	//LinkRewriter urlToUuidRewriter = new UrlToUuidRewriter(examinedDocument.getSession());
            	LinkRewriter urlToUuidRewriter = new UrlToUuidRewriter(srcPub.getSession());

                org.w3c.dom.Document xml = DocumentHelper.readDocument(examinedDocument
                        .getInputStream());

                for (int xPathIndex = 0; xPathIndex < xPaths.length; xPathIndex++) {
                    if (getLogger().isDebugEnabled()) {
                        getLogger()
                                .debug("Convert links: Check XPath [" + xPaths[xPathIndex] + "]");
                    }
                    NodeList nodes = XPathAPI.selectNodeList(xml, xPaths[xPathIndex]);
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
                        final String originalUrl = url.startsWith(prefix) ? url.substring(prefix
                                .length()) : url;
                        final String srcPubUrl;
                        if (incomingRewriter.matches(originalUrl)) {
                            srcPubUrl = incomingRewriter.rewrite(originalUrl);
                        } else {
                            srcPubUrl = originalUrl;
                        }
                        final String srcPubPrefix = "/" + srcPub.getId() + "/";
                        if (srcPubUrl.startsWith(srcPubPrefix)) {
                            //florent : modification cause pub was remplaced by srcpub at the function beginning
                        		//check concequences of this modification
                        		// final String destPubUrl = "/" + pub.getId() + "/"
                            //        + srcPubUrl.substring(srcPubPrefix.length());
                        	final String destPubUrl = "/" + srcPub.getId() + "/"
                                  + srcPubUrl.substring(srcPubPrefix.length());
                            if (urlToUuidRewriter.matches(destPubUrl)) {
                                String rewrittenUrl = urlToUuidRewriter.rewrite(destPubUrl);
                                attribute.setValue(rewrittenUrl);
                                linksRewritten = true;
                            }
                        }
                    }
                }

                if (linksRewritten) {
                    DocumentHelper.writeDocument(xml, examinedDocument.getOutputStream());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error rewriting document: [" + examinedDocument + "]", e);
        }
    }

    protected String getContextPath() throws ServiceException {
        ProcessInfoProvider process = (ProcessInfoProvider) WebAppContextUtils
                .getCurrentWebApplicationContext().getBean(ProcessInfoProvider.ROLE);
        return process.getRequest().getContextPath();
    }

}
