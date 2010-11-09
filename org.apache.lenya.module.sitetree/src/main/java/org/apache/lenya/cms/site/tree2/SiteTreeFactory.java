/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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
package org.apache.lenya.cms.site.tree2;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lenya.cms.publication.Area;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.repository.NodeFactory;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.repository.RepositoryItem;
import org.apache.lenya.cms.repository.RepositoryItemFactory;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.repository.SessionHolder;
import org.apache.lenya.cms.repository.SharedItemStore;
import org.apache.lenya.cms.site.tree.SiteTree;

/**
 * Factory for sitetree objects.
 * 
 * @version $Id: SiteTreeFactory.java 179568 2005-06-02 09:27:26Z jwkaltz $
 */
public class SiteTreeFactory implements RepositoryItemFactory {

    private static final Log logger = LogFactory.getLog(SiteTreeFactory.class);

    private SharedItemStore sharedItemStore;
    private TreeBuilder treeBuilder;
    private TreeWriter treeWriter;
    private NodeFactory nodeFactory;

    public RepositoryItem buildItem(Session session, String key) throws RepositoryException {
        String[] snippets = key.split(":");
        String publicationId = snippets[0];
        String areaName = snippets[1];
        try {
            org.apache.lenya.cms.publication.Session pubSession = (org.apache.lenya.cms.publication.Session) session
                    .getHolder();
            Publication publication = pubSession.getPublication(publicationId);
            Area area = publication.getArea(areaName);

            Session storeSession = this.sharedItemStore.getSession();
            storeSession.setHolder((SessionHolder)pubSession);
            
            if (session.isModifiable() || session == storeSession) {
                SiteTreeImpl tree = new SiteTreeImpl(area);
                tree.setBuilder(this.treeBuilder);
                tree.setWriter(this.treeWriter);
                tree.setNodeFactory(this.nodeFactory);
                return tree;
            } else {
                return new DelegatingSiteTree(area, this, storeSession, key);
            }
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    public String getItemType() {
        return SiteTree.IDENTIFIABLE_TYPE;
    }

    public void setSharedItemStore(SharedItemStore sharedItemStore) {
        this.sharedItemStore = sharedItemStore;
    }

    public void setTreeBuilder(TreeBuilder treeBuilder) {
        this.treeBuilder = treeBuilder;
    }

    public void setTreeWriter(TreeWriter treeWriter) {
        this.treeWriter = treeWriter;
    }

    public void setNodeFactory(NodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

}