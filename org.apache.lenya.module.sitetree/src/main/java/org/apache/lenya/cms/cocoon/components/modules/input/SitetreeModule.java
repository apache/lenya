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

package org.apache.lenya.cms.cocoon.components.modules.input;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.site.SiteStructure;

/**
 * Module for sitetree access.
 * 
 * @version $Id: SitetreeModule.java 159584 2005-03-31 12:49:41Z andreas $
 */
public class SitetreeModule extends AbstractPageEnvelopeModule {

    /**
     * <code>AUTHORING_NODE</code> The authoring node
     */
    public static final String AUTHORING_NODE = "authoring-node";
    /**
     * <code>LIVE_NODE</code> The live node
     */
    public static final String LIVE_NODE = "live-node";
    /**
     * <code>TRASH_NODE</code> The trash node
     */
    public static final String TRASH_NODE = "trash-node";
    /**
     * <code>ARCHIVE_NODE</code> The archive node
     */
    public static final String ARCHIVE_NODE = "archive-node";

    protected static final String[] PARAMETER_NAMES = { AUTHORING_NODE, LIVE_NODE, TRASH_NODE,
            ARCHIVE_NODE };

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttribute(java.lang.String,
     *      org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {

        Object value = null;

        try {
            PageEnvelope envelope = getEnvelope(objectModel, name);
            Publication pub = envelope.getPublication();

            SiteStructure site = null;
            if (name.equals(AUTHORING_NODE)) {
                site = pub.getArea(Publication.AUTHORING_AREA).getSite();
            }

            if (name.equals(LIVE_NODE)) {
                site = pub.getArea(Publication.LIVE_AREA).getSite();;
            }

            if (name.equals(TRASH_NODE)) {
                site = pub.getArea(Publication.TRASH_AREA).getSite();;
            }

            if (name.equals(ARCHIVE_NODE)) {
                site = pub.getArea(Publication.ARCHIVE_AREA).getSite();;
            }
            if (site != null) {
                value = site.getNode(envelope.getDocument().getPath());
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