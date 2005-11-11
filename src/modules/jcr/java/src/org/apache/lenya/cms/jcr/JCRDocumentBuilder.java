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
import javax.jcr.NodeIterator;

import org.apache.lenya.cms.repo.RepositoryException;

/**
 * JCR document builder.
 */
public class JCRDocumentBuilder implements NodeWrapperBuilder {

    protected static final String LANGUAGE_ATTRIBUTE = "xml:lang";
    protected static final String NODE_NAME = "lenya:document";
    protected static final String RESOURCE_NODE_NAME = "lenya:resource";

    private String language;
    private JCRContentNode contentNode;

    /**
     * Ctor.
     * @param contentNode The content node.
     * @param language The language.
     */
    public JCRDocumentBuilder(JCRContentNode contentNode, String language) {
        this.contentNode = contentNode;
        this.language = language;
    }

    public NodeWrapper buildNode(JCRSession session, boolean create) throws RepositoryException {
        try {
            JCRDocument document = null;
            Node contentNodeNode = this.contentNode.getNode();

            Node documentNode = null;
            for (NodeIterator i = contentNodeNode.getNodes(NODE_NAME); i.hasNext();) {
                Node node = i.nextNode();
                if (node.getProperty(LANGUAGE_ATTRIBUTE).getString().equals(this.language)) {
                    documentNode = node;
                }
            }

            if (documentNode == null && create) {
                documentNode = contentNodeNode.addNode(NODE_NAME);
                documentNode.setProperty(LANGUAGE_ATTRIBUTE, this.language);
                documentNode.addNode(RESOURCE_NODE_NAME);
            }

            if (documentNode != null) {
                document = new JCRDocument(documentNode);
            }
            return document;
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

}
