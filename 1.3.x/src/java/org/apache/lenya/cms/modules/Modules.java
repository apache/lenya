package org.apache.lenya.cms.modules;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.cocoon.util.IOUtils;
import org.apache.lenya.cms.content.Content;
/**
 * Singleton class containing all Modules.
 * 
 * @author solprovider
 */
public class Modules {
   static Map modules;
   static String servletContextPath;
   private static volatile boolean isInitialized = false;
   private Modules() {
   }
   static{
      // System.out.println("Modules: Loading...");
      // TODO: Need better getServletContextPath()
      servletContextPath = IOUtils.getFullFilename(new File("build" + File.separator + "lenya" + File.separator + "webapp"));
      initialize();
   }
   private static synchronized void initialize() {
      modules = new HashMap();
      // Global Modules
      File globalModulesPath = new File(servletContextPath, "lenya" + File.separator + "modules");
      File[] moduleDirectories = globalModulesPath.listFiles();
      if(null != moduleDirectories){
         int mdLength = moduleDirectories.length;
         for(int md = 0; md < mdLength; md++){
            File moduleDirectory = moduleDirectories[md];
            if(moduleDirectory.isDirectory()){
               Module newModule = new ModuleGlobal(moduleDirectory);
               String id = "." + newModule.getId(); // GlobalModules' IDs start with '.'
               if(modules.containsKey(id)){
                  Object o = modules.get(id);
                  if(Module.class.isAssignableFrom(o.getClass())){
                     Module oldModule = (Module) o;
                     ModuleSet moduleSet = new ModuleSet();
                     moduleSet.add(oldModule);
                     moduleSet.add(newModule);
                     modules.put(id, moduleSet);
                  }else if(ModuleSet.class.isAssignableFrom(o.getClass())){
                     ModuleSet moduleSet = (ModuleSet) o;
                     moduleSet.add(newModule);
                     modules.put(id, moduleSet);
                  }else{
                     System.out.println("GlobalModules: Found class " + o.getClass().toString());
                  }
               }else{
                  modules.put(id, newModule);
               }
            }
         }
      } // END Global Modules
      // Publication Modules
      File publicationsPath = new File(servletContextPath, "lenya" + File.separator + "pubs");
      File[] publicationDirectories = publicationsPath.listFiles();
      int pdLength = publicationDirectories.length;
      for(int pd = 0; pd < pdLength; pd++){
         File publicationDirectory = publicationDirectories[pd];
         if(publicationDirectory.isDirectory()){
            // Publication
            String pubid = publicationDirectory.getName();
            File publicationModulesPath = new File(publicationDirectory, "modules");
            File[] publicationModuleDirectories = publicationModulesPath.listFiles();
            if(null != publicationModuleDirectories){
               int pmdLength = publicationModuleDirectories.length;
               for(int md = 0; md < pmdLength; md++){
                  File moduleDirectory = publicationModuleDirectories[md];
                  if(moduleDirectory.isDirectory()){
                     Module newModule = new ModulePublication(moduleDirectory, pubid);
                     String id = pubid + "." + newModule.getId();
                     if(modules.containsKey(id)){
                        Object o = modules.get(id);
                        if(Module.class.isAssignableFrom(o.getClass())){
                           Module oldModule = (Module) o;
                           ModuleSet moduleSet = new ModuleSet();
                           moduleSet.add(oldModule);
                           moduleSet.add(newModule);
                           modules.put(id, moduleSet);
                        }else if(ModuleSet.class.isAssignableFrom(o.getClass())){
                           ModuleSet moduleSet = (ModuleSet) o;
                           moduleSet.add(newModule);
                           modules.put(id, moduleSet);
                        }else{
                           System.out.println("PublicationModules: Found class " + o.getClass().toString());
                        }
                     }else{
                        modules.put(id, newModule);
                     }
                  }
               }
            } // END Publication
         }
      } // END Publication Modules
      isInitialized = true;
      // TESTING
      // Set keyset = new TreeSet(modules.keySet());
      // Iterator iterator = keyset.iterator();
      // while(iterator.hasNext()){
      // System.out.println("MODULE=" + (String) iterator.next());
      // }
   }
   static public Module getModule(String publicationId, String moduleId, String contentType) {
      return(getModule(publicationId + "." + moduleId, contentType));
   }
   /**
    * Retrieves Module using formatted id. Dangerous outside this package -- creates confusion with 3 parameter function. Not private -- needed by Module.
    * 
    * @param id
    *           PublicationID.ModuleID or ModuleID for Global Modules
    * @param contentType
    *           See Module Constant: TYPE_FLAT, TYPE_HIERARCHICAL
    * @return
    */
   static Module getModule(String id, String contentType) {
      while(!isInitialized){
         try{
            Thread.currentThread().wait(1000);
         }catch(InterruptedException e){
            e.printStackTrace();
         }
      }
      if(modules.containsKey(id))
         return getModuleByType(modules.get(id), contentType);
      return null;
   }
   static private Module getModuleByType(Object o, String type) {
      if(Module.class.isAssignableFrom(o.getClass())){
         // System.out.println("GetModuleByType Module");
         return (Module) o;
      }else if(ModuleSet.class.isAssignableFrom(o.getClass())){
         if(Content.TYPE_HIERARCHICAL.equals(type)){
            // System.out.println("GetModuleByType Hierarchical");
            return (Module) ((ModuleSet) o).getHierarchical();
         }else{
            // System.out.println("GetModuleByType Flat");
            return (Module) ((ModuleSet) o).getFlat();
         }
      }
      // System.out.println("GetModuleByType Null");
      return null;
   }
}
