package org.apache.lenya.cms.modules;
import java.io.File;
public class ModulePublication extends Module {
   String publication;
   public ModulePublication(File moduleDirectory, String publicationId) {
      super(moduleDirectory, publicationId);
      publication = publicationId;
   }
}
