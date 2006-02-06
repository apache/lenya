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
package org.apache.lenya.cms.cocoon.components.modules.input;

import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.components.modules.input.AbstractInputModule;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.ResourceType;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.util.ServletHelper;

/**
 * <p>
 * Resource type module.
 * </p>
 * <p>
 * The syntax is either <code>{resource-type:&lt;attribute&gt;}</code> or
 * <code>{resource-type:&lt;name&gt;:&lt;attribute&gt;}</code>.
 * </p>
 */
public class ResourceTypeModule extends AbstractInputModule implements Serviceable {

    protected static final String SCHEMA_URI = "schemaUri";
    protected static final String HTTP_SCHEMA_URI = "httpSchemaUri";

    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {
        Object value = null;

        try {
            Request request = ObjectModelHelper.getRequest(objectModel);
            Session session = RepositoryUtil.getSession(request, getLogger());

            ResourceType resourceType;
            String attribute;

            String[] steps = name.split(":");
            if (steps.length == 1) {
                attribute = name;
                DocumentIdentityMap docFactory = new DocumentIdentityMap(session,
                        this.manager,
                        getLogger());
                String webappUrl = ServletHelper.getWebappURI(request);
                Document document = docFactory.getFromURL(webappUrl);
                resourceType = document.getResourceType();
            } else {
                attribute = steps[1];
                String resourceTypeName = steps[0];

                ServiceSelector selector = null;
                try {
                    selector = (ServiceSelector) this.manager.lookup(ResourceType.ROLE + "Selector");
                    resourceType = (ResourceType) selector.select(resourceTypeName);
                } finally {
                    this.manager.release(selector);
                }
            }

            if (attribute.startsWith("format-")) {
                String[] formatSteps = name.split("-");
                String format = formatSteps[1];
                value = resourceType.getFormatURI(format);
            } else if (attribute.equals(SCHEMA_URI)) {
                value = resourceType.getSchema().getURI();
            } else if (attribute.equals(HTTP_SCHEMA_URI)) {
                String uri = resourceType.getSchema().getURI();
                String path = uri.substring("fallback://".length());
                value = request.getContextPath() + "/fallback/" + path;
            } else {
                throw new ConfigurationException("Attribute [" + name + "] not supported!");
            }

        } catch (Exception e) {
            throw new ConfigurationException("Resolving attribute [" + name + "] failed: ", e);
        }

        return value;
    }

    protected ServiceManager manager;

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}
