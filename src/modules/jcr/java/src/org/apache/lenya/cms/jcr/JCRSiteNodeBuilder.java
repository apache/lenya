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

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

import org.apache.lenya.cms.repo.RepositoryException;

/**
 * JCR site node builder.
 */
public class JCRSiteNodeBuilder extends AbstractNodeWrapperBuilder implements
        ResolvingNodeWrapperBuilder {

    private JCRSite site;

    /**
     * @return The site.
     */
    public JCRSite getSite() {
        return site;
    }

    /**
     * Ctor.
     * @param site The site.
     */
    public JCRSiteNodeBuilder(JCRSite site) {
        this.site = site;
    }

    /**
     * Parameters.
     */
    public static class JCRSiteNodeBuilderParameters implements BuilderParameters {

        private Node parent;
        private String name;
        private JCRContentNode contentNode;
        private JCRSiteNode parentSiteNode;

        /**
         * Ctor.
         * @param parent The parent JCR node.
         * @param name The name.
         * @param contentNode The referenced content node.
         * @param parentSiteNode The parent site node.
         */
        public JCRSiteNodeBuilderParameters(Node parent, String name, JCRContentNode contentNode,
                JCRSiteNode parentSiteNode) {
            this(parent, name, parentSiteNode);
            this.contentNode = contentNode;
        }

        /**
         * Ctor.
         * @param parent The parent JCR node.
         * @param name The name.
         * @param parentSiteNode The parent site node.
         */
        public JCRSiteNodeBuilderParameters(Node parent, String name, JCRSiteNode parentSiteNode) {
            this.parent = parent;
            this.name = name;
            this.parentSiteNode = parentSiteNode;
        }

        /**
         * @return The content node.
         */
        public JCRContentNode getContentNode() {
            return contentNode;
        }

        /**
         * @return The name.
         */
        public String getName() {
            return name;
        }

        /**
         * @return The parent.
         */
        public Node getParent() {
            return parent;
        }

        /**
         * @return The parent site node.
         */
        public JCRSiteNode getParentSiteNode() {
            return this.parentSiteNode;
        }
    }

    /**
     * @param parent The parent.
     * @param name The node name.
     * @param contentNode The conent node to reference.
     * @param parentSiteNode The parent site node.
     * @return A parameters object.
     */
    public BuilderParameters createParameters(Node parent, String name, JCRContentNode contentNode,
            JCRSiteNode parentSiteNode) {
        return new JCRSiteNodeBuilderParameters(parent, name, contentNode, parentSiteNode);
    }

    /**
     * @param parent The parent.
     * @param name The node name.
     * @param parentSiteNode The parent site node.
     * @return A parameters object.
     */
    public BuilderParameters createParameters(Node parent, String name, JCRSiteNode parentSiteNode) {
        return new JCRSiteNodeBuilderParameters(parent, name, parentSiteNode);
    }

    protected static final String CONTENT_NODE_PROPERTY = "lenya:contentNode";
    protected static final String NODE_TYPE = "lnt:siteNode";

    public NodeWrapper addNode(JCRSession session, BuilderParameters parameters)
            throws RepositoryException {
        try {
            JCRSiteNodeBuilderParameters params = (JCRSiteNodeBuilderParameters) parameters;
            JCRSiteNode wrapper = null;
            Node siteNode = null;
            if (params.getParent().hasNode(params.getName())) {
                throw new RepositoryException("The node already exists!");
            } else {

                if (params.getContentNode() == null) {
                    throw new RepositoryException("You must provide a content node when creating a new site node!");
                }

                siteNode = params.getParent().addNode(params.getName(), NODE_TYPE);
                siteNode.setProperty(CONTENT_NODE_PROPERTY, params.getContentNode().getNode());
                wrapper = new JCRSiteNode(session, siteNode, getSite(), params.getParentSiteNode());
            }

            return wrapper;
        } catch (RepositoryException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    public NodeWrapper getNodeInternal(JCRSession session, BuilderParameters parameters)
            throws RepositoryException {
        try {
            JCRSiteNodeBuilderParameters params = (JCRSiteNodeBuilderParameters) parameters;
            JCRSiteNode wrapper = null;
            Node siteNode = null;
            if (params.getParent().hasNode(params.getName())) {
                siteNode = params.getParent().getNode(params.getName());
                wrapper = new JCRSiteNode(session, siteNode, getSite(), params.getParentSiteNode());
            }
            return wrapper;
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public String[] getKeys(JCRSession session, Node parent) throws RepositoryException {
        try {
            NodeIterator i = parent.getNodes();
            List keys = new ArrayList();
            while (i.hasNext()) {
                Node child = i.nextNode();
                keys.add(getKey(child));
            }
            return (String[]) keys.toArray(new String[keys.size()]);
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    public String getKey(Node node) throws RepositoryException {
        try {
            return node.getName();
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

}
