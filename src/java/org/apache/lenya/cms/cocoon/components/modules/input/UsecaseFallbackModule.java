/*
 * Created on 09.08.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.lenya.cms.cocoon.components.modules.input;

import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.templating.ExistingUsecaseResolver;
import org.apache.lenya.cms.publication.templating.PublicationTemplateManager;
import org.apache.lenya.cms.publication.templating.PublicationTemplateManagerImpl;

/**
 * @author nobby
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java -
 * Code Style - Code Templates
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
            PageEnvelope envelope = PageEnvelopeFactory.getInstance().getPageEnvelope(objectModel);
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