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
import org.apache.cocoon.environment.Request;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceException;
import org.apache.excalibur.source.SourceFactory;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeException;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.repository.Session;

/**
 * A factory for the "lenya" scheme (virtual protocol), which is used to resolve any src="lenya:..."
 * attributes in sitemaps. This implementation constructs the path to the source document from the
 * page envelope and delegates any further resolving to the "context" source resolver of Cocoon.
 * 
 * @version $Id$
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
    public void contextualize(Context _context) throws ContextException {
        this.context = _context;
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager _manager) throws ServiceException {
        this.manager = _manager;
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
            sourceResolver = (SourceResolver) this.manager.lookup(org.apache.excalibur.source.SourceResolver.ROLE);

            String path = location.substring(SCHEME.length());

            if (!path.startsWith("//")) {

                Map objectModel = ContextHelper.getObjectModel(this.context);
                try {
                    DocumentIdentityMap map = new DocumentIdentityMap(this.manager, getLogger());
                    PageEnvelopeFactory pageEnvelopeFactory = PageEnvelopeFactory.getInstance();

                    if (pageEnvelopeFactory != null) {
                        PageEnvelope pageEnvelope = pageEnvelopeFactory.getPageEnvelope(map,
                                objectModel);

                        if (pageEnvelope != null) {
                            String publicationID = pageEnvelope.getPublication().getId();
                            String area = pageEnvelope.getDocument().getArea();
                            path = "/" + publicationID + "/" + Publication.CONTENT_PATH + "/"
                                    + area + path;
                        }
                    }
                } catch (final PageEnvelopeException e1) {
                    throw new SourceException("Cannot attach publication-id and/or area to " + path,
                            e1);
                }
            }

            while (path.startsWith("/")) {
                path = path.substring(1);
            }

            Request request = ContextHelper.getRequest(this.context);
            Session session = (Session) request.getAttribute(Session.class.getName());
            if (session == null) {
                Identity identity = (Identity) request.getSession(false)
                        .getAttribute(Identity.class.getName());
                session = new Session(identity, getLogger());
            }

            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Creating repository source for URI [" + location + "]");
            }

            // path = this.delegationScheme + this.delegationPrefix + path;
            return new RepositorySource(this.manager, location, session, getLogger());

            // return sourceResolver.resolveURI(path);

        } catch (final ServiceException e) {
            throw new SourceException(e.getMessage(), e);
        } finally {
            this.manager.release(sourceResolver);
        }
    }

    /**
     * Does nothing because the delegated factory does this.
     * @see org.apache.excalibur.source.SourceFactory#release(org.apache.excalibur.source.Source)
     */
    public void release(Source source) {
        // do nothing
    }
}