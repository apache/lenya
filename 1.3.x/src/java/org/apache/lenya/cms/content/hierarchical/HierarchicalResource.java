package org.apache.lenya.cms.content.hierarchical;
import java.io.File;
import org.apache.lenya.cms.content.Location;
import org.apache.lenya.cms.content.Resource;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeException;
import org.apache.lenya.cms.publication.Publication;
import org.w3c.dom.Document;
/**
 * ContentInputModule requires a Resource. <br>
 * Deciding doctype would be better handled in Modules.
 * 
 * @author solprovider
 * @since 1.3
 */
public class HierarchicalResource implements Resource {
   private org.apache.lenya.cms.publication.Document doc;
   private File contentDirectory;
   private String language;
   private String id;
   private String area;
   public HierarchicalResource(File contentDirectory, String unid) {
      this.contentDirectory = contentDirectory;
      Location location = new Location(unid);
      id = location.getUnid();
      language = location.getLanguage();
      area = Publication.LIVE_AREA;
      try{
         PageEnvelope pe = PageEnvelope.getCurrent();
         doc = pe.getDocument();
         if(unid.equals(doc.getId())){
            // If using current document, use doc from PageEnvelope.
            area = doc.getArea();
            language = doc.getLanguage();
         }else{
            doc = null;
         }
      }catch(PageEnvelopeException e){
      }
   }
   public String getURI() {
      if(null != doc){
         return doc.getFile().getPath();
      }
      return new File(contentDirectory, area + id + File.separator + "index_" + language + ".xml").getPath();
   }
   public String getMetaURI() {
      return getURI();
   }
   public String getNewURI() {
      return "";
   }
   public String getUNID() {
      return id;
   }
   public String getID() {
      return id;
   }
   public String getType() {
      return "xml";
   }
   public String getDocumentType() {
      // System.out.println("HierarchicalResource.getDocumentType ID=" + id);
      // Was returning "cocoon/{id}"
      if(id.equalsIgnoreCase("index")){
         return "homepage";
      }
      return "xhtml";
   }
   public String getLanguage() {
      return language;
   }
   public String getDefaultLanguage() {
      if(null != doc){
         return doc.getLanguage();
      }
      return "";
   }
   public String[] getLanguages() {
      if(null != doc){
         try{
            return doc.getLanguages();
         }catch(DocumentException e){
         }
      }
      return null;
   }
   public Document getInfoDocument() {
      return null;
   }
   public Document update(Document document) {
      return null;
   }
   public String getTitle() {
      return getTitle(getDefaultLanguage());
   }
   public String getTitle(String language) {
      if(null != doc){
         return doc.getName();
      }
      return null;
   }
   public String getTitle(String language, String revision) {
      // Hierarchical content does not support Revisions.
      return getTitle(language);
   }
}
