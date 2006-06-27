package org.apache.lenya.cms.content.flat;

import java.io.File;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceNotFoundException;
//import org.apache.lenya.cms.content.Revision;
//import org.apache.lenya.cms.content.Translation;
import org.apache.lenya.xml.DocumentHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FlatTranslation {
   File resourceDirectory;
   File translationDirectory;
   String language = "en";
   String defaultLanguage = "en";
   String live = "";
   String edit = "last";
   String revision = "live";
   Document document;
   Element root;
   FlatRevision fr;
   boolean initRevisions = false;
   SortedSet revisions = new TreeSet();
   boolean isChanged = false;

   public FlatTranslation(File directory, String planguage, String pdefaultLanguage, String prevision){
      resourceDirectory = directory;
      language = planguage;
      defaultLanguage = pdefaultLanguage;
      revision = prevision;
      init();
   }
   public FlatTranslation(File directory, String planguage, String pdefaultLanguage){
      resourceDirectory = directory;
      language = planguage;
      defaultLanguage = pdefaultLanguage;
      init();
   }
   public FlatTranslation(File directory, String planguage){
      resourceDirectory = directory;
      language = planguage;
      defaultLanguage = planguage;
      init();
   }
   private void init(){
      translationDirectory = new File(resourceDirectory, language);
      if(!translationDirectory.exists()) translationDirectory = new File(resourceDirectory, defaultLanguage);
      if(!translationDirectory.exists()) return;
      try{
         document = DocumentHelper.readDocument(new File(translationDirectory, "translation.xml"));
         root = document.getDocumentElement();
         if(root.hasAttribute("live")) live = root.getAttribute("live");
         if(root.hasAttribute("edit")) edit = root.getAttribute("edit");
      }catch(javax.xml.parsers.ParserConfigurationException pce){
System.out.println("FlatTranslation: ParserConfigurationException");
      }catch(org.xml.sax.SAXException saxe){
System.out.println("FlatTranslation: SAXException");
      }catch(java.io.IOException ioe){
System.out.println("FlatTranslation: IOException");
      }
   }
   public String[] getRevisions() {
      if(!initRevisions){
         String[] filelist = translationDirectory.list();
         for(int f = 0; f < filelist.length; f++){
            String filename = filelist[f];
            int pos = filename.lastIndexOf(".");
            if(pos > 0) filename = filename.substring(0, pos);
            if(!filename.equalsIgnoreCase("translation")) revisions.add(filename);
         }
         initRevisions = true;
      }
      return (String[]) revisions.toArray(new String[0]);
   }
   public String getLive() {
      return live;      
   }
   public String getEdit() {
      return edit;
   }
   public String getURI(){
      if(null == fr) fr = getRevision();
      if(null == fr) return "";
      return fr.getURI();
   }
   public String getMetaURI(){
      if(null == fr) fr = getRevision();
      if(null == fr) return "";
      return fr.getMetaURI();
   }
   public String getNewURI(){
      String newRevision = getDateString();
//WORK: Change Edit to newRevision
      return new File(translationDirectory, newRevision + ".xml").getPath();
   }
   public String getExtension() {
      if(null == fr) fr = getRevision();
      if(null == fr) return "";
      return fr.getExtension();
   }
   public String getHREF() {
      if(null == fr) fr = getRevision();
      if(null == fr) return "";
      return fr.getHREF();
   }
   public FlatRevision getRevision(){
      return getRevision(revision);
   }
   public FlatRevision getRevision(String prevision){
      String rev = prevision;
      if(rev.equalsIgnoreCase("edit")){
          if(edit.length() > 0){
             rev = edit;
          }else rev = live;
      }
      if(rev.equalsIgnoreCase("live")) rev = live;
      try{
         if(rev.equalsIgnoreCase("last")) rev = (String) revisions.last();
         if(rev.equalsIgnoreCase("first")) rev = (String) revisions.first();
      }catch(java.util.NoSuchElementException nsee){
         return null;
      }
      if(null == rev) return null;
      if(rev.length() < 1) return null;
      return new FlatRevision(translationDirectory, rev);
   }
   private String getDateString(){
      return Long.toString(new java.util.Date().getTime());
   }
   void setLive(String revision){
      live = revision;
      isChanged = true;
   }
   void setEdit(String revision){
      edit = revision;
      isChanged = true;
   }
   void save(){
      if(isChanged){
         File file = new File(translationDirectory, "translation.xml");
         Document doc;
         try{
            doc = DocumentHelper.readDocument(file);
         }catch(javax.xml.parsers.ParserConfigurationException pce){
System.out.println("FlatTranslation.save - ParserConfigurationException:" + file.getAbsolutePath());
            return;
         }catch(org.xml.sax.SAXException saxe){
System.out.println("FlatTranslation.save - SAXException:" + file.getAbsolutePath());
            return;
         }catch(java.io.IOException ioe){
System.out.println("FlatTranslation.save - Could not read file:" + file.getAbsolutePath());
            return;
         }
         root = doc.getDocumentElement();
         root.setAttribute("live", live);
         root.setAttribute("edit", edit);
         try{
            DocumentHelper.writeDocument(doc, file);
         }catch(javax.xml.transform.TransformerConfigurationException tce){
System.out.println("FlatTranslation.save - TransformerConfigurationException:" + file.getAbsolutePath());
         }catch(javax.xml.transform.TransformerException te){
System.out.println("FlatTranslation.save - TransformerException:" + file.getAbsolutePath());
         }catch(java.io.IOException ioe2){
System.out.println("FlatTranslation.save - Could not write file:" + file.getAbsolutePath());
         }
      }
      isChanged = false;
   }
   void deleteRevision(String revision){
      FlatRevision fr = getRevision(revision);
      String extension = fr.getExtension();
//TODO: Backup before delete.  Use renameTo() to move outside the resource directory.
      File file = new File(translationDirectory, revision + ".xml");
      if(file.exists()){
         file.delete();
         if(extension.length() > 0){
            file = new File(translationDirectory, revision + "." + extension);
            if(file.exists()) file.delete();
         }
      }
   }
}
