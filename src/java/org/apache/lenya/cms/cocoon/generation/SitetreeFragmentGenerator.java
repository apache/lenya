/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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

/* @version $Id$*/

package org.apache.lenya.cms.cocoon.generation;

import java.io.IOException;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.generation.AbstractGenerator;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.site.Label;
import org.apache.lenya.cms.site.tree.SiteTree;
import org.apache.lenya.cms.site.tree.TreeSiteManager;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.tree.SiteTreeNode;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Generates a fragment of the navigation XML from the sitetree, 
 * corresponding to a given node.
 * The node is specified by the sitemap parameters area/documentid.
 * If the sitemap parameter initialTree is true, the top nodes
 * of the tree will be generated and the node given by the sitemap
 * parameters area/documentid will be unfolded.
 * If initialTree is false, only the children of the selected node
 * will be generated.
 * 
 */
public class SitetreeFragmentGenerator extends AbstractGenerator {

    protected Publication publication;
    private DocumentIdentityMap identityMap;
    
    /** Parameter which denotes the documentid  of the clicked node */
    protected String documentid;
    
    /** Parameter which denotes the area of the clicked node */
    protected String area;
    
    /** Parameter which decides if the initial tree with the root nodes is generated */
    protected boolean initialTree;
    
    /** FIXME: should pass this as a parameter */
    protected String[] areas = null;
    
    /** Convenience object, so we don't need to create an AttributesImpl for every element. */
    protected AttributesImpl attributes;
    
    protected static final String PARAM_AREA = "area";
    protected static final String PARAM_DOCUMENTID = "documentid";
    protected static final String PARAM_INITIAL = "initial";
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
    protected static final String ATTR_LABEL = "label";
    protected static final String ATTR_VISIBLEINNAV = "visibleinnav";
    protected static final String ATTR_LINK = "link";
    protected static final String ATTR_BASE = "base";
    protected static final String ATTR_SUFFIX = "suffix";
    protected static final String ATTR_HREF = "href";
    protected static final String ATTR_LANG = "lang";
    
    /**
     * @see org.apache.cocoon.sitemap.SitemapModelComponent#setup(org.apache.cocoon.environment.SourceResolver, java.util.Map, java.lang.String, org.apache.avalon.framework.parameters.Parameters)
     */
    public void setup(SourceResolver resolver, Map objectModel, String src,
            Parameters par) throws ProcessingException, SAXException,
            IOException {
        super.setup(resolver, objectModel, src, par);

        PageEnvelope envelope = null;

        if (getLogger().isDebugEnabled()) {
            Request request = ObjectModelHelper.getRequest(objectModel);
            getLogger().debug("Resolving page envelope for URL [" + request.getRequestURI() + "]");
        }

        area = par.getParameter(PARAM_AREA, null);
        documentid = par.getParameter(PARAM_DOCUMENTID, null);
        
        if (par.isParameter(PARAM_INITIAL)) {
            initialTree = Boolean.valueOf(par.getParameter(PARAM_INITIAL, null)).booleanValue();
        } else {
            initialTree = false;
        }
        
        if (par.isParameter(PARAM_AREAS)) {
            String parAreas = par.getParameter(PARAM_AREAS, null);
            areas = parAreas.split(",");
        } else {
            String temp[] = {"authoring", "archive", "trash"}; 
            areas = temp;
        }
        
        if (this.getLogger().isDebugEnabled()) {
            this.getLogger().debug("Parameter area: " + area);
            this.getLogger().debug("Parameter documentid: " + documentid);
            this.getLogger().debug("Parameter initialTree: " + initialTree);
            String areasStr = "";
            for (int i=0; i<areas.length; i++) areasStr += areas[i]+" ";
            this.getLogger().debug("Parameter areas: " + areasStr);
        }
        
        try {
            PublicationFactory factory = PublicationFactory.getInstance(getLogger());
            Publication pub = factory.getPublication(objectModel);
            this.identityMap = new DocumentIdentityMap(pub);
            envelope = PageEnvelopeFactory.getInstance().getPageEnvelope(this.identityMap, objectModel);            
        } catch (Exception e) {
            throw new ProcessingException("Resolving page envelope failed: ", e);
        }
        
        this.publication = envelope.getPublication();
        this.attributes = new AttributesImpl();

    }

