package org.apache.lenya.cms.content.hierarchical;

import java.io.File;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.lenya.cms.content.Content;
import org.apache.lenya.cms.content.Resource;
import org.apache.lenya.cms.publication.Publication;

public class HierarchicalContent implements Content{
   File directory;
   String[] languages = { "en" };

   public HierarchicalContent(File pdirectory, String[] planguages){
      directory = pdirectory;
      languages = planguages;
   }
/* Content API */
   public String getIndexFilename(String indexName, String language){
     return new File(directory, indexName + File.separator + "sitetree.xml").getPath();
//WORK?  Filter by language and transform sitetree to index XML.
   }
   public String getUNID(String structure, String id){
      return structure + File.separator + id;
   }
   public String[] getLanguages(){
      return languages;
   }
   public String getNewFilename(String unid, String language){
      return new File(directory, "live" + File.separator + unid + File.separator + getDateString() + "_" + language +"." + ".xml").getPath();
   }

   private String getDateString(){
      return Long.toString(new java.util.Date().getTime());
   }
}
