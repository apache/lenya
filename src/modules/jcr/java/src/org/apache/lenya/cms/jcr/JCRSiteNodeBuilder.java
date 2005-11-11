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
package org.apache.lenya.cms.jcr;

import javax.jcr.Node;

import org.apache.lenya.cms.repo.RepositoryException;

/**
 * JCR site node builder.
 */
public class JCRSiteNodeBuilder implements NodeWrapperBuilder {

    private Node parent;
    private String name;
    private JCRContentNode contentNode;
    private JCRSite site;

    /**
     * Ctor.
     * @param site The site.
     * @param parent The parent JCR node.
     * @param name The name.
     * @param contentNode The referenced content node.
     */
    public JCRSiteNodeBuilder(JCRSite site, Node parent, String name, JCRContentNode contentNode) {
        this(site, parent, name);
        this.contentNode = contentNode;
    }

    /**
     * Ctor.
     * @param site The site.
     * @param parent The parent JCR node.
     * @param name The name.
     */
    public JCRSiteNodeBuilder(JCRSite site, Node parent, String name) {
        this.site = site;
        this.parent = parent;
        this.name = name;
    }

    protected static final String CONTENT_NODE_PROPERTY = "lenya:contentnode";

    public NodeWrapper buildNode(JCRSession session, boolean create) throws RepositoryException {
        try {
            JCRSiteNode wrapper = null;
            Node siteNode = null;
            if (parent.hasNode(this.name)) {
                siteNode = parent.getNode(this.name);
            } else if (create) {

                if (this.contentNode == null) {
                    throw new RepositoryException("You must provide a content node when creating a new site node!");
                }

                siteNode = parent.addNode(this.name);
                siteNode.setProperty(CONTENT_NODE_PROPERTY, this.contentNode.getNode());
            }
            if (siteNode != null) {
                wrapper = new JCRSiteNode(siteNode, this.site);
            }

            return wrapper;
        } catch (RepositoryException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

}
