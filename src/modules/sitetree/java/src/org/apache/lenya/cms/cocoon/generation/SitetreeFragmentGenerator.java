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

/* @version $Id: SitetreeFragmentGenerator.java 159584 2005-03-31 12:49:41Z andreas $*/

package org.apache.lenya.cms.cocoon.generation;

import java.io.IOException;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.generation.ServiceableGenerator;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.site.Link;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.SiteStructure;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Generates a fragment of the navigation XML from the sitetree, corresponding to a given node. The
 * node is specified by the sitemap parameters area/path. If the sitemap parameter initialTree
 * is true, the top nodes of the tree will be generated and the node given by the sitemap parameters
 * area/path will be unfolded. If initialTree is false, only the children of the selected node
 * will be generated.
 */
public class SitetreeFragmentGenerator extends ServiceableGenerator {

    protected Publication publication;

    /** Parameter which denotes the path of the clicked node */
    protected String path;

    /** Parameter which denotes the area of the clicked node */
    protected String area;

    /**
     * Parameter which decides if the initial tree with the root nodes is generated
     */
    protected boolean initialTree;
    
    /**
     * Parameter which decides if the node mime types should be reported
     */
    protected boolean showType;

    /** FIXME: should pass this as a parameter */
    protected String[] areas = null;

    /**
     * Convenience object, so we don't need to create an AttributesImpl for every element.
     */
    protected AttributesImpl attributes;

    protected static final String PARAM_AREA = "area";
    protected static final String PARAM_PATH = "path";
    protected static final String PARAM_INITIAL = "initial";
    protected static final String PARAM_TYPE = "mimetype";
    protected static final String PARAM_AREAS = "areas";

    /** The URI of the namespace of this generator. */
    protected static final String URI = "http://apache.org/cocoon/lenya/sitetree/1.0";
    protected static final String XML_URI = "http://www.w3.org/XML/1998/namespace";

    /** The namespace prefix for this namespace. */
    protected static final String PREFIX = "site";
    protected static final String XML_PREFIX = "xml";

    protected static final String NODE_NODE = "node";
    protected static final String NODE_LABEL = "label";
    protected static final String NODE_SITE = "site";
    protected static final String NODE_FRAGMENT = "fragment";

    protected static final String ATTR_ID = "id";
    protected static final String ATTR_FOLDER = "folder";
    protected static final String ATTR_AREA = "area";
    protected static final String ATTR_PUBLICATION = "publication";
    protected static final String ATTR_LABEL = "label";
    protected static final String ATTR_VISIBLEINNAV = "visibleinnav";
    protected static final String ATTR_LINK = "link";
    protected static final String ATTR_BASE = "base";
    protected static final String ATTR_SUFFIX = "suffix";
    protected static final String ATTR_HREF = "href";
    protected static final String ATTR_UUID = "uuid";
    protected static final String ATTR_LANG = "lang";
    protected static final String ATTR_TYPE = "mimetype";

    /**
     * @see org.apache.cocoon.sitemap.SitemapModelComponent#setup(org.apache.cocoon.environment.SourceResolver,
     *      java.util.Map, java.lang.String, org.apache.avalon.framework.parameters.Parameters)
     */
    public void setup(SourceResolver _resolver, Map _objectModel, String src, Parameters par)
            throws ProcessingException, SAXException, IOException {
        super.setup(_resolver, _objectModel, src, par);

        Request request = ObjectModelHelper.getRequest(_objectModel);
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Resolving page envelope for URL [" + request.getRequestURI() + "]");
        }

        this.area = par.getParameter(PARAM_AREA, null);
        this.path = par.getParameter(PARAM_PATH, null);

        if (par.isParameter(PARAM_INITIAL)) {
            this.initialTree = Boolean.valueOf(par.getParameter(PARAM_INITIAL, null))
                    .booleanValue();
        } else {
            this.initialTree = false;
        }
        
        if (par.isParameter(PARAM_TYPE)) {
            this.showType = Boolean.valueOf(par.getParameter(PARAM_TYPE, null)).booleanValue();
        } else {
            this.showType = false;
        }

