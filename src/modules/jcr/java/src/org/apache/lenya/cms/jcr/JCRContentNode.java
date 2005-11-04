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
import org.apache.lenya.cms.repo.RepositoryException;
import org.apache.lenya.cms.repo.SiteNode;

/**
 * JCR content node.
 */
public class JCRContentNode extends NodeWrapper implements ContentNode {

    private NodeWrapperManager documentManager;
    private JCRContent content;
    
    public JCRContentNode(JCRContent content, Node node) {
        super(node);
        this.content = content;
        this.documentManager = new NodeWrapperManager(content.getArea().getPublication().getSession());
    }
    
    public Document[] getDocuments() throws RepositoryException {
        return null;
    }

    public void addDocument(String language) throws RepositoryException {
        // TODO Auto-generated method stub
        
    }

    public void removeDocument(Document document) throws RepositoryException {
        // TODO Auto-generated method stub
        
    }

    public Document getDocument(String language) throws RepositoryException {
        // TODO Auto-generated method stub
        return null;
    }
    
}
