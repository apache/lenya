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
 * JCR site builder.
 */
public class JCRSiteBuilder extends AbstractNodeWrapperBuilder {

    /**
     * Parameters.
     */
    public static class JCRSiteBuilderParameters implements BuilderParameters {

        private JCRPublicationNode area;

        /**
         * Ctor.
         * @param area The area.
         */
        public JCRSiteBuilderParameters(JCRPublicationNode area) {
            this.area = area;
        }

        /**
         * @return The area.
         */
        public JCRPublicationNode getArea() {
            return this.area;
        }
    }

    /**
     * @param area The area.
     * @return A parameters object.
     */
    public BuilderParameters createParameters(JCRPublicationNode area) {
        return new JCRSiteBuilderParameters(area);
    }

    protected static final String NODE_NAME = "lenya:site";
    protected static final String NODE_TYPE = "lnt:site";

    public NodeWrapper addNode(JCRSession session, BuilderParameters parameters) throws RepositoryException {
        try {
            JCRSiteBuilderParameters params = (JCRSiteBuilderParameters) parameters;
            Node pubNode = params.getArea().getNode();
            JCRSite wrapper = null;
            Node siteNode = null;
            if (pubNode.hasNode(NODE_NAME)) {
                throw new RepositoryException("The node already exists!");
            } else {
                siteNode = pubNode.addNode(NODE_NAME, NODE_TYPE);
                wrapper = new JCRSite(session, siteNode, params.getArea());
            }
            return wrapper;
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    public NodeWrapper getNodeInternal(JCRSession session, BuilderParameters parameters) throws RepositoryException {
        try {
            JCRSiteBuilderParameters params = (JCRSiteBuilderParameters) parameters;
            Node pubNode = params.getArea().getNode();
            JCRSite wrapper = null;
            Node siteNode = null;
            if (pubNode.hasNode(NODE_NAME)) {
                siteNode = pubNode.getNode(NODE_NAME);
                wrapper = new JCRSite(session, siteNode, params.getArea());
            }
            return wrapper;
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

}
