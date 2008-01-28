package org.apache.lenya.cms.modules;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.cocoon.components.modules.input.AbstractPageEnvelopeModule;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationModules;
/**
 * Retrieves Module Variables from publication.xconf and module.xml.
 * 
 * Variables are specified as <module name="modulename"><variable name="{variablename}">{value}</variable></module>
 */
public class ModuleInputModule extends AbstractPageEnvelopeModule implements Serviceable, ThreadSafe {
   private ServiceManager manager;
   /**
    * @see org.apache.cocoon.components.modules.input.InputModule#getAttribute(java.lang.String, org.apache.avalon.framework.configuration.Configuration, java.util.Map)
    */
   private String moduleId = "";
   public Object getAttribute(String name, Configuration modeConf, Map objectModel) throws ConfigurationException {
      if(getLogger().isDebugEnabled()){
         getLogger().debug("Resolving [" + name + "]");
      }
      init();
      // Standard Variables
      if(name.equalsIgnoreCase("module"))
         return moduleId;
      // Module Variables
      PageEnvelope pe = getEnvelope(objectModel);
      Publication pub = pe.getPublication();
      PublicationModules modules = pub.getModules();
      return modules.getVariable(moduleId, name);
//      return Modules.getModule(pub.getId(), moduleId, pub.getContentType()).getVariable(name);
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
   /**
    * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
    */
   public void service(ServiceManager manager) throws ServiceException {
      this.manager = manager;
   }
   /**
    * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
    */
   public void configure(Configuration conf) throws ConfigurationException {
      super.configure(conf);
   }
   private void init() {
      String uri = "";
      try{
         SourceResolver resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
         Source source = resolver.resolveURI("");
         uri = source.getURI();
         if(resolver != null)
            manager.release(resolver);
      }catch(org.apache.avalon.framework.service.ServiceException se){
         // Report Error?
      }catch(java.net.MalformedURLException mue){
         // Report Error?
      }catch(java.io.IOException ioe){
         // Report Error?
      }
      // TODO: Release resolver
      StringTokenizer tokens = new StringTokenizer(uri, "/");
      while(tokens.hasMoreTokens() && !(tokens.nextToken().equals("modules")));
      if(tokens.hasMoreTokens())
         moduleId = tokens.nextToken();
   }
}