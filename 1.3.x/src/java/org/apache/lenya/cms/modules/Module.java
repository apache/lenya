package org.apache.lenya.cms.modules;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.lenya.cms.content.Content;
import org.xml.sax.SAXException;
/**
 * @author solprovider
 * @since 1.3
 */
public abstract class Module {
   public static final String MODULE_XML = "module.xml";
   Map inheritList; // String names of other Modules in this publication.
   Map requiredList; // required Modules: id = reason
   Map recommendedList; // recommended Modules: id = reason
   Map optionalList; // optional Modules: id = reason
   Map variables = new HashMap(); // nameString = valueString
   Map files; // filename -> actual location as String (Better as File or Source?)
   File moduleDirectory;
   private String type = Content.TYPE_DEFAULT;
   String id;
   String name;
   String minimum = "1.3";
   String maximum = "";
   String created = "1.3";
   long modified = 1;
   String resource = "";
   String description = "";
   String usage = "";
   public Module(File moduleDirectory, String publicationId) {
      resetFiles();
      String pubIdError = (publicationId.length() > 1 ? publicationId + " - " : "");
      this.moduleDirectory = moduleDirectory;
      id = moduleDirectory.getName();
      File configFile = new File(moduleDirectory, MODULE_XML);
      if(configFile.canRead()){
         DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
         Configuration config;
         try{
            config = builder.buildFromFile(configFile);
            id = config.getAttribute("id", id);
            name = config.getAttribute("name", id);
            minimum = config.getAttribute("minimum", minimum);
            maximum = config.getAttribute("maximum", maximum);
            created = config.getAttribute("created", created);
            type = config.getAttribute("content", type);
            try{
               modified = Long.parseLong(config.getAttribute("modified", "" + modified));
            }catch(NumberFormatException nfe){
               modified = 1;
            }
            resource = config.getAttribute("resource", "");
            if((resource.length() > 0) && !resource.endsWith("/" + id)){
               if(resource.endsWith("/")){
                  resource += id;
               }else{
                  resource += "/" + id;
               }
            }
            // Description
            Configuration descriptionConf = config.getChild("description");
            description = descriptionConf.getValue("");
            // Usage
            Configuration usageConf = config.getChild("usage");
            usage = usageConf.getValue("");
            // Required
            Configuration[] requireds = config.getChildren("required");
            requiredList = new TreeMap();
            int numRequireds = requireds.length;
            for(int vR = 0; vR < numRequireds; vR++){
               Configuration required = requireds[vR];
               // Get attributes
               String id = required.getAttribute("id", "");
               String reason = required.getValue("");
               requiredList.put(id, reason);
            }
            // Recommended
            Configuration[] recommendeds = config.getChildren("recommended");
            recommendedList = new TreeMap();
            int numrecommendeds = recommendeds.length;
            for(int vR = 0; vR < numrecommendeds; vR++){
               Configuration recommended = recommendeds[vR];
               // Get attributes
               String id = recommended.getAttribute("id", "");
               String reason = recommended.getValue("");
               recommendedList.put(id, reason);
            }
            // Optional
            Configuration[] optionals = config.getChildren("optional");
            optionalList = new TreeMap();
            int numoptionals = optionals.length;
            for(int vR = 0; vR < numoptionals; vR++){
               Configuration optional = optionals[vR];
               // Get attributes
               String id = optional.getAttribute("id", "");
               String reason = optional.getValue("");
               optionalList.put(id, reason);
            }
            // Parameters
            Configuration[] parameters = config.getChildren("parameter");
            int numparameters = parameters.length;
            for(int vR = 0; vR < numparameters; vR++){
               Configuration optional = parameters[vR];
               // Get attributes
               String name = optional.getAttribute("name", "");
               String value = optional.getValue("");
               variables.put(name, value);
            }
            // Inherits
            Configuration[] inherits = config.getChildren("inherit");
            inheritList = new TreeMap();
            int numInherits = inherits.length;
            for(int vI = 0; vI < numInherits; vI++){
               Configuration inherit = inherits[vI];
               // Get attributes
               String priority = inherit.getAttribute("priority", "");
               String publication = inherit.getAttribute("publication", "");
               String id = inherit.getAttribute("id", "");
               // Massage
               // TODO: Cheap shortcuts. Rewrite inheritList storage later.
               int priorityLength = priority.length();
               String priorityPrefix = priorityLength > 3 ? priority : "000" + priority.substring(priorityLength);
               String publicationPrefix = publication.length() > 0 ? publication + "." : publicationId + ".";
               inheritList.put(priorityPrefix + "." + publicationPrefix + id, publicationPrefix + id);
            }
         }catch(ConfigurationException e){
            System.out.println("Module " + pubIdError + id + " has a ConfigurationException.\n" + e.getLocalizedMessage());
         }catch(SAXException e){
            System.out.println("Module " + pubIdError + id + " has a SAXException.\n" + e.getLocalizedMessage());
         }catch(IOException e){
            System.out.println("Module " + pubIdError + id + " has an IOException.\n" + e.getLocalizedMessage());
         }
      }
   }
   public String getContentType() {
      return type;
   }
   public int getModified() {
      // TODO: Use date?
      return 0;
   }
   public String getId() {
      return id;
   }
   public Set getRequired() {
      Set set = new LinkedHashSet();
      set.addAll(requiredList.keySet());
      StringTokenizer tokens = new StringTokenizer(resource, "/");
      while(tokens.hasMoreTokens())
         set.add(tokens.nextToken());
      return set;
   }
   public void resetFiles() {
      files = new HashMap();
   }
   /**
    * Retrieves the absolute path to the first file found following inheritance. Does not check Global for Publication Modules. Used by publication.PublicationModules.
    * 
    * @param filename
    * @return
    */
   public String getFile(String filename) {
      String ret = "";
      // Check cache
      if(files.containsKey(filename)){
         ret = (String) files.get(filename);
         // Check if cached filename still exists?
         File file = new File(ret);
         if(!file.exists()){
            ret = "";
         }
      }
      if(ret.length() < 1){
         // Check this Module.
         File file = new File(moduleDirectory, filename);
         if(file.exists()){
            ret = file.getAbsolutePath();
         }
      }
      if(ret.length() < 1){
         // Check Module Inheritance
         Iterator inherit = inheritList.entrySet().iterator();
         while(inherit.hasNext() && (ret.length() < 1)){
            ret = Modules.getModule((String) inherit.next(), type).getFile(filename);
         }
      }
      if(ret.length() > 0){
         files.put(filename, ret);
      }
      return ret;
   }
   /**
    * Retrieves parameter set in this module and specified inheritance. Does not check Global for Publication Modules. Used by publication.PublicationModules.
    * 
    * @param parameterName
    * @return
    */
   public String getVariable(String parameterName) {
      String ret = "";
      // Check cache
      if(variables.containsKey(parameterName)){
         ret = (String) variables.get(parameterName);
      }else{
         Module module;
         // Check Module Inheritance
         Iterator inherit = inheritList.entrySet().iterator();
         while(inherit.hasNext() && (ret.length() < 1)){
            module = Modules.getModule((String) inherit.next(), type);
            if(null != module)
               ret = module.getVariable(parameterName);
         }
      }
      if(ret.length() > 0)
         variables.put(parameterName, ret);
      return ret;
   }
}
