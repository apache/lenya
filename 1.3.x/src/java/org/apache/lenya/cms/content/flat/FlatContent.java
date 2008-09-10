package org.apache.lenya.cms.content.flat;
import java.io.File;
import java.util.Set;
import org.apache.lenya.cms.content.Content;
import org.apache.lenya.cms.content.Resource;
import org.apache.lenya.cms.modules.PublicationModules;
/**
 * 
 * @author solprovider
 * @since 1.3
 */
public class FlatContent implements Content {
   public static final String TYPE = "flat";
   public static final String LANGUAGE_DEFAULT = "en";
   static final String DIRECTORY_CONTENT = "resource";
   static final String RELATIONS_DEFAULT = "live";
   private File directoryResources;
   FlatIndexer indexer;
   String[] languages = {LANGUAGE_DEFAULT};
   FlatDesign design;
   public FlatContent(File directory, String[] languages, PublicationModules modules, FlatDesign design) {
      // System.out.println("FlatContent");
      directoryResources = new File(directory, DIRECTORY_CONTENT);
      this.languages = languages;
      indexer = new FlatIndexer(this, modules);
      this.design = design;
      // The next line starts indexing everything in the background when Lenya starts.
      updateIndexes();
   }
   /* Content API */
   public String getIndexFilename(String indexName, String language) {
      return indexer.getIndexFilename(indexName, language);
   }
   /* FlatContent API */
   public String[] getLanguages() {
      return languages;
   }
   public String getMetaURI(String unid, String language, String revision) {
      Resource resource = getResource(unid, language, revision);
      return resource.getMetaURI();
   }
   public String getNewURI(String unid, String language) {
      Resource resource = getResource(unid, language, "edit");
      return resource.getNewURI();
   }
   public FlatRelations getRelations(String structure) {
      // Note: Added design to constructor because getRelations is called by FlatIndex before Globals.getPublication is usable.
      // System.out.println("FlatContent.getRelations " + structure);
      if(null == design){
         System.out.println("FlatContent.getRelations: No Design. Using relation directory for " + structure);
      }else{
         Resource structureResource = design.getStructure(structure);
         File file = new File(structureResource.getURI());
         if(file.exists()){
            // System.out.println("FlatContent.getRelations: Found DesignResource for " + structure);
            return new FlatRelations(file);
         }
         // System.out.println("FlatContent.getRelations: No DesignResource. Using relation directory for " + structure);
      }
      // DEV: Needed for development with Structures in content/relation
      return new FlatRelations(new File(directoryResources.getParentFile(), "relation" + File.separator + structure + ".xml"));
   }
   public Resource getResource(String unid) {
      // System.out.println("FlatContent.getResource " + unid);
      return (Resource) new FlatResource(directoryResources, unid);
   }
   public Resource getResource(String unid, String language, String revision) {
      // System.out.println("FlatContent.getResource UNID=" + unid + " LANG=" + language + " REV=" + revision);
      return (Resource) new FlatResource(directoryResources, unid, language, revision);
   }
   public String[] getResources() {
      return directoryResources.list();
   }
   public String getUNID(String structure, String id) {
      // System.out.println("FlatContent.getUNID s=" + structure + " id='" + id + "'");
      if((0 == id.length()) || (0 == structure.length())) return "";
      FlatRelations relations = getRelations(structure);
      return relations.getUNID(id);
   }
   public String getURI(String unid, String language, String revision) {
      Resource resource = getResource(unid, language, revision);
      return resource.getURI();
   }
   /**
    * Updates Indexes in background.
    */
   void updateIndexes() {
      new Thread((Runnable) indexer).start();
   }
   public Set getStructures() {
      return indexer.getStructures();
   }
   public Set getRevisions() {
      return indexer.getRevisions();
   }
   public File getDirectory() {
      return directoryResources;
   }
}
