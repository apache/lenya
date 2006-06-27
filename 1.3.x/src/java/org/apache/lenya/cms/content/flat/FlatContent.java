package org.apache.lenya.cms.content.flat;

import java.io.File;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.lenya.cms.content.Content;
//import org.apache.lenya.cms.content.Indexer;
import org.apache.lenya.cms.content.Resource;
import org.apache.lenya.cms.publication.Publication;

public class FlatContent implements Content{
   File directory;
   FlatIndexer indexer;
   String[] languages = { "en" };

   public FlatContent(File pdirectory, String[] planguages){
      directory = pdirectory;
      languages = planguages;
      indexer = new FlatIndexer(new File(directory, "index"), this);
// The next line starts indexing everything in the background when Lenya starts.
      new Thread((Runnable) indexer).start();
   }
/* Content API */
   public String getIndexFilename(String indexName, String language){
     return indexer.getIndexFilename(indexName, language);
   }
   public String getURI(String unid, String language, String revision){
      Resource resource = getResource(unid, language, revision);
      return resource.getURI();
   }
   public String getMetaURI(String unid, String language, String revision){
      Resource resource = getResource(unid, language, revision);
      return resource.getMetaURI();
   }
   public String getNewURI(String unid, String language){

      Resource resource = getResource(unid, language, "live");
      return resource.getNewURI();
   }
   public String getUNID(String structure, String id){
      FlatRelations relations = getRelations(structure);
      return relations.getUNID(id);
   }
   public Resource getResource(String unid){
      return (Resource) new FlatResource(directory, unid);
   }
/* FlatContent API */
   public String[] getLanguages(){
      return languages;
   }
   public String[] getResources(){
      return (new File(directory, "resource")).list();
   }
   public Resource getResource(String unid, String language, String revision){
      return (Resource) new FlatResource(directory, unid, language, revision);
   }
   public FlatRelations getRelations(String structure){
     return new FlatRelations(new File(directory, "relation" + File.separator + structure + ".xml"));
   }
}
