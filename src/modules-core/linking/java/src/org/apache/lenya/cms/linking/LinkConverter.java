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
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.cms.cocoon.components.context.ContextUtility;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.ResourceType;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
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
     * @param useContextPath If the request's context path should be considered.
     */
    public void convertUrlsToUuids(Document doc, boolean useContextPath) {
        convertUrlsToUuids(doc.getPublication(), doc, useContextPath);
    }

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
                Publication pub = examinedDocument.getPublication();
                ChainLinkRewriter incomingRewriter = new ChainLinkRewriter();
                incomingRewriter.add(new RelativeToAbsoluteLinkRewriter(examinedDocument
                        .getCanonicalWebappURL()));
                incomingRewriter.add(new IncomingLinkRewriter(pub));

                // Workaround:
                // We create a new session because the sitetree of the transaction doesn't yet
                // contain
                // references to any new documents that were uploaded during the transaction.
                // See https://issues.apache.org/bugzilla/show_bug.cgi?id=47621
                Session readOnlySession = RepositoryUtil.createSession(this.manager,
                        examinedDocument.getSession().getIdentity(), false);
                DocumentFactory newFactory = DocumentUtil.createDocumentFactory(this.manager,
                        readOnlySession);
                final LinkRewriter[] rewriters = { new UrlToUuidRewriter(pub.getFactory()),
                        new UrlToUuidRewriter(newFactory) };

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
                            final String destPubUrl = "/" + pub.getId() + "/"
                                    + srcPubUrl.substring(srcPubPrefix.length());
                            boolean rewritten = false;
                            for (int i=0; i<rewriters.length; i++) {
                                final LinkRewriter rewriter = rewriters[i];
                                if (!rewritten && rewriter.matches(destPubUrl)) {
                                    String rewrittenUrl = rewriter.rewrite(destPubUrl);
                                    attribute.setValue(rewrittenUrl);
                                    linksRewritten = true;
                                    rewritten = !rewrittenUrl.equals(destPubUrl);
                                }
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
        String prefix;
        ContextUtility ctxUtil = null;
        try {
            ctxUtil = (ContextUtility) this.manager.lookup(ContextUtility.ROLE);
            prefix = ctxUtil.getRequest().getContextPath();
        } finally {
            if (ctxUtil != null) {
                this.manager.release(ctxUtil);
            }
        }
        return prefix;
    }

}
