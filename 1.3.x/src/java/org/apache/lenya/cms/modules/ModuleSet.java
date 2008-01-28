package org.apache.lenya.cms.modules;
import org.apache.lenya.cms.content.Content;
public class ModuleSet {
   Module flat = null;
   Module hierarchical = null;
   public ModuleSet() {
   }
   public Module getFlat() {
      return flat;
   }
   public Module getHierarchical() {
      return hierarchical;
   }
   public void add(Module module) {
      if(Content.TYPE_FLAT.equalsIgnoreCase(module.getType())){
         if(null == flat){
            flat = module;
         }else if(Content.TYPE_FLAT.equalsIgnoreCase(flat.getType())){
            flat = compareModules(flat, module);
         }else{
            flat = module;
         }
      }else if(Content.TYPE_HIERARCHICAL.equalsIgnoreCase(module.getType())){
         if(null == hierarchical){
            hierarchical = module;
         }else if(Content.TYPE_HIERARCHICAL.equalsIgnoreCase(hierarchical.getType())){
            hierarchical = compareModules(hierarchical, module);
         }else{
            hierarchical = module;
         }
      }else{
         if(null == flat)
            flat = module;
         if(null == hierarchical)
            hierarchical = module;
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
