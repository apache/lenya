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

/* $Id: PublicationFactory.java 177927 2005-05-23 05:32:20Z gregor $  */

package org.apache.lenya.cms.publication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.cocoon.processing.ProcessInfoProvider;
import org.apache.cocoon.spring.configurator.WebAppContextUtils;
import org.apache.cocoon.util.AbstractLogEnabled;
import org.apache.commons.lang.Validate;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.TraversableSource;
import org.apache.excalibur.source.impl.ResourceSource;
import org.apache.lenya.cms.repository.NodeFactory;
import org.apache.lenya.cms.repository.RepositoryException;

/**
 * Factory for creating publication objects.
 */
public final class PublicationManagerImpl extends AbstractLogEnabled implements PublicationManager {

    private Map id2config;
    private NodeFactory nodeFactory;
    private String publicationBaseUri;
    private SourceResolver sourceResolver;

    protected synchronized Map getId2config() throws PublicationException {
        if (this.id2config == null) {
            this.id2config = new HashMap();
            ResourceSource res;
            Source source = null;
            try {
                source = this.sourceResolver.resolveURI(getPubBaseUri());
                if (source instanceof TraversableSource) {
                    TraversableSource pubsSource = (TraversableSource) source;
                    if (pubsSource.isCollection()) {
                        Collection pubSources = pubsSource.getChildren();
                        for (Iterator i = pubSources.iterator(); i.hasNext();) {
                            TraversableSource pubSource = (TraversableSource) i.next();
                            TraversableSource configSource = (TraversableSource) this.sourceResolver
                                    .resolveURI(pubSource.getURI() + "/"
                                            + PublicationConfiguration.CONFIGURATION_URI);
                            if (configSource.exists()) {
                                String id = pubSource.getName();
                                addPublication(id);
                            }
                        }
                    } else {
                        getLogger().warn(
                                "The publications directory " + pubsSource
                                        + " does not exist.");
                    }
                }
            } catch (final Exception e) {
                throw new PublicationException(e);
            } finally {
                if (source != null) {
                    this.sourceResolver.release(source);
                }
            }
        }
        return this.id2config;
    }

    //florent : to get publication from spring
    public Publication getPublication(String pubID){
    	return (Publication)WebAppContextUtils.getCurrentWebApplicationContext().getBean(Publication.class.getName()+"/"+pubID);
    }
    	
    
    public Publication getPublication(DocumentFactory factory, String id)
            throws PublicationException {
        Validate.notNull(id);
        Map id2config = getId2config();
        if (!id2config.containsKey(id)) {
            throw new PublicationException("The publication [" + id + "] does not exist.");
        }

        PublicationConfiguration config = (PublicationConfiguration) id2config.get(id);
        PublicationFactory pubFactory = new PublicationFactory(factory.getSession(),
                getNodeFactory(), config);
        try {
            org.apache.lenya.cms.repository.Session repoSession = ((SessionImpl) factory
                    .getSession()).getRepositorySession();
            return (Publication) repoSession.getRepositoryItem(pubFactory, id);
        } catch (RepositoryException e) {
            throw new PublicationException(e);
        }
    }

    public Publication[] getPublications(DocumentFactory factory) {
        List publications = new ArrayList();

        try {
            Map id2config = getId2config();
            for (Iterator i = id2config.keySet().iterator(); i.hasNext();) {
                String publicationId = (String) i.next();
                Publication publication = getPublication(factory, publicationId);
                publications.add(publication);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return (Publication[]) publications.toArray(new Publication[publications.size()]);
    }

    public String[] getPublicationIds() {
        Set ids;
        try {
            ids = getId2config().keySet();
        } catch (PublicationException e) {
            throw new RuntimeException(e);
        }
        return (String[]) ids.toArray(new String[ids.size()]);
    }

    public void addPublication(String pubId) throws PublicationException {
        Map id2config = getId2config();
        if (id2config.containsKey(pubId)) {
            throw new PublicationException("The publication [" + pubId + "] already exists.");
        }
        ProcessInfoProvider process = (ProcessInfoProvider) WebAppContextUtils
                .getCurrentWebApplicationContext().getBean(ProcessInfoProvider.ROLE);
        PublicationConfiguration config = new PublicationConfiguration(pubId, getPubBaseUri(),
                process.getRequest().getContextPath());
        config.setSourceResolver(this.sourceResolver);
        id2config.put(pubId, config);
    }

    protected String getPubBaseUri() throws PublicationException {
        return this.publicationBaseUri == null ? getServletContext() : this.publicationBaseUri;
    }

    private String getServletContext() {
        return WebAppContextUtils.getCurrentWebApplicationContext().getServletContext()
                .getRealPath("/");
    }

    public NodeFactory getNodeFactory() {
        return nodeFactory;
    }

    public void setNodeFactory(NodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    public void setPubBaseUri(String uri) {
        this.publicationBaseUri = uri;
    }

    public void setSourceResolver(SourceResolver sourceResolver) {
        this.sourceResolver = sourceResolver;
    }

}
