package org.apache.lenya.cms.content.flat;

import java.io.File;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.lenya.cms.content.Content;
import org.apache.lenya.cms.content.Resource;

public class FlatIndexer implements Runnable {
   File indexDirectory;
   FlatContent content;
   boolean isCurrent = false;

   public FlatIndexer(File pindexDirectory, FlatContent pcontent){
      indexDirectory = pindexDirectory;
      content = pcontent;
   }

   public void run(){
      updateAll();
   }

   public void updateAll(){
      if(isCurrent) return;
      if(!indexDirectory.exists()) return;
//System.out.println("Starting Indexer UpdateAll");
      String[] files = indexDirectory.list();
//System.out.println("FI-uA: iD=" + indexDirectory.getPath());
      if(null == files) return;
      int filesLength = files.length;
      for(int f = 0; f < filesLength; f++){
         String filename = files[f];
         if(filename.endsWith(".xconf")){
            String indexName = filename.substring(0, filename.lastIndexOf(".xconf"));
            String[] languages = content.getLanguages();
            int languagesLength = languages.length;
            for(int l = 0; l < languagesLength; l++){
               String language = languages[l];
               FlatIndex index = new FlatIndex(content, indexDirectory, indexName, language);
               index.update();
            }
         }
      }
      isCurrent = true;
   }
   public String getIndexFilename(String indexName, String language){
      return (new FlatIndex(content, indexDirectory, indexName, language)).getIndexFilename();
   }
}
