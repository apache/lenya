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
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.SourceUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.cms.publication.templating.ExistingSourceResolver;
import org.apache.lenya.cms.publication.templating.PublicationTemplateManager;

/**
 * This module uses publication templating to resolve the real path for a resource. The current
 * publication ID can be provided as a parameter: <code>{fallback:{pub-id}:foo/bar}</code>. This
 * is especially useful for cocoon:// request which are triggered from non-environment components
 * (e.g. the scheduler).
 * 
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
    public Object getAttribute(final String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Resolving publication template for file [" + name + "]");
        }

        String resolvedUri = null;
        PublicationTemplateManager templateManager = null;

        try {
            templateManager = (PublicationTemplateManager) this.manager
                    .lookup(PublicationTemplateManager.ROLE);
            PublicationFactory factory = PublicationFactory.getInstance(getLogger());
            Publication publication;
            String targetUri = null;

            // check if publication ID is provided in attribute name
            if (name.indexOf(":") > -1) {
                String[] parts = name.split(":");
                if (parts.length > 2) {
                    throw new RuntimeException(
                            "The attribute may not contain more than one colons!");
                }
                String publicationId = parts[0];
                targetUri = parts[1];

                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Publication ID provided explicitely: [" + publicationId
                            + "]");
                }

                SourceResolver resolver = null;
                Source source = null;
                try {
                    resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
                    source = resolver.resolveURI("context://");
                    String contextPath = SourceUtil.getFile(source).getAbsolutePath();
                    publication = factory.getPublication(publicationId, contextPath);
                } finally {
                    if (resolver != null) {
                        if (source != null) {
                            resolver.release(source);
                        }
                        this.manager.release(resolver);
                    }
                }
            } else {
                publication = factory.getPublication(objectModel);
                targetUri = name;
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Publication resolved from request: [" + publication.getId()
                            + "]");
                }
            }
            ExistingSourceResolver resolver = new ExistingSourceResolver();
            templateManager.visit(publication, targetUri, resolver);
            resolvedUri = resolver.getURI();

        } catch (final Exception e) {
            String message = "Resolving path [" + name + "] failed: ";
            getLogger().error(message, e);
            throw new ConfigurationException(message, e);
        } finally {
            if (templateManager != null) {
                this.manager.release(templateManager);
            }
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