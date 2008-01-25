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

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.SourceUtil;
import org.apache.lenya.cms.cocoon.components.context.ContextUtility;
import org.apache.lenya.cms.repository.RepositoryException;
import org.apache.lenya.util.Assert;

/**
 * Factory for creating publication objects.
 */
public final class PublicationManagerImpl extends AbstractLogEnabled implements PublicationManager,
        Serviceable, Initializable, ThreadSafe {

    /**
     * Create a new <code>PublicationFactory</code>.
     */
    public PublicationManagerImpl() {
    }

    private Map id2config;

    protected synchronized Map getId2config() throws PublicationException {
        if (this.id2config == null) {
            this.id2config = new HashMap();
            File servletContext = new File(this.servletContextPath);
            File publicationsDirectory = new File(servletContext, Publication.PUBLICATION_PREFIX);
            File[] publicationDirectories = publicationsDirectory.listFiles(new FileFilter() {
                public boolean accept(File file) {
                    File configFile = new File(file, PublicationConfiguration.CONFIGURATION_FILE);
                    return configFile.exists();
                }
            });
            for (int i = 0; i < publicationDirectories.length; i++) {
                String id = publicationDirectories[i].getName();
                addPublication(id);
            }
        }
        return this.id2config;
    }

    public Publication getPublication(DocumentFactory factory, String id)
            throws PublicationException {

        Assert.notNull("publication ID", id);
        Map id2config = getId2config();
        if (!id2config.containsKey(id)) {
            throw new PublicationException("The publication [" + id + "] does not exist.");
        }

        PublicationConfiguration config = (PublicationConfiguration) id2config.get(id);
        PublicationFactory pubFactory = new PublicationFactory(this.manager, config);
        try {
            return (Publication) factory.getSession().getRepositoryItem(pubFactory, id);
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

    private String servletContextPath;

    private ServiceManager manager;

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    public void initialize() throws Exception {
        SourceResolver resolver = null;
        Source source = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI("context:///");
            this.servletContextPath = SourceUtil.getFile(source).getCanonicalPath();
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                this.manager.release(resolver);
            }
        }
    }

    public void addPublication(String pubId) throws PublicationException {
        Map id2config = getId2config();
        if (id2config.containsKey(pubId)) {
            throw new PublicationException("The publication [" + pubId + "] already exists.");
        }
        ContextUtility context = null;
        try {
            context = (ContextUtility) this.manager.lookup(ContextUtility.ROLE);
            PublicationConfiguration config = new PublicationConfiguration(pubId,
                    this.servletContextPath, context.getRequest().getContextPath());
            ContainerUtil.enableLogging(config, getLogger());
            id2config.put(pubId, config);
        } catch (ServiceException e) {
            throw new PublicationException(e);
        }
        finally {
            if (context != null) {
                this.manager.release(context);
            }
        }
    }

    protected String getServletContextPath() {
        return this.servletContextPath;
    }

}
