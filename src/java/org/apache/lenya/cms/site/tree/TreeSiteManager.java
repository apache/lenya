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
import java.util.List;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.site.AbstractSiteManager;
import org.apache.lenya.cms.site.SiteException;
import org.apache.log4j.Category;

/**
 * A tree-based site manager.
 * 
 * @version $Id: TreeSiteManager.java,v 1.4 2004/02/25 08:59:54 andreas Exp $
 */
public class TreeSiteManager extends AbstractSiteManager {

    private static final Category log = Category.getInstance(TreeSiteManager.class);

    /**
     * Ctor.
     * @param map The resource identity map.
     */
    public TreeSiteManager(DocumentIdentityMap map) {
        super(map);
    }

    /**
     * Returns the site tree for a certain area.
     * @param area The area.
     * @return A site tree.
     * @throws SiteException if an error occurs.
     */
    protected SiteTree getTree(String area) throws SiteException {
        SiteTree tree = getIdentityMap().getPublication().getSiteTree(area);
        return tree;
    }

    /**
     * Returns the parent of a document.
     * @param resource The resource.
     * @return A resource.
     * @throws SiteException if an error occurs.
     */
    protected Document getParent(Document resource) throws SiteException {
        Document parent = null;
        int lastSlashIndex = resource.getId().lastIndexOf("/");
        if (lastSlashIndex > 0) {
            String parentId = resource.getId().substring(0, lastSlashIndex);
            try {
                parent = getIdentityMap().get(parentId, resource.getArea());
            } catch (PublicationException e) {
                throw new SiteException(e);
            }
        }
        return parent;
    }

    /**
     * Returns the ancestors of a resource, beginning with the parent.
     * @param resource The resource.
     * @return A list of resources.
     * @throws SiteException if an error occurs.
     */
    protected List getAncestors(Document resource) throws SiteException {
        List ancestors = new ArrayList();
        Document parent = getParent(resource);
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
                resources[i] = getIdentityMap().get(descendant.getAbsoluteId(), resource.getArea());
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
        return node != null;
    }

}