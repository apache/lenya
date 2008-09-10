package org.apache.lenya.cms.content.flat;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.apache.lenya.cms.content.Resource;
import org.apache.lenya.util.Globals;
/**
 * 
 * @author solprovider
 * @since 1.3
 */
public class FlatDesign {
   public static final String DESIGN_LANGUAGE = "xx";
   static final String SEPARATOR_DESIGN = "$";
   static final String SEPARATOR_RELATIONS = "+";
   static final String DIRECTORY_DESIGN = "design";
   private File directoryResources;
   private String publicationId;
   public FlatDesign(File directory, String publicationId) {
      directoryResources = new File(directory, DIRECTORY_DESIGN);
      if(!directoryResources.exists()) directoryResources.mkdirs();
      this.publicationId = publicationId;
   }
   private Resource getResource(String unid) {
      return (Resource) new FlatResource(directoryResources, unid, DESIGN_LANGUAGE);
   }
   private Resource getResource(String unid, String revision) {
      System.out.println("FlatDesign.getResource UNID=" + unid + " REV=" + revision);
      return (Resource) new FlatResource(directoryResources, unid, DESIGN_LANGUAGE, revision);
   }
   public String[] getResources() {
      return directoryResources.list();
   }
   // New for Design
   public Resource getDesign(String filename) {
      return getResource(getDesignUnid(filename));
   }
   public Resource getDesign(String filename, String revision) {
      return getResource(getDesignUnid(filename), revision);
   }
   public Resource getStructure(String name) {
      return getResource(getStructureUnid(name));
   }
   public Resource getStructure(String name, String revision) {
      return getResource(getStructureUnid(name), revision);
   }
   public File getDirectory() {
      return directoryResources;
   }
   /**
    * Transforms filename into a string acceptable as a directory name.<br>
    * Process must be repeatable with consistent result for a given filename and not duplicate results for other filenames. <br>
    * Used by getDesignUnid
    * 
    * @param filename
    * @return
    */
   private static String makeSafe(String filename) {
      // TODO: Does this work? for all OSes?
      String ret = filename;
      try{
         ret = URLEncoder.encode(filename, "UTF-8");
      }catch(UnsupportedEncodingException e){
         ret = ret.replaceAll("[^a-zA-Z1-90]", SEPARATOR_DESIGN);
      }
      ret = ret.replaceAll("\u002A", "%2A"); // Asterisk is legal for URL encoding, but not legal directory name.
      return ret;
   }
   /**
    * Function to standardize translation of Module filename into a String that is safe as a directory name
    * 
    * @param filename
    * @return UNID of Design Resource
    */
   public static String getDesignUnid(String filename) {
      // /[pubID]/[moduleId]/filename
      String publicationId = "";
      String moduleId = "";
      String filepath = "";
      String[] parts = filename.split("/", 4);
      if(4 > parts.length) return "";
      if(0 != parts[0].length()) return "";
      if(0 == parts[1].length()){
         publicationId = Globals.getPublication().getId();
      }else{
         publicationId = parts[1];
      }
      if(0 == parts[2].length()){
         moduleId = Globals.getModuleId();
      }else{
         moduleId = parts[2];
      }
      filepath = FlatDesign.makeSafe(parts[3]);
      return publicationId + SEPARATOR_DESIGN + moduleId + SEPARATOR_DESIGN + filepath;
   }
   public String getStructureUnid(String structure) {
      return publicationId + FlatDesign.SEPARATOR_RELATIONS + structure;
   }
}
