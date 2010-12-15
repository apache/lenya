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

package org.apache.lenya.cms.site.tree;

import java.util.ArrayList;
import java.util.List;

import org.apache.avalon.framework.service.Serviceable;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentLocator;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.repository.RepositoryItemFactory;
import org.apache.lenya.cms.site.AbstractSiteManager;
import org.apache.lenya.cms.site.Link;
import org.apache.lenya.cms.site.NodeSet;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.SiteStructure;

/**
 * A tree-based site manager.
 * 
 * @version $Id: TreeSiteManager.java 208766 2005-07-01 16:05:00Z andreas $
 */
@Deprecated
public class TreeSiteManager extends AbstractSiteManager implements Serviceable {

    /**
     * Ctor.
     */
    public TreeSiteManager() {
        // do nothing
    }

    /**
     * Returns the sitetree for a specific area of this publication. Sitetrees are created on demand
     * and are cached.
     * 
     * @param map The document identity map.
     * @param publication The publication.
     * @param area The area.
     * @return A site tree.
     * @throws SiteException if an error occurs.
     */
    public DefaultSiteTree getTree(DocumentFactory map, Publication publication, String area)
            throws SiteException {

        String key = getKey(publication, area);
        DefaultSiteTree sitetree;
        RepositoryItemFactory factory = new SiteTreeFactory(this.manager, getLogger());
        try {
            sitetree = (DefaultSiteTree) map.getSession().getRepositoryItem(factory, key);
        } catch (Exception e) {
            throw new SiteException(e);
        }

        return sitetree;
    }

    protected DefaultSiteTree getTree(Document document) throws SiteException {
        return getTree(document.getFactory(), document.getPublication(), document.getArea());
    }

    /**
     * Returns the ancestors of a resource, beginning with the parent.
     * 
     * @param node The resource.
     * @return A list of resources.
     * @throws SiteException if an error occurs.
     */
    protected List getAncestors(SiteNode node) throws SiteException {
        List ancestors = new ArrayList();
        SiteNode parent = null;
        try {
            if (!node.isTopLevel()) {
                parent = node.getParent();
                ancestors.add(parent);
                ancestors.addAll(getAncestors(parent));
            }
        } catch (Exception e) {
            throw new SiteException(e);
        }
        return ancestors;
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#requires(org.apache.lenya.cms.publication.DocumentFactory,
     *      org.apache.lenya.cms.site.SiteNode, org.apache.lenya.cms.site.SiteNode)
     */
    public boolean requires(DocumentFactory map, SiteNode dependingResource,
            SiteNode requiredResource) throws SiteException {
        return getAncestors(dependingResource).contains(requiredResource);
    }

    public DocumentLocator[] getRequiredResources(DocumentFactory map, final DocumentLocator loc)
            throws SiteException {
        
        List ancestors = new ArrayList();
        DocumentLocator locator = loc;
        while (locator.getParent() != null) {
            DocumentLocator parent = locator.getParent();
            ancestors.add(parent);
            locator = parent;
        }
        return (DocumentLocator[]) ancestors.toArray(new DocumentLocator[ancestors.size()]);
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#getRequiringResources(org.apache.lenya.cms.publication.DocumentFactory,
     *      org.apache.lenya.cms.site.SiteNode)
     */
    public SiteNode[] getRequiringResources(DocumentFactory map, SiteNode resource)
            throws SiteException {

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Obtaining requiring resources of [" + resource + "]");
        }

        NodeSet nodes = new NodeSet(this.manager);
        Publication pub = resource.getStructure().getPublication();
        String area = resource.getStructure().getArea();
        SiteTree tree = getTree(map, pub, area);