    /**
     * @see org.apache.cocoon.generation.Generator#generate()
     */
    public void generate() throws IOException, SAXException, ProcessingException {
        
        try {
            
            this.contentHandler.startDocument();
            this.contentHandler.startPrefixMapping(PREFIX, URI);

            attributes.clear();
            if (!initialTree) {
                attributes.addAttribute("", ATTR_AREA, ATTR_AREA, "CDATA", area);
                attributes.addAttribute("", ATTR_BASE, ATTR_BASE, "CDATA", documentid);
            }
            
            this.contentHandler.startElement(URI, NODE_FRAGMENT, PREFIX + ':' + NODE_FRAGMENT, attributes);
            
            if (initialTree) {
                for (int i=0; i<areas.length; i++) {
                    generateFragmentInitial(areas[i]);
                }
            } else {
                generateFragment();
            }

            this.contentHandler.endElement(URI, NODE_FRAGMENT, PREFIX + ':' + NODE_FRAGMENT);
            
            this.contentHandler.endPrefixMapping(PREFIX);
            this.contentHandler.endDocument();
            
        } catch (SAXException e) {
            throw new ProcessingException(e);
        } catch (SiteException e) {
            throw new ProcessingException(e);
        }
        
        
    }
    
    /**
     * Generates a fragment of the tree which contains the children of a given node.
     * @throws SiteException
     * @throws SAXException
     * @throws ProcessingException
     */
    protected void generateFragment() 
        throws SiteException, SAXException, ProcessingException {
        
        SiteTree siteTree = null;
        if (!area.equals(Publication.AUTHORING_AREA) && 
            !area.equals(Publication.ARCHIVE_AREA) &&
            !area.equals(Publication.TRASH_AREA) &&
            !area.equals(Publication.LIVE_AREA) &&
            !area.equals(Publication.STAGING_AREA)) {
            throw new ProcessingException("Invalid area: "+area);
        }
        siteTree = ((TreeSiteManager)publication.getSiteManager(identityMap)).getTree(area);
        
        SiteTreeNode node = siteTree.getNode(documentid);
        if (this.getLogger().isDebugEnabled()) {
            this.getLogger().debug("Node with documentid "+documentid+" found.");
        }
        if (node==null) throw new SiteException("Node with documentid "+documentid+" not found.");
        
        SiteTreeNode[] children = node.getChildren();
        
        for (int i=0; i<children.length; i++) {
            startNode(NODE_NODE, children[i]);
            addLabels(children[i]);
            endNode(NODE_NODE);
        }        
    }
    
    /**
     * Generates the top node of the given area and then calls a recursive
     * method to traverse the tree, if the node given by area/documentid is
     * in this area.
     * @param siteArea
     * @throws SiteException
     * @throws SAXException
     * @throws ProcessingException
     */
    protected void generateFragmentInitial(String siteArea) 
        throws SiteException, SAXException, ProcessingException {

        SiteTree siteTree = ((TreeSiteManager)publication.getSiteManager(identityMap)).getTree(siteArea);
        
        String label = "";
        String isFolder = "";
        
        // FIXME: don't hardcode area label
        if (siteArea.equals(Publication.AUTHORING_AREA)) label = "Authoring";
        if (siteArea.equals(Publication.ARCHIVE_AREA)) label = "Archive";
        if (siteArea.equals(Publication.TRASH_AREA)) label = "Trash";
        if (siteArea.equals(Publication.LIVE_AREA)) label = "Live";
        if (siteArea.equals(Publication.STAGING_AREA)) label = "Staging";
        
        if (siteTree.getTopNodes().length>0) isFolder = "true";
        else isFolder = "false";
        
        attributes.clear();
        attributes.addAttribute("", ATTR_AREA, ATTR_AREA, "CDATA", siteArea);
        attributes.addAttribute("", ATTR_FOLDER, ATTR_FOLDER, "CDATA", isFolder);
        attributes.addAttribute("", ATTR_LABEL, ATTR_LABEL, "CDATA", label);
        
        startNode(NODE_SITE);
        
        if (area.equals(siteArea)) {
            generateFragmentRecursive(siteTree.getTopNodes(), documentid);
        }
        
        endNode(NODE_SITE);
    }
    
    /**
     * Follows the documentid to find the way in the sitetree to the specified node
     * and opens all folders on its way.
     * @param nodes
     * @param docid
     * @throws SiteException
     * @throws SAXException
     */
    protected void generateFragmentRecursive(SiteTreeNode[] nodes, String docid) throws SiteException, SAXException {
        String nodeid;
        String childid;
    
        if (nodes==null) return;
        if (docid.startsWith("/")) docid = docid.substring(1);
        if (docid.indexOf("/")!=-1) {
            nodeid = docid.substring(0, docid.indexOf("/"));
            childid = docid.substring(docid.indexOf("/")+1);
        } else {
            nodeid = docid;
            childid = "";
        }
        
        for (int i=0; i<nodes.length; i++) {
            startNode(NODE_NODE, nodes[i]);
            addLabels(nodes[i]);
            if (nodes[i].getId().equals(nodeid)) {
                generateFragmentRecursive(nodes[i].getChildren(), childid);
            }
            endNode(NODE_NODE);                
        }        
    }
        
