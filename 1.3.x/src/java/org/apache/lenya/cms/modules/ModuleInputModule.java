package org.apache.lenya.cms.modules;
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
import org.apache.lenya.util.Globals;
/**
 * Retrieves Module Variables from publication.xconf and module.xml.
 * 
 * Variables are specified as <module name="modulename"><variable name="{variablename}">{value}</variable></module>
 * 
 * @author solprovider
 * @since 1.3
 */
public class ModuleInputModule extends AbstractInputModule implements ThreadSafe {
   /**
    * @see org.apache.cocoon.components.modules.input.InputModule#getAttribute(java.lang.String, org.apache.avalon.framework.configuration.Configuration, java.util.Map)
    */
   private String moduleId = "";
   public Object getAttribute(String name, Configuration modeConf, Map objectModel) throws ConfigurationException {
      if(getLogger().isDebugEnabled()){
         getLogger().debug("Resolving [" + name + "]");
      }
      moduleId = Globals.getModuleId();
      // Standard Variables
      if(name.equalsIgnoreCase("module")){
         return moduleId;
      }
      // Module Variables
      PageEnvelope pe;
      try{
         pe = PageEnvelope.getCurrent();
      }catch(PageEnvelopeException e){
         throw new ConfigurationException("Resolving page envelope failed: ", e);
      }
      Publication pub = pe.getPublication();
      PublicationModules modules = pub.getModules();
      return modules.getVariable(moduleId, name);
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
    * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
    */
   public void configure(Configuration conf) throws ConfigurationException {
      super.configure(conf);
   }
}