package org.apache.lenya.cms.cocoon.components.modules.input;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.components.modules.input.AbstractInputModule;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeException;
import org.apache.lenya.cms.publication.Publication;
/**
 * Retrieves Publication variables.
 * 
 * @author solprovider
 * @since 1.3
 * @deprecated Use org.apache.lenya.cms.modules.PublicationInputModule.  Will be deleted soon.
 */
public class PublicationModule extends AbstractInputModule implements ThreadSafe {
   // public class PublicationModule extends AbstractPageEnvelopeModule implements Serviceable, Contextualizable, ThreadSafe {
   // private ServiceManager manager;
   // private org.apache.avalon.framework.context.Context context;
   /**
    * @see org.apache.cocoon.components.modules.input.InputModule#getAttribute(java.lang.String, org.apache.avalon.framework.configuration.Configuration, java.util.Map)
    */
   public Object getAttribute(String name, Configuration modeConf, Map objectModel) throws ConfigurationException {
      if(getLogger().isDebugEnabled()){
         getLogger().debug("Resolving [" + name + "]");
      }
      PageEnvelope pe;
      try{
         pe = PageEnvelope.getCurrent();
      }catch(PageEnvelopeException e){
         throw new ConfigurationException("Resolving page envelope failed: ", e);
      }
      Publication pub = pe.getPublication();
      if(name.equalsIgnoreCase("contenttype"))
         return pub.getContentType();
      if(name.equalsIgnoreCase("defaultlanguage"))
         return pub.getDefaultLanguage();
      if(name.equalsIgnoreCase("BreadcrumbPrefix"))
         return pub.getBreadcrumbPrefix();
      if(name.equalsIgnoreCase("SSLPrefix"))
         return pub.getSSLPrefix();
      try{
         if(name.equalsIgnoreCase("ServletContext"))
            return pub.getServletContext().getCanonicalPath();
         if(name.equalsIgnoreCase("Directory"))
            return pub.getDirectory().getCanonicalPath();
         if(name.equalsIgnoreCase("ContentDirectory"))
            return pub.getContentDirectory().getCanonicalPath();
      }catch(java.io.IOException ioe){
         return "error";
      }
      if(name.equalsIgnoreCase("languages")){
         String[] langa = pub.getLanguages();
         String langs = langa[0];
         for(int l = 1; l < langa.length; l++)
            langs += ";" + langa[l];
         return langs;
      }
      return pub.getId();
   }
   /**
    * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeNames(org.apache.avalon.framework.configuration.Configuration, java.util.Map)
    */
   public Iterator getAttributeNames(Configuration modeConf, Map objectModel) throws ConfigurationException {
      return Collections.EMPTY_SET.iterator();
   }
   /**
    * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeValues(java.lang.String, org.apache.avalon.framework.configuration.Configuration, java.util.Map)
    */
   public Object[] getAttributeValues(String name, Configuration modeConf, Map objectModel) throws ConfigurationException {
      Object[] objects = {getAttribute(name, modeConf, objectModel)};
      return objects;
   }
   // /**
   // * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
   // */
   // public void service(ServiceManager manager) throws ServiceException {
   // this.manager = manager;
   // }
   // /**
   // * Contextualizable, get the object model
   // */
   // public void contextualize(Context context) throws ContextException {
   // this.context = context;
   // }
   /**
    * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
    */
   public void configure(Configuration conf) throws ConfigurationException {
      super.configure(conf);
   }
}