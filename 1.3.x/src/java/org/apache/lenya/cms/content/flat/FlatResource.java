package org.apache.lenya.cms.content.flat;

import java.io.File;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.lenya.cms.content.Resource;
//import org.apache.lenya.cms.content.Translation;
import org.apache.lenya.xml.DocumentHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class FlatResource implements Resource {
   File contentDirectory;
   File resourceDirectory;
   Document resource;
   Element root;
   String unid ="";
   String id ="";
   String type = "xml";
   String doctype = "";
   String defaultLanguage = "en";
   String defaultRevision = "live";
   String extension = "";
   String[] languages = {"en"};

   public FlatResource(File directory, String punid, String language, String revision){
      contentDirectory = directory;
      unid = punid;
      defaultLanguage = language;
      defaultRevision = revision;
      init(false);
   }
   public FlatResource(File directory, String punid, String language){
      contentDirectory = directory;
      unid = punid;
      defaultLanguage = language;
      init(false);
   }
   public FlatResource(File directory, String punid){
      contentDirectory = directory;
      unid = punid;
//TODO: defaultLanguage = Publication's default language.
      init(true);
   }
   private void init(boolean useDefaultLanguage){
      resourceDirectory = new File(contentDirectory, "resource" + File.separator + unid);
      try{
         resource = DocumentHelper.readDocument(new File(resourceDirectory, "resource.xml"));
         root = resource.getDocumentElement();
         if(useDefaultLanguage){
            if(root.hasAttribute("defaultlanguage")){
              defaultLanguage = root.getAttribute("defaultlanguage");
            }else{
//TODO: Use Publication's Default Language
            }
         }
         if(root.hasAttribute("doctype")) doctype = root.getAttribute("doctype");
         if(root.hasAttribute("type")) type = root.getAttribute("type");
         if(root.hasAttribute("id")) id = root.getAttribute("id");
         NodeList ts = root.getElementsByTagName("translation");
         int length = ts.getLength();
         languages = new String[length];
         for(int i = 0; i < length; i++){
            Element t = (Element) ts.item(i);
            languages[i] = t.getAttribute("language");
         }
      }catch(javax.xml.parsers.ParserConfigurationException pce){
      }catch(org.xml.sax.SAXException saxe){
      }catch(java.io.IOException ioe){
      }
   }
   public String getURI(){
      return getTranslation(defaultLanguage).getURI();
   }
   public String getMetaURI(){
      return getTranslation(defaultLanguage).getMetaURI();
   }
   public String getNewURI(){
      return getTranslation(defaultLanguage).getNewURI();
   }

   public String getUNID(){
      return unid;
   }
   public String getID(){
      return id;
   }
   public String getType(){
      return type;
   }
   public String getDocumentType(){
      return doctype;
   }
   public String getLanguage(){
      return defaultLanguage;
   }
//Maintain separately?
   public String getDefaultLanguage(){
      return defaultLanguage;
   }
   public String[] getLanguages(){
     return languages;
   }

/**
 * Get Navigation Title from live revision of current language.
 */
   public String getTitle(){
      return getTitle(defaultLanguage, defaultRevision);
   }
/**
 * Get Navigation Title from live revision of specified language.
 */
   public String getTitle(String language){
      return getTitle(language, defaultRevision);
   }
/**
 * Get Navigation Title from specified revision of specified language.
 */
   public String getTitle(String language, String revision){
      try{
         return getTranslation(language).getRevision(revision).getTitle();
      }catch(java.lang.NullPointerException npe){
System.out.println("FR.gTitle NPE UNID=" + unid);
         return "";
      }
   }
   public String getExtension(){
      return getTranslation().getExtension();
   }
   public String getHREF(){
      return getTranslation().getHREF();
   }
/**
 * Get variable for Index Filters
 */
   public String get(String property, String fullid, String fulltype, String fulldoctype){
      if(property.equalsIgnoreCase("type")){ return fulltype;
      }else if(property.equalsIgnoreCase("doctype")){ return fulldoctype;
      }
      return get(property, fullid);
   }
/**
 * Get variable for Structured Index Filters
 */
   public String get(String property, String fullid){
      if(property.equalsIgnoreCase("fullid")){ return fullid; }
      return get(property);
   }
/**
 * Get variable for Flat Index Includes
 */
   public String get(String property){
      if(property.equalsIgnoreCase("unid")){ return getUNID();
      }else if(property.equalsIgnoreCase("id")){ return getID();
      }else if(property.equalsIgnoreCase("type")){ return getType();
      }else if(property.equalsIgnoreCase("doctype")){ return getDocumentType();
      }else if(property.equalsIgnoreCase("extension")){ return getExtension();
      }else if(property.equalsIgnoreCase("title")){ return getTitle();
      }else if(property.equalsIgnoreCase("language")){ return getLanguage();
      }else if(property.equalsIgnoreCase("fullid")){ return getID();
      }else if(property.equalsIgnoreCase("href")){ return getHREF();
      }
      return getXPath(property);
   }
   private String getXPath(String property){
      return "";
   }

   public FlatTranslation getTranslation(){
      return getTranslation(defaultLanguage, true);
   }
   public FlatTranslation getTranslation(String language){
      return getTranslation(language, true);
   }
   public FlatTranslation getTranslation(String language, boolean allowDefaultLanguage){
      if(allowDefaultLanguage){
         return new FlatTranslation(resourceDirectory, language, defaultLanguage);
      }else return new FlatTranslation(resourceDirectory, language);
   }
   public boolean hasRevision(){
      FlatTranslation translation = getTranslation(defaultLanguage, false);
      if(null == translation) return false;
      FlatRevision revision = translation.getRevision(defaultRevision);
      if(null == revision) return false;
      boolean exists = revision.exists();
      if(getTitle().length() < 1){
         System.out.println("Exists=" + exists + " U=" + unid + " L=" + defaultLanguage + " R=" + defaultRevision);
         return false;
      }
      try{
         return getTranslation(defaultLanguage, false).getRevision(defaultRevision).exists();
      }catch(java.lang.NullPointerException npe){
         return false;
      }
   }
}
