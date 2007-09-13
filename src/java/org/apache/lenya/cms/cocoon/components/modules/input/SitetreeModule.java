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

/* $Id: SitetreeModule.java 473841 2006-11-12 00:46:38Z gregor $  */

package org.apache.lenya.cms.cocoon.components.modules.input;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.cocoon.components.modules.input.AbstractInputModule;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.SiteTree;
import org.apache.lenya.cms.publication.SiteTreeNode;

public class SitetreeModule extends AbstractInputModule {

    public static final String AUTHORING_NODE = "authoring-node";
    public static final String LIVE_NODE = "live-node";
    public static final String TRASH_NODE = "trash-node";
    public static final String ARCHIVE_NODE = "archive-node";
    public static final String FIRST_CHILD_ID = "first-child-id";
    public static final String LABEL_HREF = "label-href";

    protected static final String[] PARAMETER_NAMES = { AUTHORING_NODE, LIVE_NODE, TRASH_NODE, ARCHIVE_NODE, FIRST_CHILD_ID, LABEL_HREF };

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttribute(java.lang.String, org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
        throws ConfigurationException {

        Object value = null;

        try {
            PageEnvelope envelope = PageEnvelopeFactory.getInstance().getPageEnvelope(objectModel);
            Publication publication = envelope.getPublication();

            if (name.equals(AUTHORING_NODE)) {
                SiteTree authoringTree = publication.getTree(Publication.AUTHORING_AREA);
                value = authoringTree.getNode(envelope.getDocument().getId());
            }

            if (name.equals(LIVE_NODE)) {
                SiteTree liveTree = publication.getTree(Publication.LIVE_AREA);
                value = liveTree.getNode(envelope.getDocument().getId());
            }

            if (name.equals(TRASH_NODE)) {
                SiteTree trashTree = publication.getTree(Publication.TRASH_AREA);
                value = trashTree.getNode(envelope.getDocument().getId());
            }
            
            if (name.equals(ARCHIVE_NODE)) {
                SiteTree archiveTree = publication.getTree(Publication.ARCHIVE_AREA);
                value = archiveTree.getNode(envelope.getDocument().getId());
            }
            
            if (name.equals(FIRST_CHILD_ID)) {
                SiteTree siteTree = publication.getTree(envelope.getDocument().getArea());
                SiteTreeNode node = siteTree.getNode(envelope.getDocument().getId()); 
                SiteTreeNode[] children = node.getChildren(envelope.getDocument().getLanguage());
                if (children.length > 0){
                    value = children[0].getId();
                } else {
                    value = null;   
                }
            }
            
            if (name.equals(LABEL_HREF)) {
                Document document = envelope.getDocument();
                SiteTree siteTree = publication.getTree(document.getArea());
                value = siteTree.getNode(document.getId()).getLabel(document.getLanguage()).getHref();
                if (value == null) value = "";
            }

        } catch (Exception e) {
            throw new ConfigurationException("Obtaining value for [" + name + "] failed: ", e);
        }

        return value;
    }

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeNames(org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Iterator getAttributeNames(Configuration modeConf, Map objectModel)
        throws ConfigurationException {
        return Arrays.asList(PARAMETER_NAMES).iterator();
    }

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeValues(java.lang.String, org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Object[] getAttributeValues(String name, Configuration modeConf, Map objectModel)
        throws ConfigurationException {
        Object[] objects = { getAttribute(name, modeConf, objectModel) };
        return objects;
    }

}
