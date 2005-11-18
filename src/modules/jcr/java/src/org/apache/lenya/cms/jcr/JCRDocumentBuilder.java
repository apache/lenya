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
import java.util.GregorianCalendar;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

import org.apache.lenya.cms.repo.RepositoryException;

/**
 * JCR document builder.
 */
public class JCRDocumentBuilder extends AbstractNodeWrapperBuilder implements
        ResolvingNodeWrapperBuilder {

    protected static final String LANGUAGE_PROPERTY = "xml:lang";
    protected static final String NODE_NAME = "lenya:document";
    protected static final String NODE_TYPE = "lnt:document";
    protected static final String RESOURCE_NODE_NAME = "lenya:resource";
    protected static final String RESOURCE_NODE_TYPE = "lnt:resource";
    protected static final String META_NODE_NAME = "lenya:meta";
    protected static final String META_NODE_TYPE = "lnt:meta";
    protected static final String LABEL_PROPERTY = "lenya:label";

    /**
     * Parameters.
     */
    public static class JCRDocumentBuilderParameters implements BuilderParameters {

        private String language;
        private String label;
        private JCRContentNode contentNode;

        /**
         * Ctor.
         * @param contentNode The content node.
         * @param language The language.
         * @param label The label.
         */
        public JCRDocumentBuilderParameters(JCRContentNode contentNode, String language, String label) {
            this.contentNode = contentNode;
            this.label = label;
            this.language = language;
        }

        /**
         * @return The content node.
         */
        public JCRContentNode getContentNode() {
            return this.contentNode;
        }

        /**
         * @return The language.
         */
        public String getLanguage() {
            return this.language;
        }
        
        /**
         * @return The label.
         */
        public String getLabel() {
            return this.label;
        }

    }

    /**
     * @param contentNode The content node.
     * @param language The language.
     * @param label The label.
     * @return The parameters object.
     */
    public BuilderParameters createParameters(JCRContentNode contentNode, String language, String label) {
        return new JCRDocumentBuilderParameters(contentNode, language, label);
    }

    public NodeWrapper addNode(JCRSession session, BuilderParameters parameters)
            throws RepositoryException {
        try {

            JCRDocumentBuilderParameters params = (JCRDocumentBuilderParameters) parameters;

            JCRDocument document = null;
            Node contentNodeNode = params.getContentNode().getNode();

            Node documentNode = getNode(params, contentNodeNode);

            if (documentNode == null) {
                documentNode = contentNodeNode.addNode(NODE_NAME, NODE_TYPE);
                documentNode.setProperty(LANGUAGE_PROPERTY, params.getLanguage());
                documentNode.setProperty(LABEL_PROPERTY, "Label");
                
                Node resourceNode = documentNode.addNode(RESOURCE_NODE_NAME, RESOURCE_NODE_TYPE);
                resourceNode.setProperty("jcr:mimeType", params.getContentNode()
                        .getDocumentType()
                        .getMimeType());
                resourceNode.setProperty("jcr:data", "");
                resourceNode.setProperty("jcr:lastModified", new GregorianCalendar());
                
//                documentNode.addNode(META_NODE_NAME, META_NODE_TYPE);
            } else {
                throw new RepositoryException("The node already exists!");
            }

            if (documentNode != null) {
                document = new JCRDocument(documentNode);
            }
            return document;
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    protected Node getNode(JCRDocumentBuilderParameters params, Node contentNodeNode)
            throws javax.jcr.RepositoryException, RepositoryException {
        Node documentNode = null;
        for (NodeIterator i = contentNodeNode.getNodes(NODE_NAME); i.hasNext();) {
            Node node = i.nextNode();
            if (getKey(node).equals(params.getLanguage())) {
                documentNode = node;
            }
        }
        return documentNode;
    }

    protected NodeWrapper getNodeInternal(JCRSession session, BuilderParameters parameters)
            throws RepositoryException {
        try {

            JCRDocumentBuilderParameters params = (JCRDocumentBuilderParameters) parameters;

            JCRDocument document = null;
            Node contentNodeNode = params.getContentNode().getNode();
            Node documentNode = getNode(params, contentNodeNode);
            if (documentNode != null) {
                document = new JCRDocument(documentNode);
            }
            return document;
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    public String[] getKeys(JCRSession session, Node reference) throws RepositoryException {
        List keys = new ArrayList();
        try {
            for (NodeIterator i = reference.getNodes(NODE_NAME); i.hasNext();) {
                Node node = i.nextNode();
                keys.add(getKey(node));
            }
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
        return (String[]) keys.toArray(new String[keys.size()]);
    }

    public String getKey(Node node) throws RepositoryException {
        try {
            return node.getProperty(LANGUAGE_PROPERTY).getString();
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

}
