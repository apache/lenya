package org.apache.lenya.cms.publication;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;

//TODO: Rearchitect so can reset all without restarting Lenya?

public class Modules {
   private ArrayList templates   = new ArrayList(); // Default list of Publications for inherit.
//TODO: external and exclude should have defaults in module.xml.  
//Current algorithm needs replacing.  
//Use TRUE, FALSE, UNSET rather than Boolean.
//Change exclude to Map.
   private Map external    = new HashMap();   // Every Module in Publication with Boolean.
   private Set exclude     = new HashSet();   // Only Modules excluded.
   private Map inherit     = new HashMap();   // Only Modules overriding inherit.
   private Map modules = new HashMap();   // Only Modules overriding inherit with new moduleID.
   boolean allAllowed  = true;
   boolean allExternal = true;
   static Map variables = new HashMap();  //publication.module.variablename = value from publication.xconf
   static Map defaultvariables = new HashMap();  //publication.module.variablename = value from module.xml
   static Map globalvariables = new HashMap(); //module.variable = value from global module.xml
   static boolean isGlobalvariablesSet = false;
   private String servletContextPath;

//Dev testing
   Configuration config;

   public Modules(String publication, String servletContextPath, Configuration config){
      if(null == config) return;
      int i;
      this.servletContextPath = servletContextPath;
      this.config = config;  //TESTING
      
      String[] attrnames = config.getAttributeNames();
      for(i = 0; i < attrnames.length; i++ ) {
         try{
            String attribute = attrnames[i];
            String value = config.getAttribute(attrnames[i]);
            if(attribute.equalsIgnoreCase("include")){
               if(!value.equalsIgnoreCase("all")) allAllowed = false;
            }else if(attribute.equalsIgnoreCase("external")){
               if(!(value.equalsIgnoreCase("all") | value.equalsIgnoreCase("true"))) allExternal = false;
            }else if((attribute.equalsIgnoreCase("inherit")|attribute.equalsIgnoreCase("template"))){
               templates.add(value);
            }
         }catch(org.apache.avalon.framework.configuration.ConfigurationException ce){ 
            //TODO: Log Errors?
         }
      }  //For each Attribute
      Configuration[] children = config.getChildren();
      for(int c = 0; c < children.length; c++ ) {
         String element = children[c].getName();
         attrnames = children[c].getAttributeNames();
         if(element.equalsIgnoreCase("inherit")){
            for(i = 0; i < attrnames.length; i++ ) {
               try{
                  templates.add(children[c].getAttribute(attrnames[i]));
               }catch(org.apache.avalon.framework.configuration.ConfigurationException ce){ 
                  //TODO: Log Errors?
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
             for(i = 0; i < attrnames.length; i++ ) {
                String attribute = attrnames[i];
                try{
                   String value = children[c].getAttribute(attrnames[i]);
                   if(attribute.equalsIgnoreCase("external")){
                      if(!(value.equalsIgnoreCase("yes") | value.equalsIgnoreCase("true"))) isExternal = false;
                   }else if(attribute.equalsIgnoreCase("exclude")){
                      if(!(value.equalsIgnoreCase("yes") | value.equalsIgnoreCase("true"))) isExcluded = true;
                   }else if(attribute.equalsIgnoreCase("name")){
                      name = value;
                   }else if((attribute.equalsIgnoreCase("inherit")|attribute.equalsIgnoreCase("template"))){
                      template = value;
                   }else if(attribute.equalsIgnoreCase("module")){
                      module = value;
                   }
               }catch(org.apache.avalon.framework.configuration.ConfigurationException ce){ 
                  //TODO: Log Errors?
               }
             }
             if(name.length() > 0){
                if(isExcluded){
                   exclude.add(name);
                }else{
                   external.put(name, new Boolean(isExternal));
                   if(template.length() > 0) inherit.put(name, template);
                   if(module.length() > 0) inherit.put(name, module);
                   //Variables
                   Map varmap = getVariablesFromConfiguration(children[c]);
                   Iterator keys = varmap.keySet().iterator();
                   while(keys.hasNext()){
                      String key = (String) keys.next();
                      variables.put(publication + "." + name + "." + key, varmap.get(key));
                   }
/* OBSOLETE
                   Configuration[] vchildren = children[c].getChildren();
                   for(int v = 0; v < vchildren.length; v++ ) {
                      String velement = vchildren[v].getName();
                      if(velement.equalsIgnoreCase("variable")|velement.equalsIgnoreCase("param")|
                            velement.equalsIgnoreCase("parameter")){
                         try{
                            String vname = vchildren[v].getAttribute("name");
                            String vvalue = vchildren[v].getValue();
                            variables.put(publication + "." + name + "." + vname, vvalue);
                         }catch(org.apache.avalon.framework.configuration.ConfigurationException vce){
                            // Do Nothing?
                         }  //try
                      }  // if variable
                   } //for
OBSOLETE - END */
               } // isExcluded
            } // Has name
         } //element tag
      } // for each child
      // Get variables from module.xml if each is not set
      loadModuleVariables(publication);
      loadGlobalVariables();
   }  // function
   public boolean isExternal(String moduleID){
      if(exclude.contains(moduleID)) return false;
      if(allExternal) return true;
      if(external.containsKey(moduleID)) return ((Boolean) external.get(moduleID)).booleanValue();
      //TODO: Check parent templates?
      return allAllowed;
   }

   public boolean isAllowed(String moduleID){
      if(exclude.contains(moduleID)) return false;
      if(allAllowed) return true;
      if(external.containsKey(moduleID)) return true;
      return false;
   }
   public String[] getTemplates(String moduleId){
      if(inherit.containsKey(moduleId)){ 
         ArrayList tmpArray = new ArrayList();
         tmpArray.add(inherit.get(moduleId));
         tmpArray.addAll(templates);
         return (String[]) tmpArray.toArray(new String[0]);
      }
      return (String[]) templates.toArray(new String[0]);
   }
   public String getInheritedModule(String moduleId){
      if(modules.containsKey(moduleId)) return (String) modules.get(moduleId);
      return moduleId; 
   }

   public String getVariable(String publication, String module, String varname){
      String moduleID = module;
      if(exclude.contains(moduleID)) return "";
      if(variables.containsKey(publication + "." + moduleID + "." + varname)){
         return (String) variables.get(publication + "." + moduleID + "." + varname);
      }
//TODO: ModuleID could change with inheritance.  Use inherit.
      String[] templates = getTemplates(moduleID);
      for(int i = 0; i < templates.length; i++) {
         if(variables.containsKey(templates[i] + "." + moduleID + "." + varname)){
               return (String) variables.get(templates[i] + "." + moduleID + "." + varname);
         }
      }
      moduleID = module;
      //Check Module Variables
      if(defaultvariables.containsKey(publication + "." + moduleID + "." + varname)){
         return (String) defaultvariables.get(publication + "." + moduleID + "." + varname);
      }
//TODO: ModuleID could change with inheritance.  Use inherit.
      for(int i = 0; i < templates.length; i++) {
         if(defaultvariables.containsKey(templates[i] + "." + moduleID + "." + varname)){
               return (String) defaultvariables.get(templates[i] + "." + moduleID + "." + varname);
         }
      }
      //Check Global Variables
      //ModuleID could change with inheritance.  Only check highest inheritance, not current module.
      if(globalvariables.containsKey(moduleID + "." + varname)){
         return (String) globalvariables.get(moduleID + "." + varname);
      }
      //Default
      return "";
   }

   public void loadModuleVariables(String publication){
      File directory = new File(servletContextPath, Publication.PUBLICATION_PREFIX 
            + File.separator + publication + File.separator + "modules");
      //For each directory in pub/modules, check directory/module.xml
      if(directory.exists() && directory.isDirectory()){
         String[] files = directory.list();
         for(int f = 0; f < files.length; f++) {
            File moduledirectory = new File(directory, files[f]);
            if(moduledirectory.exists() && moduledirectory.isDirectory()){
               File modulexml = new File(moduledirectory, "module.xml");
               if(modulexml.exists()){
                  Map varmap = getModuleXMLValues(modulexml);
                  Iterator keys = varmap.keySet().iterator();
                  while(keys.hasNext()){
                     String key = (String) keys.next();
                     defaultvariables.put(publication + "." + files[f] + "." + key, varmap.get(key));             
System.out.println("Default:"+ publication + "." + files[f] + "." + key +"="+ varmap.get(key));             
                  }
               }
            }
         }
      }
   }

   public void loadGlobalVariables(){
      if(!isGlobalvariablesSet){
         File directory = new File(servletContextPath, "lenya" + File.separator + "modules");
//For each directory in lenya/modules, check directory/module.xml
         if(directory.exists() && directory.isDirectory()){
            String[] files = directory.list();
            for(int f = 0; f < files.length; f++) {
               File moduledirectory = new File(directory, files[f]);
               if(moduledirectory.exists() && moduledirectory.isDirectory()){
                  File modulexml = new File(moduledirectory, "module.xml");
                  if(modulexml.exists()){
                     Map varmap = getModuleXMLValues(modulexml);
                     Iterator keys = varmap.keySet().iterator();
                     while(keys.hasNext()){
                        String key = (String) keys.next();
                        globalvariables.put(files[f] + "." + key, varmap.get(key));             
                     }
                  }
               }
            }
         }
         //Finish
         isGlobalvariablesSet = true;
      }
   }
   private Map getModuleXMLValues(File modulexml){
         DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
      try{
         Configuration config = builder.buildFromFile(modulexml);
//TEST: Is top level?
         return getVariablesFromConfiguration(config);
      }catch(org.xml.sax.SAXException saxe){
      }catch(java.io.IOException ioe){
      }catch(org.apache.avalon.framework.configuration.ConfigurationException ce){
      }
      return new HashMap();
   }
   private Map getVariablesFromConfiguration(Configuration config){
      Map map = new HashMap();
      Configuration[] vchildren = config.getChildren();
      for(int v = 0; v < vchildren.length; v++ ) {
         String velement = vchildren[v].getName();
//Standardize on "parameter"?
         if(velement.equalsIgnoreCase("variable")|velement.equalsIgnoreCase("param")
               |velement.equalsIgnoreCase("parameter")){
            try{
               map.put(vchildren[v].getAttribute("name"), vchildren[v].getValue());
            }catch(org.apache.avalon.framework.configuration.ConfigurationException vce){
               // Do Nothing?
            }  //try
         }  // if variable
      } //for
      return map;
   }

   private Publication getPublication(String publication){
      try{
         return PublicationFactory.getPublication(publication, servletContextPath);
      }catch(org.apache.lenya.cms.publication.PublicationException pe){
         return (Publication) null;
      }
   }
}