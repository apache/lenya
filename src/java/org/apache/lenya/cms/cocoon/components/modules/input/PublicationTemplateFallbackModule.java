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
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.cms.publication.templating.ExistingSourceResolver;
import org.apache.lenya.cms.publication.templating.PublicationTemplateManager;

/**
 * @version $Id$
 */
public class PublicationTemplateFallbackModule extends AbstractPageEnvelopeModule {

    /**
     * Ctor.
     */
    public PublicationTemplateFallbackModule() {
        super();
    }

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttribute(java.lang.String,
     *      org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {

        String resolvedUri = null;

        try {
            PublicationTemplateManager templateManager = (PublicationTemplateManager) this.manager
                    .lookup(PublicationTemplateManager.ROLE);
            PublicationFactory factory = PublicationFactory.getInstance(getLogger());
            Publication publication = factory.getPublication(objectModel);
            templateManager.setup(publication);

            ExistingSourceResolver resolver = new ExistingSourceResolver();
            templateManager.visit(name, resolver);
            resolvedUri = resolver.getURI();

        } catch (Exception e) {
            String message = "Resolving path [" + name + "] failed: ";
            getLogger().error(message, e);
            throw new ConfigurationException(message, e);
        }
        return resolvedUri;
    }

    /**
     * Returns the base URI for a certain publication.
     * @param publication The publication.
     * @return A string.
     */
    public static String getBaseURI(Publication publication) {
        String publicationUri = "context://" + Publication.PUBLICATION_PREFIX_URI + "/"
                + publication.getId();
        return publicationUri;
    }

    /**
     * Returns the base URI for a certain publication including the prefix "lenya".
     * @param publication The publication.
     * @return A string.
     */
    protected String getLenyaBaseURI(Publication publication) {
        String publicationUri = "context://" + Publication.PUBLICATION_PREFIX_URI + "/"
                + publication.getId() + "/lenya";
        return publicationUri;
    }

}