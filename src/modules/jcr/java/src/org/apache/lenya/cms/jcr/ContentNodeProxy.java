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

import org.apache.lenya.cms.jcr.mapping.AbstractNodeProxy;
import org.apache.lenya.cms.jcr.mapping.NamePathElement;
import org.apache.lenya.cms.jcr.mapping.Path;
import org.apache.lenya.cms.jcr.mapping.PathElement;
import org.apache.lenya.cms.repo.ContentNode;
import org.apache.lenya.cms.repo.Document;
import org.apache.lenya.cms.repo.DocumentType;
import org.apache.lenya.cms.repo.RepositoryException;

/**
 * Content node proxy.
 */
public class ContentNodeProxy extends AbstractNodeProxy implements ContentNode {

    protected static final String NODE_TYPE = "lnt:contentNode";
    protected static final String NODE_NAME = "lenya:contentNode";
    protected static final String ID_PROPERTY = "lenya:id";
    protected static final String DOCUMENT_TYPE_PROPERTY = "lenya:documentType";
    protected static final String VISIBLE_IN_NAV_PROPERTY = "lenya:visibleInNav";

    public Document[] getDocuments() throws RepositoryException {
        ContentProxy contentProxy = (ContentProxy) getParentProxy();
        Path path = contentProxy.getAbsolutePath()
                .append(new NamePathElement(DocumentProxy.NODE_NAME));
        return (Document[]) getRepository().getProxies(path);
    }

    public Document addDocument(String language, String label) throws RepositoryException {

        DocumentProxy proxy = (DocumentProxy) getRepository().addByProperty(getAbsolutePath(),
                DocumentProxy.NODE_TYPE,
                DocumentProxy.class.getName(),
                DocumentProxy.NODE_NAME,
                DocumentProxy.LANGUAGE_PROPERTY,
                language);
        proxy.setLabel(label);

        ResourceProxy resourceProxy = (ResourceProxy) getRepository().addByName(proxy.getAbsolutePath(),
                ResourceProxy.NODE_TYPE,
                ResourceProxy.class.getName(),
                ResourceProxy.NODE_NAME);
        resourceProxy.init();

        return proxy;
    }

    public void removeDocument(Document document) throws RepositoryException {
        // TODO Auto-generated method stub

    }

    public Document getDocument(String language) throws RepositoryException {
        Path path = DocumentProxy.getPath(this, language);
        return (Document) getRepository().getProxy(path);
    }

    public DocumentType getDocumentType() throws RepositoryException {
        String name = getPropertyString(DOCUMENT_TYPE_PROPERTY);
        return getRepository().getDocumentTypeRegistry().getDocumentType(name);
    }

    public String getNodeId() throws RepositoryException {
        try {
            return getNode().getProperty(ID_PROPERTY).getString();
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public Path getAbsolutePath() throws RepositoryException {
        return ContentNodeProxy.getPath((ContentProxy) getParentProxy(), getNodeId());
    }

    protected static Path getPath(ContentProxy contentProxy, String nodeId)
            throws RepositoryException {
        return contentProxy.getAbsolutePath()
                .append(getPathElement(NODE_NAME, ID_PROPERTY, nodeId));
    }

    public PathElement getPathElement() throws RepositoryException {
        return getPathElement(NODE_NAME, ID_PROPERTY, getNodeId());
    }

    public boolean isVisibleInNav() throws RepositoryException {
        return getPropertyBoolean(VISIBLE_IN_NAV_PROPERTY);
    }

    public void setVisibleInNav(boolean visible) throws RepositoryException {
        setProperty(VISIBLE_IN_NAV_PROPERTY, visible);
    }
    
    /**
     * @param documentType The document type's name.
     * @throws RepositoryException if an error occurs.
     */
    public void setDocumentType(String documentType) throws RepositoryException {
        setProperty(DOCUMENT_TYPE_PROPERTY, documentType);
    }

}
