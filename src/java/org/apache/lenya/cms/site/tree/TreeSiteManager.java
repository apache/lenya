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

package org.apache.lenya.cms.site.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.site.AbstractSiteManager;
import org.apache.lenya.cms.site.Label;
import org.apache.lenya.cms.site.SiteException;
import org.apache.log4j.Category;

/**
 * A tree-based site manager.
 * 
 * @version $Id$
 */
public class TreeSiteManager extends AbstractSiteManager {

    private static final Category log = Category.getInstance(TreeSiteManager.class);
    private Map siteTrees = new HashMap();

    /**
     * Ctor.
     */
    public TreeSiteManager() {
    }

    /**
     * Returns the sitetree for a specific area of this publication. Sitetrees are created on demand
     * and are cached.
     * @param area The area.
     * @return A site tree.
     * @throws SiteException if an error occurs.
     */
    public SiteTree getTree(String area) throws SiteException {
        DefaultSiteTree sitetree = null;

        if (this.siteTrees.containsKey(area)) {
            sitetree = (DefaultSiteTree) this.siteTrees.get(area);
        } else {
            sitetree = new DefaultSiteTree(getIdentityMap().getPublication().getDirectory(), area);
            this.siteTrees.put(area, sitetree);
        }
        return sitetree;
    }

