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

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.excalibur.xml.sax.SAXParser;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.site.Link;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class SaxTreeBuilder extends AbstractLogEnabled implements TreeBuilder, Serviceable,
        ContentHandler {

    protected static final String ATTR_XML_LANG = "xml:lang";
    protected static final String ELEM_SITE = "site";
    protected static final String ELEM_NODE = "node";
    protected static final String ELEM_LABEL = "label";
    protected static final String ATTR_ID = "id";
    protected static final String ATTR_UUID = "uuid";
    protected static final String ATTR_VISIBLE_IN_NAV = "visibleinnav";
    protected static final String ATTR_REVISION = "revision";

    private ServiceManager manager;
    private TreeNodeImpl currentNode;
    private StringBuffer text = new StringBuffer();
    private Link currentLink;

    public void buildTree(SiteTreeImpl tree) throws Exception {
        SAXParser parser = null;
        try {
            this.currentNode = tree.getRoot();
            Node node = tree.getRepositoryNode();

            if (node.exists() && node.getContentLength() > 0) {
                parser = (SAXParser) this.manager.lookup(SAXParser.ROLE);
                parser.parse(new InputSource(node.getInputStream()), this);
            }
        } finally {
            if (parser != null) {
                this.manager.release(parser);
            }
        }
    }

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    public void characters(char[] chars, int start, int length) throws SAXException {
        this.text.append(chars, start, length);
    }

    public void endDocument() throws SAXException {
    }

    public void endPrefixMapping(String arg0) throws SAXException {
    }

    public void ignorableWhitespace(char[] arg0, int arg1, int arg2) throws SAXException {
    }

    public void processingInstruction(String arg0, String arg1) throws SAXException {
    }

    public void setDocumentLocator(Locator arg0) {
    }

    public void skippedEntity(String arg0) throws SAXException {
    }

    public void startDocument() throws SAXException {
    }

    public void startElement(String uri, String localName, String qName, Attributes attrs)
            throws SAXException {
        try {
            if (localName.equals(ELEM_SITE)) {
                final int treeRevision = Integer.valueOf(attrs.getValue("revision")).intValue();
                final SiteTreeImpl tree = this.currentNode.getTree();
                final int latestRevision = tree.getRevision(tree.getRepositoryNode());
                if (treeRevision != latestRevision) {
                    final String message = "Tree revision " + treeRevision + " does not match RC revision "
                            + latestRevision + ". Actually this should never happen, but it is probably "
                            + "nothing to worry about.";
                    getLogger().warn(message);
                }
                tree.setRevision(treeRevision);
            }
            if (localName.equals(ELEM_NODE)) {
                String id = attrs.getValue(ATTR_ID);
                String visibleString = attrs.getValue(ATTR_VISIBLE_IN_NAV);
                boolean visible = visibleString == null ? true : Boolean.valueOf(visibleString)
                        .booleanValue();
                TreeNodeImpl node = (TreeNodeImpl) this.currentNode.addChild(id, visible);
                String uuid = attrs.getValue(ATTR_UUID);
                if (uuid != null) {
                    node.setUuid(uuid);
                }
                this.currentNode = node;
            } else if (localName.equals(ELEM_LABEL)) {
                String lang = attrs.getValue(ATTR_XML_LANG);
                this.currentLink = this.currentNode.addLink(lang, "");
            }
            this.text.setLength(0);
        } catch (Exception e) {
            throw new SAXException(e);
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        try {
            if (localName.equals(ELEM_NODE)) {
                TreeNodeImpl node = this.currentNode;
                this.currentNode = node.isTopLevel() ? node.getTree().getRoot()
                        : (TreeNodeImpl) node.getParent();
            }
            if (localName.equals(ELEM_LABEL)) {
                String label = this.text.toString();
                this.currentLink.setLabel(label);
                this.currentLink = null;
            }
            this.text.setLength(0);
        } catch (Exception e) {
            throw new SAXException(e);
        }
    }

    public void startPrefixMapping(String arg0, String arg1) throws SAXException {
    }

}