    /**
     * Begins a named node and calls setNodeAttributes to set its attributes.
     * 
     * @param nodeName  the name of the new node
     * @throws SAXException  if an error occurs while creating the node
     */
    protected void startNode(String nodeName) throws SAXException {
        this.contentHandler.startElement(URI, nodeName, PREFIX + ':' + nodeName, attributes);
    }

    /**
     * Begins a named node and calls setNodeAttributes to set its attributes.
     * 
     * @param nodeName  the name of the new node
     * @param node The attributes are taken from this node
     * @throws SAXException  if an error occurs while creating the node
     */
    protected void startNode(String nodeName, SiteTreeNode node) throws SAXException {
        setNodeAttributes(node);
        this.contentHandler.startElement(URI, nodeName, PREFIX + ':' + nodeName, attributes);
    }

    /**
     * Sets the attributes for a given node. Sets attributes id, href, folder,
     * suffix, basic-url, language-suffix.
     * 
     * @param node
     * @throws SAXException  if an error occurs while setting the attributes
     */
    protected void setNodeAttributes(SiteTreeNode node) throws SAXException {
        attributes.clear();

        String id = node.getId();
        //String isVisible = Boolean.toString(node.visibleInNav());
        String hasLink = Boolean.toString(node.hasLink());
        String href = node.getHref();
        String suffix = node.getSuffix();
        String isFolder = isFolder(node);
        
        if (this.getLogger().isDebugEnabled()) {
            this.getLogger().debug("adding attribute id: " + id);
            //this.getLogger().debug("adding attribute visibleinnav: " + isVisible);
            this.getLogger().debug("adding attribute link: " + hasLink);
            if (href!=null) this.getLogger().debug("adding attribute href: " + href);
            if (suffix!=null) this.getLogger().debug("adding attribute suffix: " + suffix);
            this.getLogger().debug("adding attribute folder: " + isFolder);
        }
        attributes.addAttribute("", ATTR_ID, ATTR_ID, "CDATA", id);
        //attributes.addAttribute("", ATTR_VISIBLEINNAV, ATTR_VISIBLEINNAV, "CDATA", isVisible);
        attributes.addAttribute("", ATTR_LINK, ATTR_LINK, "CDATA", hasLink);
        if (href!=null) attributes.addAttribute("", ATTR_HREF, ATTR_HREF, "CDATA", href);
        if (suffix!=null) attributes.addAttribute("", ATTR_SUFFIX, ATTR_SUFFIX, "CDATA", suffix);
        attributes.addAttribute("", ATTR_FOLDER, ATTR_FOLDER, "CDATA", isFolder);
    } 
            
    /**
     * Returns a value to indicate whether a node is a folder (contains subnodes).
     * With the incremental sitetree loading, we sometimes load nodes which are
     * folders, but we don't load their children. But we still have to know if
     * it's a folder or not, i.e. if it can be opened.
     * @param node 
     * @return "true" or "false"
     */
    protected String isFolder(SiteTreeNode node) {
        if (node.getChildren().length>0) return "true";
        else return "false";
    }
    
       
    /**
     * Ends the named node.
     * 
     * @param nodeName  the name of the new node
     * @throws SAXException  if an error occurs while closing the node
     */
    protected void endNode(String nodeName) throws SAXException {
        this.contentHandler.endElement(URI, nodeName, PREFIX + ':' + nodeName);
    }

    /**
     * Finds all the label children of a node and adds them to the nav xml.
     * @param node
     * @throws SAXException
     */
    protected void addLabels(SiteTreeNode node) throws SAXException {
        Label[] labels = node.getLabels();
        
        for (int i=0; i<labels.length; i++) {
            String lang = labels[i].getLanguage();
            if (lang==null) lang="";
            addLabel(labels[i].getLabel(), lang);
        }
    }
    
    /**
     * Adds a label element of a given language.
     * @param label the value of the label
     * @param language the language of the label
     * @throws SAXException
     */
    protected void addLabel(String label, String language) throws SAXException {
        attributes.clear();
        attributes.addAttribute(XML_URI, ATTR_LANG, XML_PREFIX+":"+ATTR_LANG, "CDATA", language);
        
        this.contentHandler.startElement(URI, NODE_LABEL, PREFIX + ':' + NODE_LABEL, attributes);
        char[] labelArray = label.toCharArray();
        this.contentHandler.characters(labelArray, 0, labelArray.length);
        this.contentHandler.endElement(URI, NODE_LABEL, PREFIX + ':' + NODE_LABEL);        
    }
    
}
