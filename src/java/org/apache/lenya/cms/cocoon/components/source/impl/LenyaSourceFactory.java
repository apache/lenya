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
package org.apache.lenya.cms.cocoon.components.source.impl;

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
 * A factory for the lenya protocol.
 */
public class LenyaSourceFactory 
    extends AbstractLogEnabled 
    implements SourceFactory, ThreadSafe, Contextualizable, Serviceable, Configurable {
    
    protected static final String SCHEME = "lenya:";
    
    /** fallback if no configuration is available */
    protected static final String DEFAULT_DELEGATION_SCHEME = "context:";    
    protected static final String DEFAULT_DELEGATION_PREFIX = "/" + Publication.PUBLICATION_PREFIX_URI;
    
    private Context context;
    private ServiceManager manager;
    private SourceResolver sourceResolver;
    private String delegationScheme;
    private String delegationPrefix;
    
    /**
     * Contextualizable, get the object model
     */
    public void contextualize( Context context ) throws ContextException {
        this.context = context;
    }    
    
    /**
     * Lookup the SlideRepository.
     * 
     * @param manager ServiceManager.
     */
    public void service(ServiceManager manager) throws ServiceException {
        //this.sourceResolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
        this.manager = manager;
    }
    
    public void configure(Configuration configuration)
        throws ConfigurationException {
        this.delegationScheme = configuration.getAttribute("scheme", DEFAULT_DELEGATION_SCHEME);
        this.delegationPrefix = configuration.getAttribute("prefix", DEFAULT_DELEGATION_PREFIX);
    }    

    /**
     * Get a <code>Source</code> object.
     * @param parameters This is optional.
     */
    public Source getSource(final String location, final Map parameters)
        throws MalformedURLException, IOException, SourceException {
        
        try {
            this.sourceResolver = (SourceResolver) manager.lookup(org.apache.excalibur.source.SourceResolver.ROLE);
        } catch (ServiceException e) {
            throw new SourceException(e.getMessage());
        }
        
        String path = location.substring(SCHEME.length());
        
        if (!path.startsWith("//")) {
            
            Map objectModel = ContextHelper.getObjectModel( this.context );
            try {
                PageEnvelopeFactory pageEnvelopeFactory = PageEnvelopeFactory.getInstance(); 
                
                if (pageEnvelopeFactory != null) {
                    PageEnvelope pageEnvelope = pageEnvelopeFactory.getPageEnvelope(objectModel);
                
                    if (pageEnvelope != null) {
                        String publicationID = pageEnvelope.getPublication().getId();
                        String area = pageEnvelope.getDocument().getArea();
                        path = "/" + publicationID + "/" + Publication.CONTENT_PATH + "/" + area + path;
                        
                    }
                }                
            } catch (PageEnvelopeException e) {
                throw new SourceException("Cannot attach publication-id and/or area to "+path, e);
            }
        }
        
        path = this.delegationScheme + this.delegationPrefix + path;
        
        return sourceResolver.resolveURI(path);
    }
    
    public void release(Source source) {
        // do nothing beacuse the deligated factory does this.
    }
  
    
}
