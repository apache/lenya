package org.apache.lenya.cms.modules;
import java.util.HashMap;
import java.util.Map;
import org.apache.lenya.cms.content.Content;
/**
 * 
 * @author solprovider
 * @since 1.3
 */
public class ModuleSet {
   // Rewritten to use Map(type) = Module. Needed to handle >2 Types.
   // DESIGN: Remove types because will cause problems with inheritance.
   Map modules = new HashMap();
   String defaultType;
   public ModuleSet(Module module) {
      String type = module.getContentType();
      modules.put(type, module);
      defaultType = type;
   }
   public Module getModule(String type) {
      if(modules.containsKey(type)){
         return (Module) modules.get(type);
      }
      return (Module) modules.get(defaultType);
   }
   public void add(Module module) {
      String type = module.getContentType();
      if(modules.containsKey(type)){
         modules.put(type, compareModules((Module) modules.get(type), module));
      }else{
         modules.put(type, module);
         if(Content.TYPE_DEFAULT.equalsIgnoreCase(type)){
            defaultType = Content.TYPE_DEFAULT;
         }
      }
   }
   private Module compareModules(Module m1, Module m2) {
      if(m1 == null)
         return m2;
      if(m2 == null)
         return m1;
      if(m1.getModified() < m2.getModified())
         return m2;
      return m1;
   }
}
