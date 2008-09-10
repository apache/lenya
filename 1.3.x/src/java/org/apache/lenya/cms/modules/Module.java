package org.apache.lenya.cms.modules;
import java.io.File;
import java.io.FileFilter;
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
   public static final String INDEX_FILE_SUFFIX = ".index"; // Start with period.
   Set inheritList = new LinkedHashSet(); // String names of other Modules in this publication.
   Map requiredList = new TreeMap(); // required Modules: id = reason
   Map recommendedList = new TreeMap(); // recommended Modules: id = reason
   Map optionalList = new TreeMap(); // optional Modules: id = reason
   Map variables = new HashMap(); // nameString = valueString
   Map files; // filename -> actual location as String (Better as File or Source?)
   Map indexes = new HashMap(); // indexId -> Configuration
   File moduleDirectory;
   private String type = Content.TYPE_DEFAULT;
   private String id;
   private String name = "";
   private String minimum = "1.3";
   private String maximum = "";
   private String created = "1.3";
   private long modified = 1;
   private String resource = "";
   private String description = "";
   private String usage = "";
   private String publicationId = "";
   public Module(File moduleDirectory, String publicationId) {
      resetFiles();
      this.publicationId = publicationId;
      this.moduleDirectory = moduleDirectory;
      id = moduleDirectory.getName();
      File configFile = new File(moduleDirectory, MODULE_XML);
      DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
      Configuration config;
      if(configFile.canRead()){
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
            // Resource
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
            int numInherits = inherits.length;
            for(int vI = 0; vI < numInherits; vI++){
               Configuration inherit = inherits[vI];
               // Get attributes
               String publication = inherit.getAttribute("publication", publicationId);
               String module = inherit.getAttribute("id", id);
               String key = publication + "." + module;
               inheritList.add(key);
            }
            // Indexes
            Configuration[] indexesConf = config.getChildren("index");
            int numIndexes = indexesConf.length;
            for(int vI = 0; vI < numIndexes; vI++){
               Configuration index = indexesConf[vI];
               // Get attributes
               String indexId = index.getAttribute("id", "");
               // System.out.println("Module: Found Index " + indexId);
               if(0 < indexId.length()) indexes.put(indexId, index);
            }
            // Resource parents inherit after specified inheritance.
            // Added from bottom to top: xml/xhtml/home inherits from xhtml then xml.
            if(resource.length() > 0){
               // System.out.println("RESOURCE " + resource + "=" + id);
               String[] parents = resource.split("/");
               for(int p = parents.length - 1; p > -1; p--){
                  String parent = parents[p];
                  if((parent.length() > 0) && (!parent.equalsIgnoreCase(id))){
                     String key = publicationId + "." + parent;
                     if(!inheritList.contains(key)){
                        inheritList.add(key);
                     }
                  }
               }
            }
         }catch(ConfigurationException e){
            System.out.println("Module " + publicationId + "." + id + " has a ConfigurationException.\n" + e.getLocalizedMessage());
         }catch(SAXException e){
            System.out.println("Module " + publicationId + "." + id + " has a SAXException.\n" + e.getLocalizedMessage());
         }catch(IOException e){
            System.out.println("Module " + publicationId + "." + id + " has an IOException.\n" + e.getLocalizedMessage());
         }
      } // end if - read configuration
      IndexFileFilter filter = new IndexFileFilter();
      File[] indexFiles = moduleDirectory.listFiles(filter);
      int numIndexFiles = indexFiles.length;
      for(int vI = 0; vI < numIndexFiles; vI++){
         File indexFile = indexFiles[vI];
         // System.out.println("Module: Found Index File " + indexFile.getAbsolutePath());
         try{
            config = builder.buildFromFile(indexFile);
            String indexId = config.getAttribute("id", indexFile.getName().split(INDEX_FILE_SUFFIX, 1)[0]);
            if(1 > indexId.length()){
               String filename = indexFile.getName();
               indexId = filename.substring(0, filename.length() - INDEX_FILE_SUFFIX.length());
               indexes.put(indexId, config);
            }
         }catch(ConfigurationException e){
            System.out.println("Module " + publicationId + "." + id + " Index " + indexFile.getName() + " has a ConfigurationException.\n" + e.getLocalizedMessage());
         }catch(SAXException e){
            System.out.println("Module " + publicationId + "." + id + " Index " + indexFile.getName() + " has a SAXException.\n" + e.getLocalizedMessage());
         }catch(IOException e){
            System.out.println("Module " + publicationId + "." + id + " Index " + indexFile.getName() + " has an IOException.\n" + e.getLocalizedMessage());
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
      // System.out.println("Module.getFile " + filename);
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
      // TODO: Check Design Resources without creating infinite loop.
      // BUG WATCH: This function is called by FlatIndexer while initializing FlatContent. Infinite loop.
      // Initialization only needs filename="."
      // if((ret.length() < 1) && (0 < filename.length()) && !(".".equals(filename))){
      // // Check Design Resources
      // Publication publication = Globals.getPublication();
      // if(FlatContent.TYPE.equalsIgnoreCase(publication.getContentType())){
      // String unid = Globals.getDesignUnid(filename);
      // // Cannot access Content during initialization.
      // Resource resource = ((FlatContent) publication.getContent()).getDesignResource(unid);
      // File file = new File(resource.getURI());
      // if(file.exists()) ret = file.getAbsolutePath();
      // }
      // }
      if(ret.length() < 1){
         // Check this Module.
         File file = new File(moduleDirectory, filename);
         if(file.exists()){
            ret = file.getAbsolutePath();
         }
      }
      if(ret.length() < 1){
         // Check Module Inheritance
         Iterator inherit = inheritList.iterator();
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
         Iterator inherit = inheritList.iterator();
         while(inherit.hasNext() && (ret.length() < 1)){
            module = Modules.getModule((String) inherit.next(), type);
            if(null != module) ret = module.getVariable(parameterName);
         }
      }
      if(ret.length() > 0) variables.put(parameterName, ret);
      return ret;
   }
   /**
    * Package only. Do not allow Modules implementation to escape package.
    * 
    * @return publicationId.moduleId
    */
   String getKey() {
      return publicationId + "." + id;
   }
   public String getResource() {
      return resource;
   }
   public String getName() {
      return name;
   }
   public String getDescription() {
      return description;
   }
   public String getUsage() {
      return usage;
   }
   public String getPublicationId() {
      return publicationId;
   }
   /**
    * 
    * @return Map(indexId) = Configuration
    */
   public Map getIndexes() {
      return indexes;
   }
   private class IndexFileFilter implements FileFilter {
      public boolean accept(File file) {
         if(!file.canRead()) return false;
         return file.getName().toLowerCase().endsWith(INDEX_FILE_SUFFIX.toLowerCase());
      }
   }
}
