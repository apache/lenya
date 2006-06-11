package org.apache.lenya.cms.cocoon.components.modules.input;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
/**
 * Converts querystring Usecases into Module format.
 */
public class Usecase2ModuleModule extends AbstractPageEnvelopeModule implements Serviceable, Contextualizable, ThreadSafe {
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
        String resolvedUri = name;
          PageEnvelope pe = getEnvelope(objectModel);
          String publication = pe.getPublication().getId();
          Map contextmap = ContextHelper.getObjectModel(context);
          org.apache.cocoon.environment.http.HttpRequest req = 
                 (org.apache.cocoon.environment.http.HttpRequest) contextmap.get("request");
          String request = req.getRequestURI();
          String qs = req.getQueryString();
          String lu = req.getParameter("lenya.usecase");
          if(null == lu) lu = "";
//STEP          String ls = req.getParameter("lenya.step");
//STEP          if(null == ls) ls = "";
          String before = "";
          String after = "";
          int pos = request.indexOf(publication);
          if(pos > 0) {
             pos += publication.length();
             before = request.substring(0, pos + 1);
              pos = request.indexOf("/", pos + 1);
              if(pos > 0){
                 after = request.substring(pos);
                 if(lu.length() > 0){
                    before += lu;
                 }else{
                     before = request.substring(0, pos);
                 }
              }
//STEP              if(ls.length() > 0){
//STEP                 before += "/" + ls;
//STEP              }
              qs = qs.replaceAll("lenya.usecase=" + lu, "");
//STEP              qs = qs.replaceAll("lenya.step=" + ls, "");
              //Cleanup
              qs = qs.replaceAll("&&", "&");
              qs = qs.replaceAll("\\?&", "\\?");
              qs = qs.replaceAll("&$", "");
              resolvedUri = before + after + "?" + qs;
          }
        return resolvedUri;
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