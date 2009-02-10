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

import java.util.ArrayList;
import java.util.List;

import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentLocator;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.Session;
import org.apache.lenya.cms.repository.RepositoryItemFactory;
import org.apache.lenya.cms.repository.SessionHolder;
import org.apache.lenya.cms.site.AbstractSiteManager;
import org.apache.lenya.cms.site.Link;
import org.apache.lenya.cms.site.NodeSet;
import org.apache.lenya.cms.site.SiteException;
import org.apache.lenya.cms.site.SiteNode;
import org.apache.lenya.cms.site.SiteStructure;
import org.apache.lenya.cms.site.tree.SiteTree;
import org.apache.lenya.cms.site.tree.SiteTreeNode;

/**
 * Tree-based site manager.
 */
public class TreeSiteManager extends AbstractSiteManager {

    private SiteTreeFactory siteTreeFactory;

    /**
     * Returns the sitetree for a specific area of this publication. Sitetrees are created on demand
     * and are cached.
     * 
     * @param area The area.
     * @return A site tree.
     * @throws SiteException if an error occurs.
     */
    protected SiteTree getTree(Area area) throws SiteException {

        String key = getKey(area);
        SiteTree sitetree;
        try {
            SessionHolder sessionHolder = (SessionHolder) area.getPublication().getSession();
            sitetree = (SiteTree) sessionHolder.getRepositorySession().getRepositoryItem(
                    this.siteTreeFactory, key);
        } catch (Exception e) {
            throw new SiteException(e);
        }

        return sitetree;
    }

    /**
     * @param area The area.
     * @return The key to store sitetree objects in the identity map.
     */
    protected String getKey(Area area) {
        return area.getPublication().getId() + ":" + area.getName();
    }

    public void add(String path, Document doc) throws SiteException {
        getTree(doc.area()).add(path, doc);
    }

    public boolean contains(Document doc) throws SiteException {
        return getTree(doc.area()).containsByUuid(doc.getUUID(), doc.getLanguage());
    }

    public boolean containsInAnyLanguage(Document doc) throws SiteException {
        return getTree(doc.area()).containsInAnyLanguage(doc.getUUID());
    }

    public void copy(Document srcDoc, Document destDoc) throws SiteException {
        SiteTree destinationTree = getTree(destDoc.area());

        try {
            TreeNodeImpl sourceNode = (TreeNodeImpl) srcDoc.getLink().getNode();

            SiteTreeNode[] siblings = sourceNode.getNextSiblings();
            SiteNode parent = sourceNode.getParent();
            String parentId = "";
            if (parent != null) {
                parentId = parent.getPath();
            }
            TreeNodeImpl sibling = null;
            String siblingPath = null;

            // same UUID -> insert at the same position
            if (srcDoc.getUUID().equals(destDoc.getUUID())) {
                for (int i = 0; i < siblings.length; i++) {
                    String path = parentId + "/" + siblings[i].getName();
                    sibling = (TreeNodeImpl) destinationTree.getNode(path);
                    if (sibling != null) {
                        siblingPath = path;
                        break;
                    }
                }
            }

            if (!sourceNode.hasLink(srcDoc.getLanguage())) {
                // the node that we're trying to publish
                // doesn't have this language
                throw new SiteException("The node " + srcDoc.getPath()
                        + " doesn't contain a label for language " + srcDoc.getLanguage());
            }

            String destPath = destDoc.getPath();

            Link link = sourceNode.getLink(srcDoc.getLanguage());
            SiteNode destNode = destinationTree.getNode(destPath);
            if (destNode == null) {
                if (siblingPath == null) {
                    // called for side effect of add, not return result
                    destNode = destinationTree.add(destPath);
                } else {
                    // called for side effect of add, not return result
                    destNode = destinationTree.add(destPath, siblingPath);
                }
                destinationTree.add(destPath, destDoc);
            } else {
                destDoc.getLink().setLabel(link.getLabel());
            }
        } catch (DocumentException e) {
            throw new SiteException(e);
        }
    }

