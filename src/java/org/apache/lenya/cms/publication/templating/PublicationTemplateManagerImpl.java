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

package org.apache.lenya.cms.publication.templating;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;

/**
 * Manager for publication templates.
 * 
 * @version $Id: PublicationTemplateManagerImpl.java 123348 2004-12-25 22:49:57Z
 *          gregor $
 */
public class PublicationTemplateManagerImpl extends AbstractLogEnabled implements
        PublicationTemplateManager, Serviceable {

    private Publication publication;

    private static final String ELEMENT_TEMPLATES = "templates";
    private static final String ELEMENT_TEMPLATE = "template";
    private static final String ATTRIBUTE_ID = "id";

    /**
     * Ctor.
     */
    public PublicationTemplateManagerImpl() {
	    // do nothing
    }

    private Publication[] templatePublications;

    /**
     * @see org.apache.lenya.cms.publication.templating.PublicationTemplateManager#setup(org.apache.lenya.cms.publication.Publication)
     */
    public void setup(Publication _publication) throws ConfigurationException {
        this.publication = _publication;

        File configFile = new File(_publication.getDirectory(), Publication.CONFIGURATION_FILE);
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();

        try {
            Configuration config = builder.buildFromFile(configFile);
            Configuration templatesConfig = config.getChild(ELEMENT_TEMPLATES);
            if (templatesConfig != null) {
                List templates = new ArrayList();
                Configuration[] templateConfigs = templatesConfig.getChildren(ELEMENT_TEMPLATE);
                for (int i = 0; i < templateConfigs.length; i++) {
                    String templateId = templateConfigs[i].getAttribute(ATTRIBUTE_ID);
                    PublicationFactory factory = PublicationFactory.getInstance(getLogger());
                    Publication template = factory.getPublication(templateId, _publication
                            .getServletContext().getAbsolutePath());
                    templates.add(template);
                }
                this.templatePublications = (Publication[]) templates
                        .toArray(new Publication[templates.size()]);
            }
        } catch (Exception e) {
            throw new ConfigurationException("Setting up templates failed: ", e);
        }
    }

    /**
     * @see org.apache.lenya.cms.publication.templating.PublicationTemplateManager#getTemplates()
     */
    public Publication[] getTemplates() {
        return this.templatePublications;
    }

    /**
     * @see org.apache.lenya.cms.publication.templating.PublicationTemplateManager#visit(java.lang.String,
     *      org.apache.lenya.cms.publication.templating.SourceVisitor)
     */
    public void visit(String path, SourceVisitor visitor) {

        SourceResolver resolver = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);

            String[] baseUris = getBaseURIs();
            for (int i = 0; i < baseUris.length; i++) {
                String uri = baseUris[i] + "/" + path;

                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Trying to resolve URI [" + uri + "]");
                }

                Source source = null;
                try {
                    source = resolver.resolveURI(uri);
                    visitor.visit(source);
                } catch (Exception e) {
                    getLogger().error("Could not resolve URI [" + uri + "]: ", e);
                    throw e;
                } finally {
                    if (source != null) {
                        resolver.release(source);
                    }
                }
            }

        } catch (Exception e) {
            throw new TemplatingException("Visiting path [" + path + "] failed: ", e);
        } finally {
            if (resolver != null) {
                this.manager.release(resolver);
            }
        }

    }

    private ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager _manager) throws ServiceException {
        this.manager = _manager;
    }

    /**
     * Returns the publication.
     * @return A publication.
     */
    protected Publication getPublication() {
        return this.publication;
    }

    /**
     * Returns the base URIs in traversing order.
     * @return An array of strings.
     */
    protected String[] getBaseURIs() {

        List uris = new ArrayList();

        Publication[] publications = getPublications();
        for (int i = 0; i < publications.length; i++) {
            uris.add(getBaseURI(publications[i]));
        }

        String coreBaseURI = "context://";
        uris.add(coreBaseURI);

        return (String[]) uris.toArray(new String[uris.size()]);
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
     * @see org.apache.lenya.cms.publication.templating.PublicationTemplateManager#visit(org.apache.lenya.cms.publication.templating.PublicationVisitor)
     */
    public void visit(PublicationVisitor visitor) {
        SourceResolver resolver = null;
        try {
            resolver = (SourceResolver) this.manager.lookup(SourceResolver.ROLE);

            Publication[] publications = getPublications();
            for (int i = 0; i < publications.length; i++) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Visiting publication [" + publications[i] + "]");
                }
                visitor.visit(publications[i]);
            }

        } catch (Exception e) {
            throw new TemplatingException("Visiting publications failed: ", e);
        } finally {
            if (resolver != null) {
                this.manager.release(resolver);
            }
        }

    }

    /**
     * Returns the publications in traversing order.
     * @return An array of strings.
     */
    protected Publication[] getPublications() {

        List publications = new ArrayList();

        publications.add(getPublication());

        Publication[] templates = getTemplates();
        for (int i = 0; i < templates.length; i++) {
            publications.add(templates[i]);
        }

        return (Publication[]) publications.toArray(new Publication[publications.size()]);
    }
    
}