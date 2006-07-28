/*
 * Copyright  1999-2005 The Apache Software Foundation
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
import java.util.List;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentLocator;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.repository.RepositoryItemFactory;
import org.apache.lenya.cms.site.AbstractSiteManager;
import org.apache.lenya.cms.site.Label;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.NodeFactory;
import org.apache.lenya.cms.site.NodeSet;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteStructure;

/**
 * A tree-based site manager.
 * 
 * @version $Id: TreeSiteManager.java 208766 2005-07-01 16:05:00Z andreas $
 */
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
    public SiteTree getTree(DocumentFactory map, Publication publication, String area)
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

    protected SiteTree getTree(Document document) throws SiteException {
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
        SiteNode parent;
        try {
            parent = node.getParent();
            if (parent != null) {
                ancestors.add(parent);
            }
        } catch (Exception e) {
            throw new SiteException(e);
        }
        if (parent != null) {
            ancestors.addAll(getAncestors(parent));
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

    /**
     * @see org.apache.lenya.cms.site.SiteManager#getRequiredResources(org.apache.lenya.cms.publication.DocumentFactory,
     *      org.apache.lenya.cms.site.SiteNode)
     */
    public SiteNode[] getRequiredResources(DocumentFactory map, SiteNode resource)
            throws SiteException {
        List ancestors = getAncestors(resource);
        return (SiteNode[]) ancestors.toArray(new SiteNode[ancestors.size()]);
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

        NodeSet nodes = new NodeSet();
        Publication pub = resource.getPublication();
        String area = resource.getArea();
        SiteTree tree = getTree(map, pub, area);

        SiteTreeNode node = tree.getNode(resource.getPath());
        if (node != null) {
            List preOrder = node.preOrder();

            // remove original resource (does not require itself)
            preOrder.remove(0);

            for (int i = 0; i < preOrder.size(); i++) {
                SiteTreeNode descendant = (SiteTreeNode) preOrder.get(i);
                SiteNode descendantNode = NodeFactory.getNode(pub, area, descendant.getAbsoluteId());
                nodes.add(descendantNode);
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
        SiteTreeNode node = tree.getNodeByUUID(resource.getUUID());
        boolean exists = node != null && node.getLabel(resource.getLanguage()) != null;
        return exists;
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#containsInAnyLanguage(org.apache.lenya.cms.publication.Document)
     */
    public boolean containsInAnyLanguage(Document resource) throws SiteException {
        SiteTree tree = getTree(resource);
        SiteTreeNode node = tree.getNode(resource.getPath());
        return node != null;
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#copy(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.cms.publication.Document)
     */
    public void copy(Document sourceDocument, Document destinationDocument) throws SiteException {
        SiteTree sourceTree = getTree(sourceDocument);
        SiteTree destinationTree = getTree(destinationDocument);

        SiteTreeNode sourceNode = sourceTree.getNode(sourceDocument.getPath());
        if (sourceNode == null) {
            throw new SiteException("The node for source document [" + sourceDocument
                    + "] doesn't exist!");
        }

        SiteTreeNode[] siblings = sourceNode.getNextSiblings();
        SiteTreeNode parent = sourceNode.getParent();
        String parentId = "";
        if (parent != null) {
            parentId = parent.getAbsoluteId();
        }
        SiteTreeNode sibling = null;
        String siblingDocId = null;

        // same document ID -> insert at the same position
        if (sourceDocument.getPath().equals(destinationDocument.getPath())) {
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
            throw new SiteException("The node " + sourceDocument.getPath()
                    + " doesn't contain a label for language " + sourceDocument.getLanguage());
        }
        SiteTreeNode destinationNode = destinationTree.getNode(destinationDocument.getPath());
        if (destinationNode == null) {
            Label[] labels = { label };

            if (siblingDocId == null) {
                destinationTree.addNode(destinationDocument.getPath(),
                        destinationDocument.getUUID(),
                        labels,
                        sourceNode.visibleInNav(),
                        sourceNode.getHref(),
                        sourceNode.getSuffix(),
                        sourceNode.hasLink());
            } else {
                destinationTree.addNode(destinationDocument.getPath(),
                        destinationDocument.getUUID(),
                        labels,
                        sourceNode.visibleInNav(),
                        sourceNode.getHref(),
                        sourceNode.getSuffix(),
                        sourceNode.hasLink(),
                        siblingDocId);
            }

        } else {
            // if the node already exists in the live
            // tree simply insert the label in the
            // live tree
            destinationTree.setLabel(destinationDocument.getPath(), label);
        }

    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#delete(org.apache.lenya.cms.publication.Document)
     */
    public void delete(Document document) throws SiteException {
        SiteTree tree = getTree(document.getFactory(),
                document.getPublication(),
                document.getArea());

        SiteTreeNode node = tree.getNode(document.getPath());

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
            tree.removeNode(document.getPath());
        } else {
            tree.save();
        }
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

        SiteTree tree = getTree(document);
        tree.setLabel(document.getPath(), labelObject);
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#setVisibleInNav(org.apache.lenya.cms.publication.Document,
     *      boolean)
     */
    public void setVisibleInNav(Document document, boolean visibleInNav) throws SiteException {
        SiteTree tree = getTree(document);
        tree.setVisibleInNav(document.getPath(), visibleInNav);
    }

    /**
     * Returns the label object of a document.
     * 
     * @param document The document.
     * @return A label.
     * @throws SiteException if an error occurs.
     */
    protected Label getLabelObject(Document document) throws SiteException {
        Label label = null;
        SiteTree siteTree = getTree(document);
        if (siteTree != null) {
            SiteTreeNode node = siteTree.getNodeByUUID(document.getUUID());
            if (node == null) {
                throw new SiteException("Node for document [" + document + "] does not exist!");
            }
            label = node.getLabel(document.getLanguage());
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
            List allNodes = getTree(map, publication, area).getNode("/").preOrder();
            List documents = new ArrayList();

            for (int i = 1; i < allNodes.size(); i++) {
                SiteTreeNode node = (SiteTreeNode) allNodes.get(i);
                Document doc = map.get(publication, area, node.getUUID());
                String[] languages = doc.getLanguages();
                for (int l = 0; l < languages.length; l++) {
                    documents.add(map.getLanguageVersion(doc, languages[l]));
                }
            }
            return (Document[]) documents.toArray(new Document[documents.size()]);
        } catch (Exception e) {
            throw new SiteException(e);
        }
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#add(org.apache.lenya.cms.publication.Document)
     */
    public void add(String path, Document document) throws SiteException {

        if (contains(document)) {
            throw new SiteException("The document [" + document + "] is already contained!");
        }
        SiteTree tree = getTree(document);
        Label label = new Label("", document.getLanguage());

        SiteTreeNode node = tree.getNode(path);
        if (node == null) {
            Label[] labels = { label };
            tree.addNode(path, document.getUUID(), labels, true, null, null, false);
        } else {
            tree.addLabel(path, label);
        }

    }

    /**
     * @param publication The publication.
     * @param area The area.
     * @return The key to store sitetree objects in the identity map.
     */
    protected String getKey(Publication publication, String area) {
        return publication.getId() + ":" + area;
    }

    protected ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
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
     * 
     * @param document The document.
     * @return the unique documentid
     * @throws SiteException if an error occurs.
     */
    protected String computeUniquePath(DocumentFactory factory, DocumentLocator locator)
            throws SiteException {
        String path = locator.getPath();

        Publication pub;
        try {
            pub = PublicationUtil.getPublication(this.manager, locator.getPublicationId());
        } catch (PublicationException e) {
            throw new SiteException(e);
        }
        SiteTree tree = getTree(factory, pub, locator.getArea());

        SiteTreeNode node = tree.getNode(path);
        String suffix = null;
        int version = 0;
        String idwithoutsuffix = null;

        if (node != null) {
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

            while (node != null) {
                version = version + 1;
                path = idwithoutsuffix + "-" + version;
                node = tree.getNode(path);
            }
        }

        return path;
    }

    public boolean isVisibleInNav(Document document) throws SiteException {
        SiteTree tree = getTree(document);
        return tree.isVisibleInNav(document.getPath());
    }

    public String getPath(DocumentFactory factory, Publication pub, String area, String uuid)
            throws SiteException {
        SiteTree tree = getTree(factory, pub, area);
        SiteTreeNode node = tree.getNodeByUUID(uuid);
        if (node == null) {
            throw new SiteException("No node found for [" + pub.getId() + ":" + area + ":" + uuid
                    + "]");
        }
        return node.getAbsoluteId();
    }

    public String getUUID(DocumentFactory factory, Publication pub, String area, String path)
            throws SiteException {
        SiteTree tree = getTree(factory, pub, area);
        SiteTreeNode node = tree.getNode(path);
        if (node == null) {
            throw new SiteException("No node found for [" + pub.getId() + ":" + area + ":" + path
                    + "]");
        }
        return node.getUUID();
    }

    public boolean contains(DocumentFactory factory, Publication pub, String area, String path)
            throws SiteException {
        SiteTree tree = getTree(factory, pub, area);
        return tree.getNode(path) != null;
    }

    public boolean contains(DocumentFactory factory, DocumentLocator locator) throws SiteException {
        Publication pub;
        try {
            pub = PublicationUtil.getPublication(this.manager, locator.getPublicationId());
        } catch (PublicationException e) {
            throw new SiteException(e);
        }
        SiteTree tree = getTree(factory, pub, locator.getArea());
        SiteTreeNode node = tree.getNode(locator.getPath());
        if (node == null) {
            return false;
        } else {
            return node.getLabel(locator.getLanguage()) != null;
        }
    }

}
