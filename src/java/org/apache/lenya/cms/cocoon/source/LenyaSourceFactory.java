/*
 * Copyright 1999-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.lenya.cms.cocoon.source;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.components.ContextHelper;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceException;
import org.apache.excalibur.source.SourceFactory;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeException;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.Publication;

/**
 * A factory for the Lenya protocol.
 * @version $Id:$
 */
public class LenyaSourceFactory extends AbstractLogEnabled implements SourceFactory, ThreadSafe,
        Contextualizable, Serviceable, Configurable {

    protected static final String SCHEME = "lenya:";

    /** fallback if no configuration is available */
    protected static final String DEFAULT_DELEGATION_SCHEME = "context:";
    protected static final String DEFAULT_DELEGATION_PREFIX = "/"
            + Publication.PUBLICATION_PREFIX_URI;

    private Context context;
    private ServiceManager manager;
    private String delegationScheme;
    private String delegationPrefix;

    /**
     * Used for resolving the object model.
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize(Context context) throws ContextException {
        this.context = context;
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration configuration) throws ConfigurationException {
        this.delegationScheme = configuration.getAttribute("scheme", DEFAULT_DELEGATION_SCHEME);
        this.delegationPrefix = configuration.getAttribute("prefix", DEFAULT_DELEGATION_PREFIX);
    }

    /**
     * @see org.apache.excalibur.source.SourceFactory#getSource(java.lang.String, java.util.Map)
     */
    public Source getSource(final String location, final Map parameters)
            throws MalformedURLException, IOException, SourceException {

        SourceResolver sourceResolver = null;

        try {
            sourceResolver = (SourceResolver) manager
                    .lookup(org.apache.excalibur.source.SourceResolver.ROLE);

            String path = location.substring(SCHEME.length());

            if (!path.startsWith("//")) {

                Map objectModel = ContextHelper.getObjectModel(this.context);
                try {
                    PageEnvelopeFactory pageEnvelopeFactory = PageEnvelopeFactory.getInstance();

                    if (pageEnvelopeFactory != null) {
                        PageEnvelope pageEnvelope = pageEnvelopeFactory
                                .getPageEnvelope(objectModel);

                        if (pageEnvelope != null) {
                            String publicationID = pageEnvelope.getPublication().getId();
                            String area = pageEnvelope.getDocument().getArea();
                            path = "/" + publicationID + "/" + Publication.CONTENT_PATH + "/"
                                    + area + path;
                        }
                    }
                } catch (PageEnvelopeException e) {
                    throw new SourceException(
                            "Cannot attach publication-id and/or area to " + path, e);
                }
            }

            path = this.delegationScheme + this.delegationPrefix + path;

            return sourceResolver.resolveURI(path);
            
        } catch (ServiceException e) {
            throw new SourceException(e.getMessage(), e);
        } finally {
            this.manager.release(sourceResolver);
        }
    }

    /**
     * Does nothing beacuse the delegated factory does this.
     * @see org.apache.excalibur.source.SourceFactory#release(org.apache.excalibur.source.Source)
     */
    public void release(Source source) {
    }
}