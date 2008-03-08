package org.apache.lenya.cms.content.flat;
import java.io.File;
/**
 * FlatContent handles threading. This class could be nested in FlatContent.
 * 
 * @author solprovider
 * @since 1.3
 */
class FlatIndexer implements Runnable {
   // TODO: Build list of Index configurations from Modules. Move .xconf files to Modules. Change extension (to .icx, .xic, .index?)
   File indexDirectory;
   FlatContent content;
   private volatile boolean isRunning = false;
   private volatile boolean runAgain = false; //
   private Object lock = new Object(); // for locking synchronization.
   FlatIndexer(File pindexDirectory, FlatContent pcontent) {
      indexDirectory = pindexDirectory;
      content = pcontent;
   }
   public void run() {
      // Only the first thread updates. Additional requests set flag so the first thread will repeat the update.
      // runAgain cannot be false on later threads unless the Indexer is updating
      // If already queued, do nothing.
      if(runAgain){
         return;
      }
      runAgain = true;
      // Set to run, but return if already running,
      if(isRunning){
         return;
      }
      // synchronized so two threads do not conflict if both believe are first thread.
      synchronized (lock){
         isRunning = true;
         // Run. Run again if another request was queued.
         while(runAgain){
            // runAgain is only set to false just before an update
            runAgain = false;
            update();
         }
         isRunning = false;
      }
   }
   /**
    * Updates all Indexes.
    */
   void update() {
      System.out.println("Starting Indexer for " + indexDirectory.getAbsolutePath());
      updateIndexAll();
      if(!indexDirectory.exists()) return;
      // System.out.println("Starting Indexer UpdateAll");
      String[] files = indexDirectory.list();
      // System.out.println("FlatIndexer.updateAll ID=" + indexDirectory.getPath());
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
   }
   /**
    * Updates all Indexes for one language.
    * 
    * @param language
    */
   void update(String language) {
      updateIndexAll();
      if(!indexDirectory.exists()) return;
      String[] files = indexDirectory.list();
      if(null == files) return;
      int filesLength = files.length;
      for(int f = 0; f < filesLength; f++){
         String filename = files[f];
         if(filename.endsWith(".xconf")){
            String indexName = filename.substring(0, filename.lastIndexOf(".xconf"));
            FlatIndex index = new FlatIndex(content, indexDirectory, indexName, language);
            index.update();
         }
      }
   }
   private void updateIndexAll() {
      // Update the all Resources index (ignores Translations and Revisions).
      FlatIndex index = new FlatIndex(content, indexDirectory, "", "");
      index.update();
   }
   /**
    * 
    * @param indexName
    * @param language
    * @return filename of specified Index
    */
   public String getIndexFilename(String indexName, String language) {
      return (new FlatIndex(content, indexDirectory, indexName, language)).getIndexFilename();
   }
}
