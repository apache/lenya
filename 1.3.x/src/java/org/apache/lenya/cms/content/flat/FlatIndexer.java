package org.apache.lenya.cms.content.flat;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.lenya.cms.modules.Module;
import org.apache.lenya.cms.modules.PublicationModules;
/**
 * FlatContent handles threading. This class could be nested in FlatContent.
 * 
 * @author solprovider
 * @since 1.3
 */
class FlatIndexer implements Runnable {
   /**
    * @deprecated indexDirectory was used for loading indexes from one directory.
    */
   File indexDirectory;
   FlatContent content;
   FlatIndex allIndex;
   Map indexes = new HashMap();
   Set structures = new TreeSet();
   Set revisions = new TreeSet();
   private volatile boolean isRunning = false;
   private volatile boolean runAgain = false; //
   private Object lock = new Object(); // for locking synchronization.
   /**
    * Loads configuration from PublicationModules.
    * 
    * @param content
    * @param modules
    * 
    */
   FlatIndexer(FlatContent content, PublicationModules modules) {
      // System.out.println("FlatIndexer");
      revisions.add("live");
      revisions.add("edit");
      this.content = content;
      loadIndexes(modules.getSourceModules());
   }
   /**
    * 
    * @param modules -
    *           Map(?) = Module
    */
   private void loadIndexes(Map modules) {
      allIndex = new FlatIndex(content);
      // TODO: Prioritize indexes? Currently just overwrites duplicate without thought.
      Map indexConfigurations = new HashMap();
      Iterator iterator = modules.entrySet().iterator();
      while(iterator.hasNext()){
         Map.Entry entry = (Map.Entry) iterator.next();
         Module module = (Module) entry.getValue();
         indexConfigurations.putAll(module.getIndexes());
      }
      iterator = indexConfigurations.entrySet().iterator();
      while(iterator.hasNext()){
         Map.Entry entry = (Map.Entry) iterator.next();
         String id = (String) entry.getKey();
         if(!indexes.containsKey(id)){
            Configuration config = (Configuration) entry.getValue();
            FlatIndex index = new FlatIndex(id, config, content);
            indexes.put(id, index);
            // System.out.println("FlatIndexer.loadIndexes added " + id);
            String structure = index.getStructure();
            if(0 < structure.length()){
               structures.add(structure);
            }
            String revision = index.getRevision();
            if(0 < revision.length()){
               revisions.add(revision);
            }
         }
      }
   }
   public void run() {
      // Only the first thread updates. Additional requests set flag so the first thread will repeat the update.
      // runAgain cannot be false on later threads unless the Indexer is updating
      // If already queued, do nothing.
      if(runAgain){ return; }
      runAgain = true;
      // Set to run, but return if already running,
      if(isRunning){ return; }
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
      // System.out.println("FlatIndexer.update");
      // Update the all Resources index (ignores Translations and Revisions).
      allIndex.update();
      Iterator iterator = indexes.entrySet().iterator();
      while(iterator.hasNext()){
         Map.Entry entry = (Map.Entry) iterator.next();
         FlatIndex index = (FlatIndex) entry.getValue();
         index.update();
      }
   }
   /**
    * Updates "ALL" Index without threading.
    */
   void updateAllIndex() {
      allIndex.update();
   }
   /**
    * Updates all Indexes for one language. Cannot be called - needs integration with threading system.
    * 
    * @param language
    */
   // private void update(String language) {
   // System.out.println("FlatIndexer.update language=" + language);
   //
   // updateIndexAll();
   // Iterator iterator = indexes.entrySet().iterator();
   // while(iterator.hasNext()){
   // Map.Entry entry = (Map.Entry) iterator.next();
   // FlatIndex index = (FlatIndex) entry.getValue();
   // index.update(language);
   // }
   // }
   // private void updateIndexAll() {
   // // Update the all Resources index (ignores Translations and Revisions).
   // allIndex.update();
   // }
   /**
    * 
    * @param indexName
    * @param language
    * @return filename of specified Index
    */
   public String getIndexFilename(String indexName, String language) {
      // System.out.println("FlatIndexer.getIndexFilename " + indexName + " l=" + language);
      if((null != indexName) && (0 < indexName.length())){
         if(indexes.containsKey(indexName)){ return(((FlatIndex) indexes.get(indexName)).getFilename(language)); }
      }
      return allIndex.getFilename("");
   }
   Set getStructures() {
      return structures;
   }
   Set getRevisions() {
      return revisions;
   }
}
