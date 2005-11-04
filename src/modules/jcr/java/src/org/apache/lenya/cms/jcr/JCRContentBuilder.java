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
 * JCR content builder.
 */
public class JCRContentBuilder implements NodeWrapperBuilder {
    
    private JCRArea area;
    
    /**
     * Ctor.
     * @param area The area.
     */
    public JCRContentBuilder(JCRArea area) {
        this.area = area;
    }

    protected static final String NODE_NAME = "lenya:content";

    public NodeWrapper buildNode(JCRSession session, boolean create) throws RepositoryException {
        try {
            
            Node pubNode = this.area.getPublicationNode().getNode();
            JCRContent wrapper = null;
            Node contentNode;
            if (pubNode.hasNode(NODE_NAME)) {
                contentNode = pubNode.getNode(NODE_NAME);
                wrapper = new JCRContent(contentNode, area);
            }
            else if (create) {
                contentNode = pubNode.addNode(NODE_NAME);
                wrapper = new JCRContent(contentNode, area);
            }
            
            return wrapper;
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

}
