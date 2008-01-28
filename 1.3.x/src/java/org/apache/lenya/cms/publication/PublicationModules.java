package org.apache.lenya.cms.publication;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.lenya.cms.modules.Module;
import org.apache.lenya.cms.modules.Modules;
//TODO: Rearchitect so can reset all without restarting Lenya?
public class PublicationModules {
   private List templates = new ArrayList(); // Default list of Publications for inherit.
   // TODO: external and exclude should have defaults in module.xml.
   // Current algorithm needs replacing.
   // Use TRUE, FALSE, UNSET rather than Boolean.
   // Change exclude to Map.
   private Map external = new HashMap(); // Every Module in Publication with Boolean.
   private Set exclude = new HashSet(); // Only Modules excluded.
   private Map inherit = new HashMap(); // Only Modules overriding inherit.
   private Map files = new HashMap();
   // private Map modules = new HashMap(); // Only Modules overriding inherit with new moduleID.
   boolean allAllowed = true;
   boolean allExternal = true;
   static Map variables = new HashMap(); // publication.module.variablename = value from publication.xconf
   // static Map defaultvariables = new HashMap(); // publication.module.variablename = value from module.xml
   // static Map globalvariables = new HashMap(); // module.variable = value from global module.xml
   // static boolean isGlobalvariablesSet = false;
   private String publicationId;
   private String servletContextPath;
   private String contentType;
   // Dev testing
   Configuration config;
   public PublicationModules(String publicationId, String contentType, String servletContextPath, Configuration config) {
      if(null == config)
         return;
      int i;
      this.publicationId = publicationId;
      this.servletContextPath = servletContextPath;
      this.contentType = contentType;
      this.config = config; // TESTING
      String[] attrnames = config.getAttributeNames();
      for(i = 0; i < attrnames.length; i++){
         try{
            String attribute = attrnames[i];
            String value = config.getAttribute(attrnames[i]);
            if(attribute.equalsIgnoreCase("include")){
               if(!value.equalsIgnoreCase("all"))
                  allAllowed = false;
            }else if(attribute.equalsIgnoreCase("external")){
               if(!(value.equalsIgnoreCase("all") | value.equalsIgnoreCase("true")))
                  allExternal = false;
            }else if((attribute.equalsIgnoreCase("inherit") | attribute.equalsIgnoreCase("template"))){
               templates.add(value);
            }
         }catch(org.apache.avalon.framework.configuration.ConfigurationException ce){
            // TODO: Log Errors?
         }
      } // For each Attribute
      Configuration[] children = config.getChildren();
      for(int c = 0; c < children.length; c++){
         String element = children[c].getName();
         attrnames = children[c].getAttributeNames();
         if(element.equalsIgnoreCase("inherit")){
            for(i = 0; i < attrnames.length; i++){
               try{
                  templates.add(children[c].getAttribute(attrnames[i]));
               }catch(org.apache.avalon.framework.configuration.ConfigurationException ce){
                  // TODO: Log Errors?
               }
            }
            try{
               templates.add(children[c].getValue());
            }catch(org.apache.avalon.framework.configuration.ConfigurationException ce){
               // Error means no value
            }
         }else if(element.equalsIgnoreCase("module")){
            boolean isExternal = true;
            boolean isExcluded = false;
            String name = "";
            String template = "";
            String module = "";
            for(i = 0; i < attrnames.length; i++){
               String attribute = attrnames[i];
               try{
                  String value = children[c].getAttribute(attrnames[i]);
                  if(attribute.equalsIgnoreCase("external")){
                     if(!(value.equalsIgnoreCase("yes") | value.equalsIgnoreCase("true")))
                        isExternal = false;
                  }else if(attribute.equalsIgnoreCase("exclude")){
                     if(!(value.equalsIgnoreCase("yes") | value.equalsIgnoreCase("true")))
                        isExcluded = true;
                  }else if(attribute.equalsIgnoreCase("name")){
                     name = value;
                  }else if((attribute.equalsIgnoreCase("inherit") | attribute.equalsIgnoreCase("template"))){
                     template = value;
                  }else if(attribute.equalsIgnoreCase("module")){
                     module = value;
                  }
               }catch(org.apache.avalon.framework.configuration.ConfigurationException ce){
                  // TODO: Log Errors?
               }
            }
            if(name.length() > 0){
               if(isExcluded){
                  exclude.add(name);
               }else{
                  external.put(name, new Boolean(isExternal));
                  if(template.length() > 0)
                     inherit.put(name, template);
                  if(module.length() > 0)
                     inherit.put(name, module);
                  // Variables
                  Map varmap = getVariablesFromConfiguration(children[c]);
                  Iterator keys = varmap.keySet().iterator();
                  while(keys.hasNext()){
                     String key = (String) keys.next();
                     variables.put(publicationId + "." + name + "." + key, varmap.get(key));
                  }
               } // isExcluded
            } // Has name
         } // element tag
      } // for each child
   } // function
   public boolean isExternal(String moduleId) {
      if(exclude.contains(moduleId))
         return false;
      if(allExternal)
         return true;
      if(external.containsKey(moduleId))
         return ((Boolean) external.get(moduleId)).booleanValue();
      // TODO: Check parent templates?
      return allAllowed;
   }
   public boolean isAllowed(String moduleId) {
      if(exclude.contains(moduleId))
         return false;
      if(allAllowed)
         return true;
      if(external.containsKey(moduleId))
         return true;
      return false;
   }
   // public String[] getTemplates(String moduleId) {
   // if(inherit.containsKey(moduleId)){
   // ArrayList tmpArray = new ArrayList();
   // tmpArray.add(inherit.get(moduleId));
   // tmpArray.addAll(templates);
   // return (String[]) tmpArray.toArray(new String[0]);
   // }
   // return (String[]) templates.toArray(new String[0]);
   // }
   // public String getInheritedModule(String moduleId) {
   // if(modules.containsKey(moduleId))
   // return (String) modules.get(moduleId);
   // return moduleId;
   // }
   public String getVariable(String moduleId, String varname) {
      // TODO: Add reset. Do InputModules support double colon syntax?
      if(exclude.contains(moduleId))
         return "";
      if(variables.containsKey(publicationId + "." + moduleId + "." + varname)){
         return (String) variables.get(publicationId + "." + moduleId + "." + varname);
      }
      // Check Module Variables
      String ret = "";
      Module module = Modules.getModule(publicationId, moduleId, contentType);
      if(null != module){
         ret = module.getVariable(varname);
      }
      if(ret.length() < 1){
         // Check inheritance
         // NOTE: contentType remains of this Publication, not the Inherited Publication.
         Iterator iterator = templates.iterator();
         while(iterator.hasNext() && (ret.length() < 1)){
            String i_pubid = (String) iterator.next();
            // Inherited Publication variables
            if(variables.containsKey(i_pubid + "." + moduleId + "." + varname)){
               return (String) variables.get(i_pubid + "." + moduleId + "." + varname);
            }
            // Inherited Publication Module variables
            if(ret.length() < 1){
               module = Modules.getModule(i_pubid, moduleId, contentType);
               if(null != module){
                  ret = module.getVariable(varname);
               }
            }
         }
      }
      if(ret.length() < 1){
         // Check Global Module
         module = Modules.getModule("", moduleId, contentType);
         if(null != module){
            ret = module.getVariable(varname);
         }
      }
      if(ret.length() > 0){
         variables.put(publicationId + "." + moduleId + "." + varname, ret);
      }
      return ret;
   }
   public String getFile(String moduleId, String filename, boolean reset) {
      if(reset){
         files = new HashMap();
      }
      if(exclude.contains(moduleId))
         return "";
      if(files.containsKey(moduleId + "." + filename)){
         return (String) files.get(moduleId + "." + filename);
      }
      // Check Module Variables
      String ret = "";
      Module module = Modules.getModule(publicationId, moduleId, contentType);
      if(null != module){
         if(reset)
            module.resetFiles();
         ret = module.getFile(filename);
      }
      if(ret.length() < 1){
         // Check inheritance
         Iterator iterator = templates.iterator();
         while(iterator.hasNext() && (ret.length() < 1)){
            module = Modules.getModule((String) iterator.next(), moduleId, contentType);
            if(null != module){
               if(reset)
                  module.resetFiles();
               ret = module.getFile(filename);
            }
         }
      }
      if(ret.length() < 1){
         // Check Global Module
         module = Modules.getModule("", moduleId, contentType);
         if(null != module){
            if(reset)
               module.resetFiles();
            ret = module.getFile(filename);
         }
      }
      if(ret.length() > 0){
         files.put(moduleId + "." + filename, ret);
      }
      return ret;
   }
   private Map getVariablesFromConfiguration(Configuration config) {
      Map map = new HashMap();
      Configuration[] vchildren = config.getChildren();
      for(int v = 0; v < vchildren.length; v++){
         String velement = vchildren[v].getName();
         // Standardize on "parameter"?
         if(velement.equalsIgnoreCase("variable") | velement.equalsIgnoreCase("param") | velement.equalsIgnoreCase("parameter")){
            try{
               map.put(vchildren[v].getAttribute("name"), vchildren[v].getValue());
            }catch(org.apache.avalon.framework.configuration.ConfigurationException vce){
               // Do Nothing?
            } // try
         } // if variable
      } // for
      return map;
   }
   // private Publication getPublication() {
   // try{
   // return PublicationFactory.getPublication(publicationId, servletContextPath);
   // }catch(org.apache.lenya.cms.publication.PublicationException pe){
   // return (Publication) null;
   // }
   // }
}