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

import org.apache.lenya.cms.repo.ContentNode;
import org.apache.lenya.cms.repo.Document;
import org.apache.lenya.cms.repo.DocumentType;
import org.apache.lenya.cms.repo.Repository;
import org.apache.lenya.cms.repo.RepositoryException;

/**
 * JCR content node.
 */
public class JCRContentNode extends NodeWrapper implements ContentNode {

    private NodeWrapperManager documentManager;
    private JCRDocumentBuilder builder = new JCRDocumentBuilder();
    private JCRContent content;
    private JCRSession session;

    /**
     * Ctor.
     * @param session The session.
     * @param content The content object.
     * @param node The JCR node.
     */
    public JCRContentNode(JCRSession session, JCRContent content, Node node) {
        super(node);
        this.session = session;
        this.content = content;
        this.documentManager = new NodeWrapperManager(session, this.builder);
    }

    protected JCRContent getContent() {
        return this.content;
    }

    public Document[] getDocuments() throws RepositoryException {
        try {
            List documents = new ArrayList();
            for (NodeIterator i = getNode().getNodes(JCRDocumentBuilder.NODE_NAME); i.hasNext();) {
                Node node = i.nextNode();
                documents.add(getDocument(node.getProperty(JCRDocumentBuilder.LANGUAGE_PROPERTY)
                        .getString()));
            }
            return (Document[]) documents.toArray(new Document[documents.size()]);
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }

    }

    public Document addDocument(String language, String label) throws RepositoryException {
        BuilderParameters params = builder.createParameters(this, language, label);
        return (Document) this.documentManager.getNode(language, params, true);
    }

    public void removeDocument(Document document) throws RepositoryException {
        try {
            ((JCRDocument) document).getNode().remove();
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public Document getDocument(String language) throws RepositoryException {
        BuilderParameters params = builder.createParameters(this, language, null);
        return (Document) this.documentManager.getNode(language, params, false);
    }

    public DocumentType getDocumentType() throws RepositoryException {
        Repository repo = this.session.getRepository();
        String doctypeName;
        try {
            doctypeName = getNode().getProperty(JCRContentNodeBuilder.DOCUMENT_TYPE_PROPERTY)
                    .getString();
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
        return repo.getDocumentTypeRegistry().getDocumentType(doctypeName);
    }

}
