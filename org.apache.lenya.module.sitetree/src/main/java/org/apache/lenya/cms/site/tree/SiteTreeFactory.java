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
package org.apache.lenya.cms.site.tree;

import org.apache.cocoon.util.AbstractLogEnabled;
import org.apache.commons.logging.Log;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.cms.repository.RepositoryItem;
import org.apache.lenya.cms.repository.RepositoryItemFactory;
import org.apache.lenya.cms.repository.Session;

/**
 * Factory for sitetree objects.
 * 
 * @version $Id: SiteTreeFactory.java 179568 2005-06-02 09:27:26Z jwkaltz $
 */
public class SiteTreeFactory extends AbstractLogEnabled implements RepositoryItemFactory {

    /**
     * Ctor.
     * @param logger The logger.
     */
    public SiteTreeFactory(Log logger) {
        setLogger(logger);
    }

    public RepositoryItem buildItem(Session session, String key) throws RepositoryException {
        String[] snippets = key.split(":");
        String publicationId = snippets[0];
        String area = snippets[1];
        DefaultSiteTree tree;
        try {
            org.apache.lenya.cms.publication.Session pubSession = (org.apache.lenya.cms.publication.Session) session;
            Publication publication = pubSession.getPublication(publicationId);
            tree = new DefaultSiteTree(publication.getArea(area));
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
        return tree;
    }

    public String getItemType() {
        return SiteTree.IDENTIFIABLE_TYPE;
    }

    public boolean isSharable() {
        return true;
    }

}