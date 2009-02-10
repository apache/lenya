package org.apache.lenya.cms.site.tree2;

import org.apache.cocoon.util.AbstractLogEnabled;
import org.apache.excalibur.xml.sax.SAXParser;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.site.Link;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class SaxTreeBuilder extends AbstractLogEnabled implements TreeBuilder, ContentHandler {

    protected static final String ATTR_XML_LANG = "xml:lang";
    protected static final String ELEM_SITE = "site";
    protected static final String ELEM_NODE = "node";
    protected static final String ELEM_LABEL = "label";
    protected static final String ATTR_ID = "id";
    protected static final String ATTR_UUID = "uuid";
    protected static final String ATTR_VISIBLE_IN_NAV = "visibleinnav";
    protected static final String ATTR_REVISION = "revision";

    private TreeNodeImpl currentNode;
    private StringBuffer text = new StringBuffer();
    private Link currentLink;
    private SAXParser parser;

    public void buildTree(SiteTreeImpl tree) throws Exception {
        SAXParser parser = null;
        this.currentNode = tree.getRoot();
        Node node = tree.getRepositoryNode();

        if (node.exists() && node.getContentLength() > 0) {
            this.parser.parse(new InputSource(node.getInputStream()), this);
        }
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
                int revision = Integer.valueOf(attrs.getValue("revision")).intValue();
                SiteTreeImpl tree = this.currentNode.getTree();
                int latestRevision = tree.getRevision(tree.getRepositoryNode());
                if (revision != latestRevision) {
                    String message = "Trying to load outdated tree, revision should be "
                            + latestRevision + " but is " + revision;
                    getLogger().error(message);
                }
                tree.setRevision(revision);
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

    public void setParser(SAXParser parser) {
        this.parser = parser;
    }

}
