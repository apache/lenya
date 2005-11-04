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
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.lenya.cms.repo.Area;
import org.apache.lenya.cms.repo.Content;
import org.apache.lenya.cms.repo.Site;

/**
 * JCR area.
 */
public class JCRArea implements Area {

    private String area;
    private JCRPublication publication;

    private NodeWrapperManager nodeManager;

    /**
     * Ctor.
     * @param publication The publication.
     * @param area The area ID.
     */
    public JCRArea(JCRPublication publication, String area) {
        this.publication = publication;
        this.area = area;
        this.nodeManager = new NodeWrapperManager(publication.getSession());
    }

    protected JCRPublication getPublication() {
        return this.publication;
    }

    protected String getArea() {
        return this.area;
    }

    protected Session getSession() throws RepositoryException {
        return getPublication().getSession().getSession(getArea());
    }

    protected JCRPublicationNode getPublicationNode()
            throws org.apache.lenya.cms.repo.RepositoryException {
        JCRPublicationNodeBuilder builder = new JCRPublicationNodeBuilder(getPublication().getPublicationId(),
                getArea());
        return (JCRPublicationNode) this.nodeManager.getNode(JCRPublicationNodeBuilder.NODE_NAME,
                builder,
                true);
    }

    protected Node getSubNode(Node parent, String childName) throws RepositoryException {
        Node child = null;
        if (parent.hasNode(childName)) {
            child = parent.getNode(childName);
        } else {
            child = parent.addNode(childName);
        }
        return child;
    }

    public Content getContent() throws org.apache.lenya.cms.repo.RepositoryException {
        NodeWrapperBuilder builder = new JCRContentBuilder(this);
        return (Content) this.nodeManager.getNode(JCRContentBuilder.NODE_NAME, builder, true);
    }

    public Site getSite() throws org.apache.lenya.cms.repo.RepositoryException {
        NodeWrapperBuilder builder = new JCRSiteBuilder(this);
        return (Site) this.nodeManager.getNode(JCRSiteBuilder.NODE_NAME, builder, true);
    }

    public void clear() throws org.apache.lenya.cms.repo.RepositoryException {
        // TODO Auto-generated method stub

    }

}
