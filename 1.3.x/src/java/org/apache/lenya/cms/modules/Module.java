package org.apache.lenya.cms.modules;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.xml.sax.SAXException;
abstract class Module {
   public static final String TYPE_FLAT = "flat";
   public static final String TYPE_HIERARCHICAL = "hierarchical";
   public static final String MODULE_XML = "module.xml";
   Map inheritList; // String names of other Modules in this publication.
   Map requiredList; // required Modules: id = reason
   Map recommendedList; // recommended Modules: id = reason
   Map optionalList; // optional Modules: id = reason
   Map variables; // String
   Map files; // filename -> actual location as String (Better as File or
   // Source?)
   File moduleDirectory;
   String type = TYPE_HIERARCHICAL;
   String id;
   String name;
   String minimum = "1.3";
   String maximum = "";
   String created = "1.3";
   String content = "all";
   String modified = "1.3";
   String resource = "";
   String description = "";
   String usage = "";
   public Module(File moduleDirectory, String publicationId) {
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
            content = config.getAttribute("content", content);
            modified = config.getAttribute("modified", modified);
            resource = config.getAttribute("resource", "");
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
               String publicationPrefix = publication.length() > 0 ? publication + "." : "";
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
   public String getType() {
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
   abstract public String getFile(String filename);
}