    public DocumentLocator getAvailableLocator(Session session, DocumentLocator locator)
            throws SiteException {
        return DocumentLocator.getLocator(locator.getPublicationId(), locator.getArea(),
                computeUniquePath(session, locator), locator.getLanguage());
    }

    /**
     * compute an unique document id
     * @param session The session.
     * @param locator The locator.
     * @return the unique documentid
     * @throws SiteException if an error occurs.
     */
    protected String computeUniquePath(Session session, DocumentLocator locator)
            throws SiteException {
        String path = locator.getPath();

        Publication pub;
        SiteTree tree;
        try {
            pub = session.getPublication(locator.getPublicationId());
            tree = getTree(pub.getArea(locator.getArea()));
        } catch (PublicationException e) {
            throw new SiteException(e);
        }

        String suffix = null;
        int version = 0;
        String idwithoutsuffix = null;

        if (tree.contains(path)) {
            int n = path.lastIndexOf("/");
            String lastToken = "";
            String substring = path;
            lastToken = path.substring(n);
            substring = path.substring(0, n);

            int l = lastToken.length();
            int index = lastToken.lastIndexOf("-");
            if (0 < index && index < l && lastToken.substring(index + 1).matches("[\\d]*")) {
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

    public Document[] getDocuments(Publication pub, String area) throws SiteException {
        Area areaObj = pub.getArea(area);
        SiteTree tree = getTree(areaObj);
        SiteNode[] preOrder = tree.preOrder();
        List docs = new ArrayList();
        for (int i = 0; i < preOrder.length; i++) {
            String[] langs = preOrder[i].getLanguages();
            for (int l = 0; l < langs.length; l++) {
                docs.add(preOrder[i].getLink(langs[l]).getDocument());
            }
        }
        return (Document[]) docs.toArray(new Document[docs.size()]);
    }

    public DocumentLocator[] getRequiredResources(Session session, DocumentLocator loc)
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

    public SiteNode[] getRequiringResources(SiteNode resource) throws SiteException {
        NodeSet nodes = new NodeSet();
        SiteTree tree = (SiteTree) resource.getStructure();

        TreeNode node = (TreeNode) tree.getNode(resource.getPath());
        if (node != null) {
            SiteNode[] preOrder = node.preOrder();

            // exclude original resource (does not require itself)
            for (int i = 1; i < preOrder.length; i++) {
                TreeNode descendant = (TreeNode) preOrder[i];
                nodes.add(descendant);
            }
        }

        return nodes.getNodes();
    }

    public SiteStructure getSiteStructure(Publication publication, String area)
            throws SiteException {
        try {
            return getTree(publication.getArea(area));
        } catch (PublicationException e) {
            throw new SiteException(e);
        }
    }

    public boolean isVisibleInNav(Document document) throws SiteException {
        try {
            return document.getLink().getNode().isVisible();
        } catch (DocumentException e) {
            throw new SiteException(e);
        }
    }

    public boolean requires(SiteNode depending, SiteNode required) throws SiteException {
        return depending.getPath().startsWith(required.getPath() + "/");
    }

    public void set(String path, Document document) throws SiteException {
        if (contains(document)) {
            throw new SiteException("The document [" + document + "] is already contained!");
        }
        SiteTreeImpl tree = (SiteTreeImpl) getTree(document.area());
        TreeNodeImpl node = (TreeNodeImpl) tree.getNode(path);
        node.setUuid(document.getUUID());
        tree.changed();
    }

    public void setVisibleInNav(Document document, boolean visibleInNav) throws SiteException {
        try {
            document.getLink().getNode().setVisible(visibleInNav);
        } catch (DocumentException e) {
            throw new SiteException(e);
        }
    }

    public void setSiteTreeFactory(SiteTreeFactory siteTreeFactory) {
        this.siteTreeFactory = siteTreeFactory;
    }

}
