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

import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.templating.ExistingUsecaseResolver;
import org.apache.lenya.cms.publication.templating.PublicationTemplateManager;
import org.apache.lenya.cms.publication.templating.PublicationTemplateManagerImpl;

/**
 * @version $Id$
 */
public class UsecaseFallbackModule extends AbstractPageEnvelopeModule implements Serviceable {

    /**
     * Ctor.
     */
    public UsecaseFallbackModule() {
        super();
    }

    protected static final String USECASE_SITEMAP = "usecase.xmap";

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttribute(java.lang.String,
     *      org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {

        String resolvedSitemapUri = null;

        try {
            PublicationTemplateManager templateManager = (PublicationTemplateManager) this.manager
                    .lookup(PublicationTemplateManager.ROLE);
            PageEnvelope envelope = getEnvelope(objectModel);
            templateManager.setup(envelope.getPublication());

            ExistingUsecaseResolver resolver = new ExistingUsecaseResolver(name);
            templateManager.visit(resolver);

            Publication publication = resolver.getPublication();
            if (publication != null) {
                resolvedSitemapUri = PublicationTemplateManagerImpl.getBaseURI(publication) + "/"
                        + USECASE_SITEMAP;
            } else {
                resolvedSitemapUri = "context://lenya/" + USECASE_SITEMAP;
            }
        } catch (Exception e) {
            throw new ConfigurationException("Resolving sitemap URI for usecase [" + name
                    + "] failed: ", e);
        }
        return resolvedSitemapUri;
    }
 
    private ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}