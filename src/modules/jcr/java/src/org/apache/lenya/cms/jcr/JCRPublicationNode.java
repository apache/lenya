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

import org.apache.lenya.cms.repo.Area;
import org.apache.lenya.cms.repo.Content;
import org.apache.lenya.cms.repo.RepositoryException;
import org.apache.lenya.cms.repo.Site;

/**
 * Publication node.
 */
public class JCRPublicationNode extends NodeWrapper implements Area {

    /**
     * Ctor.
     * @param session 
     * @param node The JCR node.
     */
    public JCRPublicationNode(JCRSession session, Node node) {
        super(node);
        this.contentManager = new NodeWrapperManager(session, this.contentBuilder);
        this.siteManager = new NodeWrapperManager(session, this.siteBuilder);
    }

    private NodeWrapperManager contentManager;
    private NodeWrapperManager siteManager;

    private JCRContentBuilder contentBuilder = new JCRContentBuilder();
    private JCRSiteBuilder siteBuilder = new JCRSiteBuilder();

    protected String getPublicationId() throws RepositoryException {
        try {
            return getNode().getProperty(JCRPublicationNodeBuilder.ID_ATTRIBUTE).getString();
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    protected String getArea() throws RepositoryException {
        try {
            return getNode().getSession().getWorkspace().getName();
        } catch (javax.jcr.RepositoryException e) {
            throw new RepositoryException(e);
        }
    }

    public Content getContent() throws org.apache.lenya.cms.repo.RepositoryException {
        BuilderParameters params = this.contentBuilder.createParameters(this);
        return (Content) this.contentManager.getNode(JCRContentBuilder.NODE_NAME, params, true);
    }

    public Site getSite() throws org.apache.lenya.cms.repo.RepositoryException {
        BuilderParameters params = this.siteBuilder.createParameters(this);
        return (Site) this.siteManager.getNode(JCRSiteBuilder.NODE_NAME, params, true);
    }

    public void clear() throws org.apache.lenya.cms.repo.RepositoryException {
        // TODO Auto-generated method stub

    }
    
    public String toString() {
        try {
            return getArea();
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

}