        SiteTreeNodeImpl node = (SiteTreeNodeImpl) tree.getNode(resource.getPath());
        if (node != null) {
            List preOrder = node.preOrder();

            // remove original resource (does not require itself)
            preOrder.remove(0);

            for (int i = 0; i < preOrder.size(); i++) {
                SiteTreeNode descendant = (SiteTreeNode) preOrder.get(i);
                nodes.add(descendant);
            }

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Obtaining requiring resources completed.");
            }
        }

        return nodes.getNodes();
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#contains(org.apache.lenya.cms.publication.Document)
     */
    public boolean contains(Document resource) throws SiteException {
        SiteTree tree = getTree(resource);
        return tree.containsByUuid(resource.getUUID(), resource.getLanguage());
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#containsInAnyLanguage(org.apache.lenya.cms.publication.Document)
     */
    public boolean containsInAnyLanguage(Document resource) throws SiteException {
        SiteTree tree = getTree(resource);
        return tree.containsInAnyLanguage(resource.getUUID());
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#copy(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.cms.publication.Document)
     */
    public void copy(Document sourceDocument, Document destinationDocument) throws SiteException {
        DefaultSiteTree destinationTree = getTree(destinationDocument);

        try {
            SiteTreeNode sourceNode = (SiteTreeNode) sourceDocument.getLink().getNode();

            SiteTreeNode[] siblings = sourceNode.getNextSiblings();
            SiteNode parent = sourceNode.getParent();
            String parentId = "";
            if (parent != null) {
                parentId = parent.getPath();
            }
            SiteTreeNode sibling = null;
            String siblingPath = null;

            // same UUID -> insert at the same position
            if (sourceDocument.getUUID().equals(destinationDocument.getUUID())) {
                for (int i = 0; i < siblings.length; i++) {
                    String path = parentId + "/" + siblings[i].getName();
                    sibling = (SiteTreeNode) destinationTree.getNode(path);
                    if (sibling != null) {
                        siblingPath = path;
                        break;
                    }
                }
            }

            if (!sourceNode.hasLink(sourceDocument.getLanguage())) {
                // the node that we're trying to publish
                // doesn't have this language
                throw new SiteException("The node " + sourceDocument.getPath()
                        + " doesn't contain a label for language " + sourceDocument.getLanguage());
            }
            Link link = sourceNode.getLink(sourceDocument.getLanguage());
            SiteTreeNode destinationNode = (SiteTreeNode) destinationTree.getNode(destinationDocument.getPath());
            if (destinationNode == null) {
                if (siblingPath == null) {
                    destinationTree.addNode(destinationDocument.getPath(),
                            destinationDocument.getUUID(),
                            sourceNode.isVisible(),
                            sourceNode.getHref(),
                            sourceNode.getSuffix(),
                            sourceNode.hasLink());
                    destinationTree.addLabel(destinationDocument.getPath(),
                            destinationDocument.getLanguage(),
                            link.getLabel());
                } else {
                    destinationTree.addNode(destinationDocument.getPath(),
                            destinationDocument.getUUID(),
                            sourceNode.isVisible(),
                            sourceNode.getHref(),
                            sourceNode.getSuffix(),
                            sourceNode.hasLink(),
                            siblingPath);
                    destinationTree.addLabel(destinationDocument.getPath(),
                            destinationDocument.getLanguage(),
                            link.getLabel());
                }

            } else {
                // if the node already exists in the live
                // tree simply insert the label in the
                // live tree
                destinationDocument.getLink().setLabel(link.getLabel());
            }
        } catch (DocumentException e) {
            throw new SiteException(e);
        }

    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#setVisibleInNav(org.apache.lenya.cms.publication.Document,
     *      boolean)
     */
    public void setVisibleInNav(Document document, boolean visibleInNav) throws SiteException {
        SiteTree tree = getTree(document);
        try {
            tree.getNode(document.getPath()).setVisible(visibleInNav);
        } catch (DocumentException e) {
            throw new SiteException(e);
        }
    }

    /**
     * Returns the label object of a document.
     * 
     * @param document The document.
     * @return A label.
     * @throws SiteException if an error occurs.
     */
    protected Link getLabelObject(Document document) throws SiteException {
        Link label = null;
        SiteTree siteTree = getTree(document);
        if (siteTree != null) {
            SiteTreeNode node = (SiteTreeNode) siteTree.getByUuid(document.getUUID(),
                    document.getLanguage()).getNode();
            if (node == null) {
                throw new SiteException("Node for document [" + document + "] does not exist!");
            }
            label = (Link) node.getLink(document.getLanguage());
        }

        if (label == null) {
            throw new SiteException("The label of document [" + document + "] is null!");
        }

        return label;
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#getDocuments(org.apache.lenya.cms.publication.DocumentFactory,
     *      org.apache.lenya.cms.publication.Publication, java.lang.String)
     */
    public Document[] getDocuments(DocumentFactory map, Publication publication, String area)
            throws SiteException {
        try {
            SiteTreeNodeImpl root = (SiteTreeNodeImpl) getTree(map, publication, area).getNode("/");
            List allNodes = root.preOrder();
            List documents = new ArrayList();

            for (int i = 1; i < allNodes.size(); i++) {
                SiteTreeNode node = (SiteTreeNode) allNodes.get(i);
                Document doc = map.get(publication, area, node.getUuid());
                String[] languages = doc.getLanguages();
                for (int l = 0; l < languages.length; l++) {
                    documents.add(doc.getTranslation(languages[l]));
                }
            }
            return (Document[]) documents.toArray(new Document[documents.size()]);
        } catch (Exception e) {
            throw new SiteException(e);
        }
    }

    public void add(String path, Document document) throws SiteException {

        if (contains(document)) {
            throw new SiteException("The document [" + document + "] is already contained!");
        }
        DefaultSiteTree tree = getTree(document);

        SiteTreeNode node;
        if (!tree.contains(path)) {
            // done for side effect of calling addNodNode, not the resulting return value
            node = tree.addNode(path, document.getUUID(), true, null, null, false);
        } else {
            node = (SiteTreeNode) tree.getNode(path);
            if (node.getUuid() != null) {
                ((SiteTreeNodeImpl) node).setUUID(document.getUUID());
            }
        }
        tree.addLabel(path, document.getLanguage(), "");
    }

    public void set(String path, Document document) throws SiteException {

        if (contains(document)) {
            throw new SiteException("The document [" + document + "] is already contained!");
        }
        DefaultSiteTree tree = getTree(document);
        SiteTreeNodeImpl node = (SiteTreeNodeImpl) tree.getNode(path);
        node.setUUID(document.getUUID());
        tree.changed();
    }

    /**
     * @param publication The publication.
     * @param area The area.
     * @return The key to store sitetree objects in the identity map.
     */
    protected String getKey(Publication publication, String area) {
        return publication.getId() + ":" + area;
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#getSiteStructure(org.apache.lenya.cms.publication.DocumentFactory,
     *      org.apache.lenya.cms.publication.Publication, java.lang.String)
     */
    public SiteStructure getSiteStructure(DocumentFactory map, Publication publiation, String area)
            throws SiteException {
        return getTree(map, publiation, area);
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#getAvailableLocator(DocumentFactory,
     *      org.apache.lenya.cms.publication.DocumentLocator)
     */
    public DocumentLocator getAvailableLocator(DocumentFactory factory, DocumentLocator locator)
            throws SiteException {
        return DocumentLocator.getLocator(locator.getPublicationId(),
                locator.getArea(),
                computeUniquePath(factory, locator),
                locator.getLanguage());
    }

    /**
     * compute an unique document id
     * @param factory The factory.
     * @param locator The locator.
     * @return the unique documentid
     * @throws SiteException if an error occurs.
     */
    protected String computeUniquePath(DocumentFactory factory, DocumentLocator locator)
            throws SiteException {
        String path = locator.getPath();

        Publication pub;
        try {
            pub = factory.getPublication(locator.getPublicationId());
        } catch (PublicationException e) {
            throw new SiteException(e);
        }
        SiteTree tree = getTree(factory, pub, locator.getArea());

        String suffix = null;
        int version = 0;
        String idwithoutsuffix = null;

        if (tree.contains(path)) {
            int n = path.lastIndexOf("/");
            String lastToken = "";
            String substring = path;
            if ((n < path.length()) && (n > 0)) {
                lastToken = path.substring(n);
                substring = path.substring(0, n);
            }

            int l = lastToken.length();
            int index = lastToken.lastIndexOf("-");
            if (0 < index && index < l) {
                suffix = lastToken.substring(index + 1);
                idwithoutsuffix = substring + lastToken.substring(0, index);
                version = Integer.parseInt(suffix);
            } else {
                idwithoutsuffix = substring + lastToken;
            }

            while (tree.contains(path)) {
                version = version + 1;
                path = idwithoutsuffix + "-" + version;
            }
        }

        return path;
    }

    public boolean isVisibleInNav(Document document) throws SiteException {
        SiteTree tree = getTree(document);
        try {
            return tree.getNode(document.getPath()).isVisible();
        } catch (DocumentException e) {
            throw new SiteException(e);
        }
    }

    protected String getPath(DocumentFactory factory, Publication pub, String area, String uuid,
            String language) throws SiteException {
        SiteTree tree = getTree(factory, pub, area);
        SiteNode node = tree.getByUuid(uuid, language).getNode();
        if (node == null) {
            throw new SiteException("No node found for [" + pub.getId() + ":" + area + ":" + uuid
                    + ":" + language + "]");
        }
        return node.getPath();
    }

    protected String getUUID(DocumentFactory factory, Publication pub, String area, String path)
            throws SiteException {
        SiteTree tree = getTree(factory, pub, area);
        SiteNode node = tree.getNode(path);
        if (node == null) {
            throw new SiteException("No node found for [" + pub.getId() + ":" + area + ":" + path
                    + "]");
        }
        return node.getUuid();
    }

    protected boolean contains(DocumentFactory factory, DocumentLocator locator)
            throws SiteException {
        Publication pub;
        try {
            pub = factory.getPublication(locator.getPublicationId());
        } catch (PublicationException e) {
            throw new SiteException(e);
        }
        SiteTree tree = getTree(factory, pub, locator.getArea());
        if (tree.contains(locator.getPath())) {
            SiteNode node = tree.getNode(locator.getPath());
            return node.hasLink(locator.getLanguage());
        } else {
            return false;
        }
    }

}
