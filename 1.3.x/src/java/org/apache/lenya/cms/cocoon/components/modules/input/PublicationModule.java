package org.apache.lenya.cms.cocoon.components.modules.input;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.components.ContextHelper;
import org.apache.lenya.cms.publication.PageEnvelope;

import java.net.MalformedURLException;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.publication.Publication;

import org.apache.cocoon.components.CocoonComponentManager;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.excalibur.source.SourceUtil;
import org.apache.excalibur.source.URIAbsolutizer;

/**
 * Retrieves Publication variables.
 */
public class PublicationModule extends AbstractPageEnvelopeModule implements Serviceable, Contextualizable, ThreadSafe {
    private ServiceManager manager;
    private org.apache.avalon.framework.context.Context context;
    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttribute(java.lang.String,
     *      org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */

    public Object getAttribute(String name, Configuration modeConf, Map objectModel) throws ConfigurationException {
        if(getLogger().isDebugEnabled()) {
           getLogger().debug("Resolving [" + name + "]");
        }
        PageEnvelope pe = getEnvelope(objectModel);
        Publication pub = pe.getPublication();
        if(name.equalsIgnoreCase("contenttype")) return pub.getContentType();
        if(name.equalsIgnoreCase("defaultlanguage")) return pub.getDefaultLanguage();
        if(name.equalsIgnoreCase("BreadcrumbPrefix")) return pub.getBreadcrumbPrefix();
        if(name.equalsIgnoreCase("SSLPrefix")) return pub.getSSLPrefix();
        try{
        if(name.equalsIgnoreCase("ServletContext")) return pub.getServletContext().getCanonicalPath();
        if(name.equalsIgnoreCase("Directory")) return pub.getDirectory().getCanonicalPath();
        if(name.equalsIgnoreCase("ContentDirectory")) return pub.getContentDirectory().getCanonicalPath();
        }catch(java.io.IOException ioe){
           return "error";
        }

        if(name.equalsIgnoreCase("languages")){
           String[] langa = pub.getLanguages();
           String langs = langa[0];
           for(int l=1; l < langa.length; l++) langs += ";" + langa[l];
           return langs;
        }
        return pub.getId();
    }

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeNames(org.apache.avalon.framework.configuration.Configuration,
     *      java.util.Map)
     */
    public Iterator getAttributeNames(Configuration modeConf, Map objectModel) throws ConfigurationException {
        return Collections.EMPTY_SET.iterator();
    }

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeValues(java.lang.String,
     *      org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Object[] getAttributeValues(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {
        Object[] objects = { getAttribute(name, modeConf, objectModel) };
        return objects;
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }
    /**
     * Contextualizable, get the object model
     */
    public void contextualize( Context context ) throws ContextException {
        this.context = context;
    }
    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration conf) throws ConfigurationException {
        super.configure(conf);
    }
}