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

import java.util.HashSet;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.ValueFormatException;

import org.apache.jackrabbit.core.WorkspaceImpl;
import org.apache.lenya.cms.repo.RepositoryException;

/**
 * JCR publication node builder.
 */
public class JCRPublicationNodeBuilder extends AbstractNodeWrapperBuilder implements
        ResolvingNodeWrapperBuilder {

    private JCRSession session;

    /**
     * Ctor.
     * @param session The JCR session.
     */
    public JCRPublicationNodeBuilder(JCRSession session) {
        this.session = session;
    }

    /**
     * Parameters.
     */
    public static class JCRPublicationNodeBuilderParameters implements BuilderParameters {

        /**
         * Ctor.
         * @param pubId The publication ID.
         * @param area The area.
         */
        public JCRPublicationNodeBuilderParameters(String pubId, String area) {
            this.pubId = pubId;
            this.area = area;
        }

        private String pubId;
        private String area;

        /**
         * @return The publication ID.
         */
        public String getPublicationId() {
            return this.pubId;
        }

        /**
         * @return The area.
         */
        public String getArea() {
            return this.area;
        }

    }

    /**
     * @param pubId The publication ID.
     * @param area The area.
     * @return A parameters object.
     */
    public BuilderParameters createParameters(String pubId, String area) {
        return new JCRPublicationNodeBuilderParameters(pubId, area);
    }

    protected static final String NODE_NAME = "lenya:publication";
    protected static final String NODE_TYPE = "lnt:publication";
    protected static final String ID_ATTRIBUTE = "lenya:id";

    public NodeWrapper addNode(JCRSession session, BuilderParameters parameters)
            throws RepositoryException {
        try {
            JCRPublicationNodeBuilderParameters params = (JCRPublicationNodeBuilderParameters) parameters;

            JCRPublicationNode wrapper = null;
            Node rootNode = session.getSession(params.getArea()).getRootNode();

            Node pubNode = getNode(params, rootNode);
            if (pubNode != null) {
                throw new RepositoryException("The node already exists!");
            }
            pubNode = rootNode.addNode(NODE_NAME, NODE_TYPE);
            pubNode.setProperty(ID_ATTRIBUTE, params.getPublicationId());
            wrapper = new JCRPublicationNode(session, pubNode);

            return wrapper;
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    protected Node getNode(JCRPublicationNodeBuilderParameters params, Node rootNode)
            throws javax.jcr.RepositoryException, ValueFormatException, PathNotFoundException {
        Node pubNode = null;
        for (NodeIterator pubNodes = rootNode.getNodes(NODE_NAME); pubNodes.hasNext();) {
            Node node = pubNodes.nextNode();
            if (node.getProperty(ID_ATTRIBUTE).getString().equals(params.getPublicationId())) {
                pubNode = node;
            }
        }
        return pubNode;
    }

    public NodeWrapper getNodeInternal(JCRSession session, BuilderParameters parameters)
            throws RepositoryException {
        try {
            JCRPublicationNodeBuilderParameters params = (JCRPublicationNodeBuilderParameters) parameters;
            JCRPublicationNode wrapper = null;
            Node rootNode = session.getSession(params.getArea()).getRootNode();
            Node pubNode = getNode(params, rootNode);
            if (pubNode != null) {
                wrapper = new JCRPublicationNode(session, pubNode);
            }
            return wrapper;
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    public String[] getKeys(JCRSession session, Node reference) throws RepositoryException {

        Set keys = new HashSet();

        WorkspaceImpl defaultWorkspace = getDefaultWorkspace();
        try {
            String[] workspaces = defaultWorkspace.getAccessibleWorkspaceNames();
            for (int i = 0; i < workspaces.length; i++) {
                Session areaSession = this.session.getRepository()
                        .getRepository()
                        .login(new SimpleCredentials("john", "".toCharArray()));
                for (NodeIterator pubNodes = areaSession.getRootNode().getNodes(NODE_NAME); pubNodes.hasNext();) {
                    Node node = pubNodes.nextNode();
                    keys.add(node.getProperty(ID_ATTRIBUTE).getString() + ":" + workspaces[i]);
                }
            }
            return (String[]) keys.toArray(new String[keys.size()]);
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public String getKey(Node node) throws RepositoryException {
        try {
            return node.getProperty(ID_ATTRIBUTE) + ":"
                    + node.getSession().getWorkspace().getName();
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    protected WorkspaceImpl getDefaultWorkspace() throws RepositoryException {
        try {
            Session defaultWorkspaceSession = this.session.getRepository()
                    .getRepository()
                    .login(new SimpleCredentials("john", "".toCharArray()));
            WorkspaceImpl defaultWorkspace = (WorkspaceImpl) defaultWorkspaceSession.getWorkspace();
            return defaultWorkspace;
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

}
