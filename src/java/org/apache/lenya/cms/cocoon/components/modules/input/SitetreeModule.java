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

package org.apache.lenya.cms.cocoon.components.modules.input;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.site.tree.SiteTree;
import org.apache.lenya.cms.site.tree.TreeSiteManager;

/**
 * Module for sitetree access.
 * 
 * @version $Id$
 */
public class SitetreeModule extends AbstractPageEnvelopeModule {

    public static final String AUTHORING_NODE = "authoring-node";
    public static final String LIVE_NODE = "live-node";
    public static final String TRASH_NODE = "trash-node";
    public static final String ARCHIVE_NODE = "archive-node";
    
    protected static final String[] PARAMETER_NAMES = { AUTHORING_NODE, LIVE_NODE, TRASH_NODE, ARCHIVE_NODE };

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttribute(java.lang.String,
     *      org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {

        Object value = null;

        try {
            PageEnvelope envelope = getEnvelope(objectModel);
            Publication publication = envelope.getPublication();
            DocumentIdentityMap map = envelope.getIdentityMap();
            TreeSiteManager manager = (TreeSiteManager) publication.getSiteManager(map);

            if (name.equals(AUTHORING_NODE)) {
                SiteTree authoringTree = manager.getTree(Publication.AUTHORING_AREA);
                value = authoringTree.getNode(envelope.getDocument().getId());
            }

            if (name.equals(LIVE_NODE)) {
                SiteTree liveTree = manager.getTree(Publication.LIVE_AREA);
                value = liveTree.getNode(envelope.getDocument().getId());
            }
            
            if (name.equals(TRASH_NODE)) {
                SiteTree trashTree = manager.getTree(Publication.TRASH_AREA);
                value = trashTree.getNode(envelope.getDocument().getId());
            }
            
            if (name.equals(ARCHIVE_NODE)) {
                SiteTree archiveTree = manager.getTree(Publication.ARCHIVE_AREA);
                value = archiveTree.getNode(envelope.getDocument().getId());
            }
        } catch (Exception e) {
            throw new ConfigurationException("Obtaining value for [" + name + "] failed: ", e);
        }

        return value;
    }

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeNames(org.apache.avalon.framework.configuration.Configuration,
     *      java.util.Map)
     */
    public Iterator getAttributeNames(Configuration modeConf, Map objectModel)
            throws ConfigurationException {
        return Arrays.asList(PARAMETER_NAMES).iterator();
    }

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeValues(java.lang.String,
     *      org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Object[] getAttributeValues(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {
        Object[] objects = { getAttribute(name, modeConf, objectModel) };
        return objects;
    }

}