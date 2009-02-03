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
import java.util.Map;
import java.util.Date;
import java.text.SimpleDateFormat;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.components.modules.input.AbstractInputModule;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.commons.lang.StringUtils;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.ResourceType;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.util.ServletHelper;

/**
 * <p>
 * Resource type module.
 * </p>
 * <p>
 * The syntax is either <code>{resource-type:&lt;attribute&gt;}</code> (which uses the resource
 * type of the currenlty requested document) or
 * <code>{resource-type:&lt;name&gt;:&lt;attribute&gt;}</code> (which allows to access an
 * arbitrary resource type).
 * </p>
 * <p>
 * Attributes:
 * </p>
 * <ul>
 * <li><strong><code>expires</code></strong> - the expiration date in RFC 822/1123 format, see
 * {@link org.apache.lenya.cms.publication.ResourceType#getExpires()}</li>
 * <li><strong><code>schemaUri</code></strong> - see
 * {@link org.apache.lenya.xml.Schema#getURI()}</li>
 * <li><strong><code>httpSchemaUri</code></strong> - the URI to request the schema over HTTP, without Proxy and context (use {proxy:} around it).</li>
 * <li><strong><code>supportsFormat:{format}</code></strong> - true if the resource type
 * supports this format, false otherwise</li>
 * </ul>
 */
public class ResourceTypeModule extends AbstractInputModule implements Serviceable {

    protected static final String SCHEMA_URI = "schemaUri";
    protected static final String HTTP_SCHEMA_URI = "httpSchemaUri";
    protected static final String EXPIRES = "expires";
    protected static final String SUPPORTS_FORMAT = "supportsFormat";

    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {
        Object value = null;

        try {
            Request request = ObjectModelHelper.getRequest(objectModel);
            Session session = RepositoryUtil.getSession(this.manager, request);

            ResourceType resourceType;
            Publication pub = null;
            String attribute;

            String[] steps = name.split(":");
            if (steps.length == 1) {
                DocumentFactory docFactory = DocumentUtil.createDocumentFactory(this.manager,
                        session);
                String webappUrl = ServletHelper.getWebappURI(request);
                Document document = docFactory.getFromURL(webappUrl);
                pub = document.getPublication();

                attribute = name;
                resourceType = document.getResourceType();
            } else {
                attribute = steps[1];
                String resourceTypeName = steps[0];

                ServiceSelector selector = null;
                try {
                    selector = (ServiceSelector) this.manager
                            .lookup(ResourceType.ROLE + "Selector");
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
                value = transformFallbackUriToHttp(pub.getId(), uri);
            } else if (attribute.equals(EXPIRES)) {
                Date expires = resourceType.getExpires();
                SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss zzz");
                value = sdf.format(expires);
            } else if (attribute.equals(SUPPORTS_FORMAT)) {
                String format = steps[steps.length - 1];
                String[] formats = resourceType.getFormats();
                return Boolean.toString(Arrays.asList(formats).contains(format));
            } else {
                throw new ConfigurationException("Attribute [" + name + "] not supported!");
            }

        } catch (Exception e) {
            throw new ConfigurationException("Resolving attribute [" + name + "] failed: ", e);
        }

        return value;
    }

    /**
     * Transforms a fallback URI for resources into a HTTP URL.
     * 
     * Currently only supports module urls:
     * 
     * fallback://lenya/modules/foo/resources/schemas/bar.rng ->
     * prefix/pubid/modules/foo/schemas/bar.rng
     * 
     * FIXME: allow other kind of fallback URIs
     * 
     * @param pubid publication id of the current document
     * @param prefix prefix which will be prepended to the resulting URL
     * @param uri fallback uri, must start with fallback://
     * @return A string.
     * @throws ConfigurationException
     */
    protected String transformFallbackUriToHttp(String pubid, String uri)
            throws ConfigurationException {
        if (uri.startsWith("fallback://lenya/modules/")) {
            String path = StringUtils.substringAfter(uri, "fallback://lenya/modules/");
            String module = StringUtils.substringBefore(path, "/");
            path = StringUtils.substringAfter(path, module + "/resources");
            return "/" + pubid + "/modules/" + module + path;
        } else {
            throw new ConfigurationException("Don't know how to create HTTP URL from : " + uri);
        }
    }

    protected ServiceManager manager;

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}
