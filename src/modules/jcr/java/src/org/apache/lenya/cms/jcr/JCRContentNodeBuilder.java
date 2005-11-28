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
import java.util.Arrays;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.ValueFormatException;

import org.apache.lenya.cms.jcr.util.Assertion;
import org.apache.lenya.cms.repo.DocumentType;
import org.apache.lenya.cms.repo.RepositoryException;

/**
 * JCR content node builder.
 */
public class JCRContentNodeBuilder extends AbstractNodeWrapperBuilder implements
        ResolvingNodeWrapperBuilder {

    protected static final String NODE_NAME = "lenya:contentNode";
    protected static final String NODE_TYPE = "lnt:contentNode";
    protected static final String ID_PROPERTY = "lenya:id";
    protected static final String DOCUMENT_TYPE_PROPERTY = "lenya:documentType";
    protected static final String VISIBLE_IN_NAV_PROPERTY = "lenya:visibleInNav";

    /**
     * Ctor.
     */
    public JCRContentNodeBuilder() {
    }

    public NodeWrapper addNode(JCRSession session, BuilderParameters parameters)
            throws RepositoryException {
        try {
            JCRContentNodeBuilderParameters params = (JCRContentNodeBuilderParameters) parameters;
            JCRContentNode jcrContentNode = null;
            Node content = params.getContent().getNode();
            Node contentNode = getNode(params, content);
            if (contentNode == null) {

                String names[] = session.getRepository()
                        .getDocumentTypeRegistry()
                        .getDocumentTypeNames();
                if (params.getDocumentType() == null) {
                    throw new RepositoryException("The document type must not be null.");
                } else if (!Arrays.asList(names).contains(params.getDocumentType())) {
                    throw new RepositoryException("The document type [" + params.getDocumentType()
                            + "] is not registered.");
                }

                contentNode = content.addNode(NODE_NAME, NODE_TYPE);
                contentNode.setProperty(ID_PROPERTY, params.getId());
                contentNode.setProperty(VISIBLE_IN_NAV_PROPERTY, true);
                
                if (params.getDocumentType() != null) {
                    contentNode.setProperty(DOCUMENT_TYPE_PROPERTY, params.getDocumentType());
                }
                
                jcrContentNode = new JCRContentNode(session, params.getContent(), contentNode);
            } else {
                throw new RepositoryException("The node already exists!");
            }

            return jcrContentNode;
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    protected Node getNode(JCRContentNodeBuilderParameters params, Node content)
            throws javax.jcr.RepositoryException, ValueFormatException, PathNotFoundException,
            RepositoryException {
        Node contentNode = null;
        for (NodeIterator nodes = content.getNodes(NODE_NAME); nodes.hasNext();) {
            Node node = nodes.nextNode();
            if (getKey(node).equals(params.getId())) {
                contentNode = node;
            }
        }
        return contentNode;
    }

    public NodeWrapper getNodeInternal(JCRSession session, BuilderParameters parameters)
            throws RepositoryException {
        try {
            JCRContentNodeBuilderParameters params = (JCRContentNodeBuilderParameters) parameters;
            JCRContentNode jcrContentNode = null;
            Node content = params.getContent().getNode();
            Node contentNode = getNode(params, content);
            if (contentNode != null) {
                jcrContentNode = new JCRContentNode(session, params.getContent(), contentNode);
            }
            return jcrContentNode;
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    public String getKey(Node node) throws RepositoryException {
        try {
            return node.getProperty(ID_PROPERTY).getString();
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * @param content The content.
     * @param id The id.
     * @param documentType The document type.
     * @return A parameters object.
     */
    public BuilderParameters createParameters(JCRContent content, String id,
            DocumentType documentType) {
        return new JCRContentNodeBuilderParameters(content, id, documentType);
    }

    /**
     * @param content The content.
     * @param id The id.
     * @return A parameters object.
     */
    public BuilderParameters createParameters(JCRContent content, String id) {
        return new JCRContentNodeBuilderParameters(content, id, null);
    }

    /**
     * Parameters.
     */
    public static class JCRContentNodeBuilderParameters implements BuilderParameters {

        private JCRContent content;
        private String id;
        private String documentType;

        /**
         * @param content The content.
         * @param id The node ID.
         * @param documentType The document type.
         */
        public JCRContentNodeBuilderParameters(JCRContent content, String id,
                DocumentType documentType) {

            Assertion.notNull(content, "JCR content");
            Assertion.notNull(id, "ID");

            this.content = content;
            this.id = id;
            if (documentType != null) {
                this.documentType = documentType.getName();
            }
        }

        /**
         * @return The content.
         */
        public JCRContent getContent() {
            return content;
        }

        /**
         * @return The node ID.
         */
        public String getId() {
            return id;
        }

        /**
         * @return The document type name.
         */
        public String getDocumentType() {
            return this.documentType;
        }

    }

    public String[] getKeys(JCRSession session, Node content) throws RepositoryException {
        try {
            List keys = new ArrayList();
            for (NodeIterator nodes = content.getNodes(NODE_NAME); nodes.hasNext();) {
                Node node = nodes.nextNode();
                String key = getKey(node);
                keys.add(key);
            }
            return (String[]) keys.toArray(new String[keys.size()]);
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

}
