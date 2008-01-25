package org.apache.lenya.cms.modules;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.cocoon.util.IOUtils;
/**
 * Singleton class containing all Modules.
 * 
 * @author solprovider
 */
public class Modules {
   static Map globalModules;
   static Map publicationModules;
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
      globalModules = new HashMap();
      publicationModules = new HashMap();
      // Global Modules
      File globalModulesPath = new File(servletContextPath, "lenya" + File.separator + "modules");
      File[] moduleDirectories = globalModulesPath.listFiles();
      if(null != moduleDirectories){
         int mdLength = moduleDirectories.length;
         for(int md = 0; md < mdLength; md++){
            File moduleDirectory = moduleDirectories[md];
            if(moduleDirectory.isDirectory()){
               Module newModule = new GlobalModule(moduleDirectory);
               String id = newModule.getId();
               if(globalModules.containsKey(id)){
                  Object o = globalModules.get(id);
                  if(Module.class.isAssignableFrom(o.getClass())){
                     Module oldModule = (Module) o;
                     ModuleSet moduleSet = new ModuleSet();
                     moduleSet.add(oldModule);
                     moduleSet.add(newModule);
                     globalModules.put(id, moduleSet);
                  }else if(ModuleSet.class.isAssignableFrom(o.getClass())){
                     ModuleSet moduleSet = (ModuleSet) o;
                     moduleSet.add(newModule);
                     globalModules.put(id, moduleSet);
                  }else{
                     System.out.println("GlobalModules: Found class " + o.getClass().toString());
                  }
               }else{
                  globalModules.put(id, newModule);
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
                     Module newModule = new PublicationModule(moduleDirectory, pubid);
                     String id = pubid + "." + newModule.getId();
                     if(publicationModules.containsKey(id)){
                        Object o = publicationModules.get(id);
                        if(Module.class.isAssignableFrom(o.getClass())){
                           Module oldModule = (Module) o;
                           ModuleSet moduleSet = new ModuleSet();
                           moduleSet.add(oldModule);
                           moduleSet.add(newModule);
                           publicationModules.put(id, moduleSet);
                        }else if(ModuleSet.class.isAssignableFrom(o.getClass())){
                           ModuleSet moduleSet = (ModuleSet) o;
                           moduleSet.add(newModule);
                           publicationModules.put(id, moduleSet);
                        }else{
                           System.out.println("PublicationModules: Found class " + o.getClass().toString());
                        }
                     }else{
                        publicationModules.put(id, newModule);
                     }
                  }
               }
            } // END Publication
         }
      } // END Publication Modules
      isInitialized = true;
   }
   static public Module getModule(String id, String type) {
      while(!isInitialized)
         try{
            Thread.currentThread().wait(1000);
         }catch(InterruptedException e){
            e.printStackTrace();
         }
      if(publicationModules.containsKey(id))
         return getModuleByType(publicationModules.get(id), type);
      if(globalModules.containsKey(id))
         return getModuleByType(globalModules.get(id), type);
      return null;
   }
   static private Module getModuleByType(Object o, String type) {
      if(Module.class.isAssignableFrom(o.getClass())){
         return (Module) o;
      }else if(ModuleSet.class.isAssignableFrom(o.getClass())){
         if(Module.TYPE_HIERARCHICAL.equals(type)){
            return (Module) ((ModuleSet) o).getHierarchical();
         }else{
            return (Module) ((ModuleSet) o).getFlat();
         }
      }
      return null;
   }
}
