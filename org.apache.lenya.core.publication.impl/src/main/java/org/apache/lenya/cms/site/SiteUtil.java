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
package org.apache.lenya.cms.site;

import java.util.HashSet;
import java.util.Set;

import org.apache.cocoon.spring.configurator.WebAppContextUtils;
import org.apache.lenya.cms.publication.DocumentLocator;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.Session;

/**
 * Utility to handle site structures.
 * 
 * @version $Id$
 */
public class SiteUtil {

    private SiteUtil() {
    }

    /**
     * Returns a sub-site starting with a certain node, which includes the node itself and all nodes
     * which require this node, in preorder.
     * 
     * @param node The top-level document.
     * @return A document set.
     * @throws SiteException if an error occurs.
     */
    public static NodeSet getSubSite(SiteNode node) throws SiteException {
        SiteManager siteManager = null;
        SiteNode[] subsite;
        try {
            String hint = node.getStructure().getPublication().getSiteManagerHint();
            siteManager = (SiteManager) WebAppContextUtils.getCurrentWebApplicationContext()
                    .getBean(SiteManager.class.getName() + "/" + hint);

            Set nodes = new HashSet();
            nodes.add(node);

            SiteNode[] requiringNodes = siteManager.getRequiringResources(node);
            for (int i = 0; i < requiringNodes.length; i++) {
                nodes.add(requiringNodes[i]);
            }

            subsite = (SiteNode[]) nodes.toArray(new SiteNode[nodes.size()]);
        } catch (Exception e) {
            throw new SiteException(e);
        }
        return new NodeSet(subsite);
    }

    /**
     * @see org.apache.lenya.cms.site.SiteManager#getAvailableLocator(DocumentFactory,
     *      DocumentLocator)
     * @param factory The factory.
     * @param locator The locator.
     * @return A document.
     * @throws SiteException if an error occurs.
     */
    public static DocumentLocator getAvailableLocator(Session session,
            DocumentLocator locator) throws SiteException {
        SiteManager siteManager = null;
        try {
            Publication pub = session.getPublication(locator.getPublicationId());
            siteManager = (SiteManager) WebAppContextUtils.getCurrentWebApplicationContext()
                    .getBean(SiteManager.ROLE + "/" + pub.getSiteManagerHint());
            return siteManager.getAvailableLocator(session, locator);
        } catch (Exception e) {
            throw new SiteException(e);
        }
    }

}