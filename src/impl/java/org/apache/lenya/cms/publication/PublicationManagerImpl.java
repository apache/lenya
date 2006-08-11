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

/* $Id: PublicationFactory.java 177927 2005-05-23 05:32:20Z gregor $  */

package org.apache.lenya.cms.publication;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static Map id2config = new HashMap();

    public synchronized Publication getPublication(DocumentFactory factory, String id) throws PublicationException {
        
        assert id != null;
        if (id.indexOf("/") != -1) {
            throw new PublicationException("The publication ID [" + id + "] must not contain a slash!");
        }

        PublicationConfiguration config = null;

        if (id2config.containsKey(id)) {
            config = (PublicationConfiguration) id2config.get(id);
        } else {
            config = new PublicationConfiguration(id, servletContextPath);
            ContainerUtil.enableLogging(config, getLogger());
            id2config.put(id, config);
        }

        if (config == null) {
            throw new PublicationException("The publication for ID [" + id
                    + "] could not be created.");
        }
        return new PublicationImpl(this.manager, factory, config);
    }

    public Publication[] getPublications(DocumentFactory factory) throws PublicationException {
        List publications = new ArrayList();

        try {
            File servletContext = new File(this.servletContextPath);
            File publicationsDirectory = new File(servletContext, Publication.PUBLICATION_PREFIX);
            File[] publicationDirectories = publicationsDirectory.listFiles(new FileFilter() {
                public boolean accept(File file) {
                    return file.isDirectory();
                }
            });

            for (int i = 0; i < publicationDirectories.length; i++) {
                String publicationId = publicationDirectories[i].getName();
                Publication publication = getPublication(factory, publicationId);
                publications.add(publication);
            }

        } catch (Exception e) {
            throw new PublicationException(e);
        }

        return (Publication[]) publications.toArray(new Publication[publications.size()]);
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

}