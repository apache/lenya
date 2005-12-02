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
import org.apache.lenya.cms.jcr.mapping.Path;
import org.apache.lenya.cms.jcr.mapping.PathElement;
import org.apache.lenya.cms.repo.Area;
import org.apache.lenya.cms.repo.Content;
import org.apache.lenya.cms.repo.ContentNode;
import org.apache.lenya.cms.repo.DocumentType;
import org.apache.lenya.cms.repo.RepositoryException;

/**
 * Content proxy.
 */
public class ContentProxy extends AbstractNodeProxy implements Content {

    protected static final String NODE_NAME = "lenya:content";
    protected static final String NODE_TYPE = "lnt:content";

    public ContentNode getNode(String id) throws RepositoryException {
        Path path = getAbsolutePath().append(getPathElement(ContentNodeProxy.NODE_NAME,
                ContentNodeProxy.ID_PROPERTY,
                id));
        return (ContentNode) getRepository().getProxy(path);
    }

    public ContentNode[] getNodes() throws RepositoryException {
        Path path = getAbsolutePath().append(getPathElement(ContentNodeProxy.NODE_NAME));
        return (ContentNode[]) getRepository().getProxies(path);
    }

    public ContentNode addNode(DocumentType documentType) throws RepositoryException {
        ContentNodeProxy proxy = (ContentNodeProxy) getRepository().addByNameWithoutCheck(getAbsolutePath(),
                ContentNodeProxy.NODE_TYPE,
                ContentNodeProxy.class.getName(),
                ContentNodeProxy.NODE_NAME);
        proxy.setVisibleInNav(true);
        proxy.setDocumentType(documentType.getName());
        return proxy;
    }

    public Path getAbsolutePath() throws RepositoryException {
        AreaProxy area = (AreaProxy) getArea();
        return area.getAbsolutePath().append(getPathElement());
    }

    public Area getArea() throws RepositoryException {
        return (Area) getParentProxy();
    }

    public PathElement getPathElement() throws RepositoryException {
        return getPathElement(NODE_NAME);
    }

}
