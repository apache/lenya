package org.apache.lenya.cms.content;
import java.util.StringTokenizer;
/**
 * Parses location from URL. Not aware of Content -- unid may be unid or id.
 * 
 * @author solprovider
 * @since 1.3
 */
public class Location {
   private String unid = "";
   private String language = "";
   private String revision = "";
   private String extension = "";
   /**
    * Separate on periods, exclamation marks, and underscores <br>
    * revision = part after last exclamation mark. <br>
    * extension = part after last period. <br>
    * language = first two-character part after an underscore and additional parts until period or exclamation mark. <br>
    * unid = first part and underscore-separated parts until another part is found.
    * 
    * @param location
    */
   public Location(String location) {
      StringTokenizer tokens = new StringTokenizer(location, "!_.", true);
      if(tokens.hasMoreTokens()){
         unid = tokens.nextToken();
      }
      boolean inLanguage = false;
      boolean inUnid = true;
      while(tokens.hasMoreTokens()){
         String token = tokens.nextToken();
         if("!".equals(token)){
            inLanguage = false;
            inUnid = false;
            if(tokens.hasMoreTokens()){
               revision = tokens.nextToken();
            }
         }else if("_".equals(token)){
            if(tokens.hasMoreTokens()){
               token = tokens.nextToken();
               if(inLanguage){
                  if(tokens.hasMoreTokens()){
                     language = language + "_" + token;
                  }
               }else{
                  if(2 == token.length()){
                     language = token;
                     inLanguage = true;
                     inUnid = false;
                  }else if(inUnid){
                     unid = unid + "_" + token;
                  }
               }
            }
         }else if(".".equals(token)){
            inLanguage = false;
            inUnid = false;
            if(tokens.hasMoreTokens()){
               extension = tokens.nextToken();
            }
         }else{
            inLanguage = false;
            System.out.println("Location part not found in " + location);
         }
      }
   }
   public String getUnid() {
      return unid;
   }
   public String getLanguage() {
      return language;
   }
   public String getRevision() {
      return revision;
   }
   public String getExtension() {
      return extension;
   }
}
