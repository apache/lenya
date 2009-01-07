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
package org.apache.lenya.cms.site.tree2;

import java.io.OutputStream;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.serialization.Serializer;
import org.apache.excalibur.xml.sax.XMLizable;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.site.Link;
import org.apache.lenya.cms.site.SiteNode;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class SaxTreeWriter extends AbstractLogEnabled implements TreeWriter, XMLizable, Serviceable {

    protected static final String NS = SiteTreeImpl.NAMESPACE;
    protected static final String TYPE_CDATA = "CDATA";
    protected static final String ATTR_ID = SaxTreeBuilder.ATTR_ID;
    protected static final String ATTR_UUID = SaxTreeBuilder.ATTR_UUID;
    protected static final String ATTR_REVISION = SaxTreeBuilder.ATTR_REVISION;
    protected static final String ATTR_VISIBLE = SaxTreeBuilder.ATTR_VISIBLE_IN_NAV;
    protected static final String ATTR_LANG = SaxTreeBuilder.ATTR_XML_LANG;
    protected static final String ELEM_SITE = SaxTreeBuilder.ELEM_SITE;
    protected static final String ELEM_NODE = SaxTreeBuilder.ELEM_NODE;
    protected static final String ELEM_LABEL = SaxTreeBuilder.ELEM_LABEL;

    private SiteTreeImpl tree;
    private ServiceManager manager;

    public void writeTree(SiteTreeImpl tree) throws Exception {
        this.tree = tree;

        Serializer serializer = null;
        OutputStream stream = null;
        try {
            serializer = (Serializer) this.manager.lookup(ROLE + "Serializer");
            stream = tree.getRepositoryNode().getOutputStream();
            serializer.setOutputStream(stream);
            toSAX(serializer);
        } finally {
            if (stream != null) {
                stream.close();
            }
            if (serializer != null) {
                this.manager.release(serializer);
            }
        }
    }

    public void toSAX(ContentHandler handler) throws SAXException {
        handler.startDocument();
        handler.startPrefixMapping("", SiteTreeImpl.NAMESPACE);

        Node repoNode = this.tree.getRepositoryNode();
        int revision = this.tree.getRevision(repoNode) + 1;
        AttributesImpl attrs = new AttributesImpl();
        attrs
                .addAttribute("", ATTR_REVISION, ATTR_REVISION, TYPE_CDATA, Integer
                        .toString(revision));
        handler.startElement(NS, ELEM_SITE, ELEM_SITE, attrs);

        try {
            toSAX(handler, this.tree.getTopLevelNodes());
        } catch (Exception e) {
            throw new SAXException(e);
        }

        handler.endElement(NS, ELEM_SITE, ELEM_SITE);
        handler.endPrefixMapping("");
        handler.endDocument();
    }

    protected void toSAX(ContentHandler handler, SiteNode[] nodes) throws Exception {
        for (int i = 0; i < nodes.length; i++) {
            toSAX(handler, nodes[i]);
        }
    }

    protected void toSAX(ContentHandler handler, SiteNode node) throws Exception {
        AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute("", ATTR_ID, ATTR_ID, TYPE_CDATA, node.getName());
        String uuid = node.getUuid();
        if (uuid != null) {
            attrs.addAttribute("", ATTR_UUID, ATTR_UUID, TYPE_CDATA, uuid);
        }
        if (!node.isVisible()) {
            attrs.addAttribute("", ATTR_VISIBLE, ATTR_VISIBLE, TYPE_CDATA, Boolean.FALSE.toString());
        }
        handler.startElement(NS, ELEM_NODE, ELEM_NODE, attrs);

        String[] languages = node.getLanguages();
        for (int i = 0; i < languages.length; i++) {
            toSAX(handler, node.getLink(languages[i]));
        }

        toSAX(handler, node.getChildren());
        handler.endElement(NS, ELEM_NODE, ELEM_NODE);
    }

    protected void toSAX(ContentHandler handler, Link link) throws Exception {
        AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute("", ATTR_LANG, ATTR_LANG, TYPE_CDATA, link.getLanguage());
        handler.startElement(NS, ELEM_LABEL, ELEM_LABEL, attrs);
        char[] chars = link.getLabel().toCharArray();
        handler.characters(chars, 0, chars.length);
        handler.endElement(NS, ELEM_LABEL, ELEM_LABEL);
    }

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}
