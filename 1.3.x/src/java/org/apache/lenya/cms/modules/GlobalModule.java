package org.apache.lenya.cms.modules;

import java.io.File;
import java.util.Iterator;

public class GlobalModule extends Module {

	public GlobalModule(File moduleDirectory) {
		super(moduleDirectory, "");
	}
   /**
    * Retrieves the absolute path to the first file found following inheritance.
    * @param filename
    * @return
    */
   public String getFile(String filename){
      // Check cache
      if(files.containsKey(filename)) return (String) files.get(filename);
      // Check this Module.
      File file = new File(moduleDirectory, filename);
      if(file.exists()) return file.getAbsolutePath();
      // Check Module Inheritance
      Iterator inherit = inheritList.entrySet().iterator();
      String ret = "";
      while(inherit.hasNext() && (ret.length() < 1)){
         ret = Modules.getModule((String)inherit.next(), type).getFile(filename);
      }
      if(ret.length() > 0)   return ret;
      // Check Global Module using id.
      return Modules.getModule(id, type).getFile(filename);
   }
}
