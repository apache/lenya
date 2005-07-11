/*
 * Copyright  1999-2005 The Apache Software Foundation
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
package org.apache.lenya.cms.publication;

import java.util.ArrayList;
import java.util.List;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.lenya.cms.authoring.DefaultBranchCreator;
import org.apache.lenya.cms.authoring.NodeCreatorInterface;

/**
 * Resource type, implemented as a publet.
 * 
 * @version $Id:$
 */
public class ResourceTypeImpl extends AbstractLogEnabled implements Configurable, Serviceable,
        ThreadSafe, ResourceType {

    protected static final String SCHEMA_ELEMENT = "schema";
    protected static final String CREATOR_ELEMENT = "creator";
    protected static final String SRC_ATTRIBUTE = "src";
    protected static final String ELEMENT_REWRITE_ATTRIBUTE = "link-attribute";
    protected static final String ATTRIBUTE_XPATH = "xpath";
    protected static final String SAMPLE_NAME = "sample-name";

    private String schemaUri = null;
    private String sampleUri = null;
    private String[] linkAttributeXPaths;
    private NodeCreatorInterface creator;

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration config) throws ConfigurationException {

        try {

            Configuration schemaConf = config.getChild(SCHEMA_ELEMENT, false);

            if (schemaConf != null) {
                this.schemaUri = schemaConf.getAttribute(SRC_ATTRIBUTE);
            }

            Configuration creatorConf = config.getChild(CREATOR_ELEMENT, false);

            if (creatorConf != null) {
                String creatorClassName = creatorConf.getAttribute(SRC_ATTRIBUTE);
                Class creatorClass = Class.forName(creatorClassName);
                this.creator = (NodeCreatorInterface) creatorClass.newInstance();
                this.creator.init(creatorConf, manager, getLogger());
            } else {
                creator = new DefaultBranchCreator();
            }

            // determine the sample content location.
            if (creatorConf != null) {
                Configuration sampleConf = creatorConf.getChild(SAMPLE_NAME, false);
                if (sampleConf != null) {
                    this.sampleUri = sampleConf.getValue();
                }
            }

            Configuration[] rewriteAttributeConfigs = config.getChildren(ELEMENT_REWRITE_ATTRIBUTE);
            List xPaths = new ArrayList();
            for (int i = 0; i < rewriteAttributeConfigs.length; i++) {
                String xPath = rewriteAttributeConfigs[i].getAttribute(ATTRIBUTE_XPATH);
                xPaths.add(xPath);
            }
            this.linkAttributeXPaths = (String[]) xPaths.toArray(new String[xPaths.size()]);
        } catch (Exception e) {
            throw new ConfigurationException("Configuring resource type failed: ", e);
        }

    }

    private ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    public String getSchemaDefinitionSourceURI() {
        return this.schemaUri;
    }

    public String[] getLinkAttributeXPaths() {
        return this.linkAttributeXPaths;
    }

    public String getSampleURI() {
        return this.sampleUri;
    }

    public NodeCreatorInterface getCreator() {
        return this.creator;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public String getName() {
        return this.name;
    }

}
