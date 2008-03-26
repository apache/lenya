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

import java.util.HashSet;
import java.util.Set;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

/**
 * Basic link manager implementation which searches for links by parsing the
 * document content. For better performance use an implementation which is based
 * on meta data or a centralized link registry.
 */
public class ContentLinkManager extends AbstractLogEnabled implements LinkManager, Serviceable {

    protected ServiceManager manager;

    public Link[] getLinksFrom(Document source) {

        Set links = new HashSet();

        try {
            String[] xPaths = source.getResourceType().getLinkAttributeXPaths();
            if (xPaths.length > 0) {
                org.w3c.dom.Document xml = DocumentHelper.readDocument(source.getInputStream());

                if (xml == null) {
                    throw new RuntimeException("The document [" + source
                            + "] doesn't contain any XML content.");
                }

                for (int i = 0; i < xPaths.length; i++) {
                    NodeIterator iter = XPathAPI.selectNodeIterator(xml, xPaths[i]);
                    Node node;
                    while ((node = iter.nextNode()) != null) {
                        Attr attr = (Attr) node;
                        String uri = getDocumentUri(attr.getValue());
                        if (isLinkUri(uri)) {
                            links.add(new Link(uri));
                        }
                    }
                }
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return (Link[]) links.toArray(new Link[links.size()]);
    }

    /**
     * @param uri The URI as used in the content.
     * @return The actual document URI without anchor and query string.
     */
    protected String getDocumentUri(String uri) {
        String docUri = uri;
        docUri = removeSuffix(docUri, '#');
        docUri = removeSuffix(docUri, '?');
        return docUri;
    }

    protected String removeSuffix(String docUri, char delimiter) {
        int anchorIndex = docUri.indexOf(delimiter);
        if (anchorIndex > -1) {
            docUri = docUri.substring(0, anchorIndex);
        }
        return docUri;
    }

    protected boolean isLinkUri(String uri) {
        return uri.startsWith(LinkResolver.SCHEME + ":");
    }

    public Document[] getReferencingDocuments(Document target) {

        Document[] allDocs = target.area().getDocuments();
        Set docs = new HashSet();

        LinkResolver resolver = null;
        try {
            resolver = (LinkResolver) this.manager.lookup(LinkResolver.ROLE);
            for (int d = 0; d < allDocs.length; d++) {

                Link[] links = getLinksFrom(allDocs[d]);
                for (int l = 0; l < links.length; l++) {
                    LinkTarget linkTarget = resolver.resolve(allDocs[d], links[l].getUri());
                    if (linkTarget.exists() && linkTarget.getDocument().equals(target)) {
                        docs.add(allDocs[d]);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (resolver != null) {
                this.manager.release(resolver);
            }
        }
        return (Document[]) docs.toArray(new Document[docs.size()]);
    }

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}