    /**
     * Returns the ancestors of a resource, beginning with the parent.
     * @param resource The resource.
     * @return A list of resources.
     * @throws SiteException if an error occurs.
     */
    protected List getAncestors(Document resource) throws SiteException {
        List ancestors = new ArrayList();
        Document parent;
        try {
            parent = getIdentityMap().getFactory().getParent(resource);
        } catch (DocumentBuildException e) {
            throw new SiteException(e);
        }
        if (parent != null) {
            ancestors.add(parent);
            ancestors.addAll(getAncestors(parent));
        }
        return ancestors;
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#requires(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.cms.publication.Document)
     */
    public boolean requires(Document dependingResource, Document requiredResource)
            throws SiteException {
        return getAncestors(dependingResource).contains(requiredResource);
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#getRequiredResources(org.apache.lenya.cms.publication.Document)
     */
    public Document[] getRequiredResources(Document resource) throws SiteException {
        List ancestors = getAncestors(resource);
        return (Document[]) ancestors.toArray(new Document[ancestors.size()]);
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#getRequiringResources(org.apache.lenya.cms.publication.Document)
     */
    public Document[] getRequiringResources(Document resource) throws SiteException {

        if (log.isDebugEnabled()) {
            log.debug("Obtaining requiring resources of [" + resource + "]");
        }

        SiteTree tree = getTree(resource.getArea());

        SiteTreeNode node = tree.getNode(resource.getId());
        List preOrder = node.preOrder();

        // remove original resource (does not require itself)
        preOrder.remove(0);

        Document[] resources = new Document[preOrder.size()];

        try {
            for (int i = 0; i < resources.length; i++) {
                SiteTreeNode descendant = (SiteTreeNode) preOrder.get(i);
                resources[i] = getIdentityMap().getFactory().get(resource.getArea(),
                        descendant.getAbsoluteId());
                if (log.isDebugEnabled()) {
                    log.debug("    Descendant: [" + resources[i] + "]");
                }
            }
        } catch (PublicationException e) {
            throw new SiteException(e);
        }

        if (log.isDebugEnabled()) {
            log.debug("Obtaining requiring resources completed.");
        }

        return resources;
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#contains(org.apache.lenya.cms.publication.Document)
     */
    public boolean contains(Document resource) throws SiteException {
        SiteTreeNode node = getTree(resource.getArea()).getNode(resource.getId());
        return node != null && node.getLabel(resource.getLanguage()) != null;
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#containsInAnyLanguage(org.apache.lenya.cms.publication.Document)
     */
    public boolean containsInAnyLanguage(Document resource) throws SiteException {
        SiteTreeNode node = getTree(resource.getArea()).getNode(resource.getId());
        return node != null;
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#copy(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.cms.publication.Document)
     */
    public void copy(Document sourceDocument, Document destinationDocument) throws SiteException {
        SiteTree sourceTree = getTree(sourceDocument.getArea());
        SiteTree destinationTree = getTree(destinationDocument.getArea());

        SiteTreeNode sourceNode = sourceTree.getNode(sourceDocument.getId());
        if (sourceNode == null) {
            throw new SiteException("The node for source document [" + sourceDocument.getId()
                    + "] doesn't exist!");
        } else {

            SiteTreeNode[] siblings = sourceNode.getNextSiblings();
            String parentId = sourceNode.getParent().getAbsoluteId();
            SiteTreeNode sibling = null;
            String siblingDocId = null;

            // same document ID -> insert at the same position
            if (sourceDocument.getId().equals(destinationDocument.getId())) {
                for (int i = 0; i < siblings.length; i++) {
                    String docId = parentId + "/" + siblings[i].getId();
                    sibling = destinationTree.getNode(docId);
                    if (sibling != null) {
                        siblingDocId = docId;
                        break;
                    }
                }
            }

            Label label = sourceNode.getLabel(sourceDocument.getLanguage());
            if (label == null) {
                // the node that we're trying to publish
                // doesn't have this language
                throw new SiteException("The node " + sourceDocument.getId()
                        + " doesn't contain a label for language " + sourceDocument.getLanguage());
            } else {
                SiteTreeNode destinationNode = destinationTree.getNode(destinationDocument.getId());
                if (destinationNode == null) {
                    Label[] labels = { label };

                    if (siblingDocId == null) {
                        destinationTree.addNode(destinationDocument.getId(), labels, sourceNode
                                .getHref(), sourceNode.getSuffix(), sourceNode.hasLink());
                    } else {
                        destinationTree.addNode(destinationDocument.getId(), labels, sourceNode
                                .getHref(), sourceNode.getSuffix(), sourceNode.hasLink(),
                                siblingDocId);
                    }

                } else {
                    // if the node already exists in the live
                    // tree simply insert the label in the
                    // live tree
                    destinationTree.setLabel(destinationDocument.getId(), label);
                }
            }
        }

        destinationTree.save();
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#delete(org.apache.lenya.cms.publication.Document)
     */
    public void delete(Document document) throws SiteException {
        SiteTree tree = getTree(document.getArea());

        SiteTreeNode node = tree.getNode(document.getId());

        if (node == null) {
            throw new SiteException("Sitetree node for document [" + document + "] does not exist!");
        }

        Label label = node.getLabel(document.getLanguage());

        if (label == null) {
            throw new SiteException("Sitetree label for document [" + document + "] in language ["
                    + document.getLanguage() + "]does not exist!");
        }

        if (node.getLabels().length == 1 && node.getChildren().length > 0) {
            throw new SiteException("Cannot delete last language version of document [" + document
                    + "] because this node has children.");
        }

        node.removeLabel(label);

        if (node.getLabels().length == 0) {
            tree.removeNode(document.getId());
        }

        tree.save();
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#getLabel(org.apache.lenya.cms.publication.Document)
     */
    public String getLabel(Document document) throws SiteException {
        Label label = getLabelObject(document);
        return label.getLabel();
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#setLabel(org.apache.lenya.cms.publication.Document,
     *      java.lang.String)
     */
    public void setLabel(Document document, String label) throws SiteException {
        Label labelObject = getLabelObject(document);
        labelObject.setLabel(label);
        
        SiteTree tree = getTree(document.getArea());
        tree.setLabel(document.getId(), labelObject);
        tree.save();
    }

    /**
     * Returns the label object of a document.
     * @param document The document.
     * @return A label.
     * @throws SiteException if an error occurs.
     */
    protected Label getLabelObject(Document document) throws SiteException {
        Label label = null;
        SiteTree siteTree = getTree(document.getArea());
        if (siteTree != null) {
            SiteTreeNode node = siteTree.getNode(document.getId());
            if (node == null) {
                throw new SiteException("Node for document [" + document + "] does not exist!");
            }
            label = node.getLabel(document.getLanguage());
        }

        if (label == null) {
            throw new SiteException("The label of document [" + document + "] is null!");
        }

        return label;
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#getDocuments(java.lang.String)
     */
    public Document[] getDocuments(String area) throws SiteException {
        try {
            List allNodes = getTree(area).getNode("/").preOrder();
            Document[] documents = new Document[allNodes.size() - 1];

            for (int i = 1; i < allNodes.size(); i++) {
                SiteTreeNode node = (SiteTreeNode) allNodes.get(i);
                documents[i - 1] = getIdentityMap().getFactory().get(area, node.getAbsoluteId());
            }
            return documents;
        } catch (DocumentBuildException e) {
            throw new SiteException(e);
        }
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#add(org.apache.lenya.cms.publication.Document)
     */
    public void add(Document document) throws SiteException {

        if (contains(document)) {
            throw new SiteException("The document [" + document + "] is already contained!");
        }
        SiteTree tree = getTree(document.getArea());
        Label label = new Label("", document.getLanguage());
        
        SiteTreeNode node = tree.getNode(document.getId());
        if (node == null) {
            Label[] labels = { label };
            tree.addNode(document.getId(), labels, null, null, false);
            tree.save();
        }
        else {
            tree.addLabel(document.getId(), label);
        }

    }

}