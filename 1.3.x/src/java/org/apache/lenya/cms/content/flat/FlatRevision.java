package org.apache.lenya.cms.content.flat;

import java.io.File;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.lenya.xml.DocumentHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FlatRevision {
   File translationDirectory;
   File file;
   File metafile;
   String revision;
   String title = "";
   String extension = "";
   String href = "";
   Document resource;
   Element root;

   public FlatRevision(File directory, String prevision){
      translationDirectory = directory;
      revision = prevision;
      init();
   }
   private void init(){
      try{
         metafile = new File(translationDirectory, revision + ".xml");
         resource = DocumentHelper.readDocument(metafile);
         root = resource.getDocumentElement();
         if(root.hasAttribute("title")) title = root.getAttribute("title");
         if(root.hasAttribute("extension")) extension = root.getAttribute("extension");
         if(root.hasAttribute("href")) href = root.getAttribute("href");
      }catch(javax.xml.parsers.ParserConfigurationException pce){
      }catch(org.xml.sax.SAXException saxe){
      }catch(java.io.IOException ioe){
      }
      if(extension.length() > 0){
         file = new File(translationDirectory, revision + "." + extension);
      }else file = metafile;
   }
   public String getTitle(){
      return title;
   }
   public String getExtension(){
      return extension;
   }
   public String getHREF(){
      return href;
   }
   public String getURI(){
      return file.getPath();
   }
   public String getMetaURI(){
      return metafile.getPath();
   }
   public boolean exists(){
      return file.exists();
   }
}