        if (par.isParameter(PARAM_AREAS)) {
            String parAreas = par.getParameter(PARAM_AREAS, null);
            this.areas = parAreas.split(",");
        } else {
            String temp[] = { "authoring", "archive", "trash" };
            this.areas = temp;
        }

        if (this.getLogger().isDebugEnabled()) {
            this.getLogger().debug("Parameter area: " + this.area);
            this.getLogger().debug("Parameter path: " + this.path);
            this.getLogger().debug("Parameter initialTree: " + this.initialTree);
            StringBuffer areasStr = new StringBuffer();
            for (int i = 0; i < this.areas.length; i++) {
                areasStr.append(this.areas[i]).append(" ");
            }
            this.getLogger().debug("Parameter areas: " + areasStr.toString());
        }

        try {
            this.publication = PublicationUtil.getPublication(this.manager, _objectModel);
        } catch (Exception e) {
            throw new ProcessingException("Could not create publication: ", e);
        }
        this.attributes = new AttributesImpl();

    }

    /**
     * @see org.apache.cocoon.generation.Generator#generate()
     */
    public void generate() throws IOException, SAXException, ProcessingException {

        try {

            this.contentHandler.startDocument();
            this.contentHandler.startPrefixMapping(PREFIX, URI);

            this.attributes.clear();
            this.attributes.addAttribute("", ATTR_PUBLICATION, ATTR_PUBLICATION, "CDATA",
                    this.publication.getId());
            
            if (!this.initialTree) {
                this.attributes.addAttribute("", ATTR_AREA, ATTR_AREA, "CDATA", this.area);
                this.attributes.addAttribute("", ATTR_BASE, ATTR_BASE, "CDATA", this.path);
            }

            this.contentHandler.startElement(URI,
                    NODE_FRAGMENT,
                    PREFIX + ':' + NODE_FRAGMENT,
                    this.attributes);

            if (this.initialTree) {
                for (int i = 0; i < this.areas.length; i++) {
                    generateFragmentInitial(this.areas[i]);
                }
            } else {
                generateFragment();
            }

            this.contentHandler.endElement(URI, NODE_FRAGMENT, PREFIX + ':' + NODE_FRAGMENT);

            this.contentHandler.endPrefixMapping(PREFIX);
            this.contentHandler.endDocument();

        } catch (final SAXException e) {
            throw new ProcessingException(e);
        } catch (final SiteException e) {
            throw new ProcessingException(e);
        }

    }

    /**
     * Generates a fragment of the tree which contains the children of a given node.
     * @throws SiteException
     * @throws SAXException
     * @throws ProcessingException
     */
    protected void generateFragment() throws SiteException, SAXException, ProcessingException {

        if (!this.area.equals(Publication.AUTHORING_AREA)
                && !this.area.equals(Publication.ARCHIVE_AREA)
                && !this.area.equals(Publication.TRASH_AREA)
                && !this.area.equals(Publication.LIVE_AREA)
                && !this.area.equals(Publication.STAGING_AREA)) {
            throw new ProcessingException("Invalid area: " + this.area);
        }

        try {
            
            SiteStructure site = this.publication.getArea(this.area).getSite();

            SiteNode[] children;
            
            if (this.path.equals("/")) {
                children = site.getTopLevelNodes();
            }
            else {
                SiteNode node = site.getNode(this.path);
                children = node.getChildren();
            }

            addNodes(children);
        } catch (PublicationException e) {
            throw new ProcessingException(e);
        }
    }

    /**
     * Adds the given nodes (not recursive).
     * @param children
     * @throws SAXException
     * @throws SiteException 
     */
    protected void addNodes(SiteNode[] children) throws SAXException, SiteException {
        for (int i = 0; i < children.length; i++) {
            startNode(NODE_NODE, children[i]);
            addLabels(children[i]);
            endNode(NODE_NODE);
        }
    }

    /**
     * Generates the top node of the given area and then calls a recursive method to traverse the
     * tree, if the node given by area/path is in this area.
     * @param siteArea
     * @throws SiteException
     * @throws SAXException
     * @throws ProcessingException
     */
    protected void generateFragmentInitial(String siteArea) throws SiteException, SAXException,
            ProcessingException {
        
        ServiceSelector selector = null;
        SiteManager siteManager = null;
        try {
            selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE + "Selector");
            siteManager = (SiteManager) selector.select(this.publication.getSiteManagerHint());
            SiteStructure siteTree = this.publication.getArea(siteArea).getSite();

            String label = "";
            String isFolder = "";

            // FIXME: don't hardcode area label
            if (siteArea.equals(Publication.AUTHORING_AREA))
                label = "Authoring";
            if (siteArea.equals(Publication.ARCHIVE_AREA))
                label = "Archive";
            if (siteArea.equals(Publication.TRASH_AREA))
                label = "Trash";
            if (siteArea.equals(Publication.LIVE_AREA))
                label = "Live";
            if (siteArea.equals(Publication.STAGING_AREA))
                label = "Staging";

            if (siteTree.getTopLevelNodes().length > 0)
                isFolder = "true";
            else
                isFolder = "false";

            this.attributes.clear();
            this.attributes.addAttribute("", ATTR_AREA, ATTR_AREA, "CDATA", siteArea);
            this.attributes.addAttribute("", ATTR_FOLDER, ATTR_FOLDER, "CDATA", isFolder);
            this.attributes.addAttribute("", ATTR_LABEL, ATTR_LABEL, "CDATA", label);

            startNode(NODE_SITE);

            if (this.area.equals(siteArea)) {
                generateFragmentRecursive(siteTree.getTopLevelNodes(), this.path);
            }

            endNode(NODE_SITE);
        } catch (Exception e) {
            throw new ProcessingException(e);
        } finally {
            if (selector != null) {
                if (siteManager != null) {
                    selector.release(siteManager);
                }
                this.manager.release(selector);
            }
        }
    }

    /**
     * Follows the path to find the way in the sitetree to the specified node and opens all
     * folders on its way.
     * @param nodes
     * @param path
     * @throws SiteException
     * @throws SAXException
     */
    protected void generateFragmentRecursive(SiteNode[] nodes, String path)
            throws SiteException, SAXException {
        String nodeid;
        String childid;

        if (nodes == null)
            return;
        if (path.startsWith("/"))
            path = path.substring(1);
        if (path.indexOf("/") != -1) {
            nodeid = path.substring(0, path.indexOf("/"));
            childid = path.substring(path.indexOf("/") + 1);
        } else {
            nodeid = path;
            childid = "";
        }

        for (int i = 0; i < nodes.length; i++) {
            addNodeRecursive(nodes[i], nodeid, childid);
        }
    }

    /**
     * Adds the given node, and if the node's id matched the given nodeid, it continues recursively.
     * @param node
     * @param nodeid
     * @param childid
     * @throws SAXException
     * @throws SiteException
     */
    protected void addNodeRecursive(SiteNode node, String nodeid, String childid)
            throws SAXException, SiteException {
        startNode(NODE_NODE, node);
        addLabels(node);
        if (node.getName().equals(nodeid)) {
            generateFragmentRecursive(node.getChildren(), childid);
        }
        endNode(NODE_NODE);
    }

    /**
     * Begins a named node and calls setNodeAttributes to set its attributes.
     * @param nodeName the name of the new node
     * @throws SAXException if an error occurs while creating the node
     */
    protected void startNode(String nodeName) throws SAXException {
        this.contentHandler.startElement(URI, nodeName, PREFIX + ':' + nodeName, this.attributes);
    }

    /**
     * Begins a named node and calls setNodeAttributes to set its attributes.
     * @param nodeName the name of the new node
     * @param node The attributes are taken from this node
     * @throws SAXException if an error occurs while creating the node
     * @throws SiteException 
     */
    protected void startNode(String nodeName, SiteNode node) throws SAXException, SiteException {
        setNodeAttributes(node);
        this.contentHandler.startElement(URI, nodeName, PREFIX + ':' + nodeName, this.attributes);
    }

    /**
     * Sets the attributes for a given node. Sets attributes id, href, folder, suffix, basic-url,
     * language-suffix.
     * @param node
     * @throws SAXException if an error occurs while setting the attributes
     * @throws SiteException 
     */
    protected void setNodeAttributes(SiteNode node) throws SAXException, SiteException {
        this.attributes.clear();

        String id = node.getName();
        // String isVisible = Boolean.toString(node.visibleInNav());
        String hasLink = Boolean.toString(node.hasLink());
        String href = node.getHref();
        String suffix = node.getSuffix();
        String isFolder = Boolean.toString(isFolder(node));
        String uuid = node.getUuid();

        if (this.getLogger().isDebugEnabled()) {
            this.getLogger().debug("adding attribute id: " + id);
            // this.getLogger().debug("adding attribute visibleinnav: " +
            // isVisible);
            this.getLogger().debug("adding attribute link: " + hasLink);
            if (href != null)
                this.getLogger().debug("adding attribute href: " + href);
            if (suffix != null)
                this.getLogger().debug("adding attribute suffix: " + suffix);
            this.getLogger().debug("adding attribute folder: " + isFolder);
        }
        this.attributes.addAttribute("", ATTR_ID, ATTR_ID, "CDATA", id);
        // attributes.addAttribute("", ATTR_VISIBLEINNAV, ATTR_VISIBLEINNAV,
        // "CDATA", isVisible);
        this.attributes.addAttribute("", ATTR_LINK, ATTR_LINK, "CDATA", hasLink);
        if (href != null)
            this.attributes.addAttribute("", ATTR_HREF, ATTR_HREF, "CDATA", href);
        if (suffix != null)
            this.attributes.addAttribute("", ATTR_SUFFIX, ATTR_SUFFIX, "CDATA", suffix);
        if (uuid != null)
            this.attributes.addAttribute("", ATTR_UUID, ATTR_UUID, "CDATA", uuid);
        this.attributes.addAttribute("", ATTR_FOLDER, ATTR_FOLDER, "CDATA", isFolder);
        
        if (this.showType) {
            try {
                String type = this.publication.getArea(this.area)
                    .getDocument(node.getUuid(),this.publication.getDefaultLanguage())
                    .getMimeType();
                this.attributes.addAttribute("",ATTR_TYPE, ATTR_TYPE, "CDATA", type);
            } catch (PublicationException e) {
                throw new SiteException(e);
            }
        }
            
    }

    /**
     * Returns a value to indicate whether a node is a folder (contains subnodes). With the
     * incremental sitetree loading, we sometimes load nodes which are folders, but we don't load
     * their children. But we still have to know if it's a folder or not, i.e. if it can be opened.
     * @param node
     * @return A boolean value.
     */
    protected boolean isFolder(SiteNode node) {
        if (node.getChildren().length > 0)
            return true;
        return false;
    }

    /**
     * Ends the named node.
     * @param nodeName the name of the new node
     * @throws SAXException if an error occurs while closing the node
     */
    protected void endNode(String nodeName) throws SAXException {
        this.contentHandler.endElement(URI, nodeName, PREFIX + ':' + nodeName);
    }

    /**
     * Finds all the label children of a node and adds them to the nav xml.
     * @param node
     * @throws SAXException
     */
    protected void addLabels(SiteNode node) throws SAXException {
        String[] languages = node.getLanguages();

        for (int i = 0; i < languages.length; i++) {
            Link link;
            try {
                link = node.getLink(languages[i]);
            } catch (SiteException e) {
                throw new RuntimeException(e);
            }
            addLabel(link.getLabel(), languages[i]);
        }
    }

    /**
     * Adds a label element of a given language.
     * @param label the value of the label
     * @param language the language of the label
     * @throws SAXException
     */
    protected void addLabel(String label, String language) throws SAXException {
        this.attributes.clear();
        this.attributes.addAttribute(XML_URI,
                ATTR_LANG,
                XML_PREFIX + ":" + ATTR_LANG,
                "CDATA",
                language);

        this.contentHandler.startElement(URI,
                NODE_LABEL,
                PREFIX + ':' + NODE_LABEL,
                this.attributes);
        char[] labelArray = label.toCharArray();
        this.contentHandler.characters(labelArray, 0, labelArray.length);
        this.contentHandler.endElement(URI, NODE_LABEL, PREFIX + ':' + NODE_LABEL);
    }

}
