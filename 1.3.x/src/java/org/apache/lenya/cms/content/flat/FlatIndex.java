package org.apache.lenya.cms.content.flat;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.lenya.xml.DocumentHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
/**
 * 
 * @author solprovider
 * @since 1.3
 */
public class FlatIndex {
   File configurationFile = null;
   File indexFile;
   FlatContent content;
   String indexName;
   String language = "";
   String structure = "";
   String revision = "live";
   Map filter = new HashMap();
   Map include = new HashMap();
   public FlatIndex(FlatContent content, File indexDirectory, String indexName, String language) {
      if(null == indexName){ // SitetreeGenerator called without src attribute.
         this.indexName = "";
      }else{
         this.indexName = indexName;
      }
      this.content = content;
      if(0 == this.indexName.length()){ // Special case: all Resources disregarding Translations
         indexFile = new File(indexDirectory, "all.xml");
      }else{
         this.language = language;
         configurationFile = new File(indexDirectory, indexName + ".xconf");
         indexFile = new File(indexDirectory, indexName + "_" + language + ".xml");
      }
   }
   private void loadConfiguration() {
      if(null == configurationFile){ // Special case: all Resources disregarding Translations
         include.put("unid", new FlatIndexPart("unid"));
         include.put("type", new FlatIndexPart("type"));
         include.put("doctype", new FlatIndexPart("doctype"));
         include.put("id", new FlatIndexPart("id"));
      }else{
         Document document;
         try{
            document = DocumentHelper.readDocument(configurationFile);
         }catch(javax.xml.parsers.ParserConfigurationException pce){
            System.out.println("Indexer: could not parse " + configurationFile.getPath());
            return;
         }catch(org.xml.sax.SAXException saxe){
            System.out.println("Indexer: " + configurationFile.getPath() + " is not valid.");
            return;
         }catch(java.io.IOException ioe){
            System.out.println("Indexer: could not read " + configurationFile.getPath());
            return;
         }
         Element root = document.getDocumentElement();
         if(root.hasAttribute("structure")) structure = root.getAttribute("structure");
         if(root.hasAttribute("revision")) revision = root.getAttribute("revision");
         NodeList nl;
         int length;
         int i;
         nl = root.getElementsByTagName("filter");
         length = nl.getLength();
         for(i = 0; i < length; i++){
            FlatIndexPart part = new FlatIndexPart((Element) nl.item(i));
            filter.put(part.getName(), part);
         }
         nl = root.getElementsByTagName("include");
         length = nl.getLength();
         for(i = 0; i < length; i++){
            FlatIndexPart part = new FlatIndexPart((Element) nl.item(i));
            include.put(part.getName(), part);
         }
         include.put("unid", new FlatIndexPart("unid"));
         include.put("type", new FlatIndexPart("type"));
         include.put("doctype", new FlatIndexPart("doctype"));
         include.put("id", new FlatIndexPart("id"));
         include.put("title", new FlatIndexPart("title"));
         include.put("href", new FlatIndexPart("href"));
         include.put("extension", new FlatIndexPart("extension"));
      }
   }
   public String getIndexFilename() {
      if(!indexFile.exists()) update();
      return indexFile.getPath();
   }
   public void update() {
      // TODO: Add publication ID to the log.
      // System.out.println("Indexer updating " + indexName + "(" + language + ")");
      loadConfiguration();
      // Init Document
      Document document;
      try{
         document = org.apache.lenya.xml.DocumentHelper.createDocument("", "index", null);
      }catch(javax.xml.parsers.ParserConfigurationException pce){
         System.out.println("FlatIndex: ParserConfigurationException");
         return;
      }
      Element root = document.getDocumentElement();
      root.setAttribute("name", indexName);
      root.setAttribute("language", language);
      // Update
      if(structure.length() > 0){
         include.put("fullid", new FlatIndexPart("fullid"));
         updateStructure(document, root);
      }else if(0 < this.indexName.length()){
         updateFlat(document, root);
      }else{ // Special case: all Resources disregarding Translations
         updateAll(document, root);
      }
      // Write document
      try{
         org.apache.lenya.xml.DocumentHelper.writeDocument(document, indexFile);
      }catch(javax.xml.transform.TransformerConfigurationException tce){
         System.out.println("FlatIndex: TransformerConfigurationException");
      }catch(javax.xml.transform.TransformerException te){
         System.out.println("FlatIndex: TransformerException");
      }catch(java.io.IOException ioe){
         System.out.println("FlatIndex: IOException");
      }
      // System.out.println("Indexing " + indexName + "_" + language + " Completed");
   }
   private void updateStructure(Document document, Element root) {
      FlatRelations relations = content.getRelations(structure);
      Element resourceElement = relations.getRoot();
      if(null == resourceElement){
         System.out.println("Error creating Index " + indexName + "_" + language + ".  Could not find Structure " + structure);
      }else{
         addStructureResource(document, root, resourceElement, "");
      }
   }
   /**
    * Add child "resource" elements of resourceElement to root, and recurse for each. Must pass filters.
    */
   // private void addStructureResource(Document document, Element root, Element resourceElement, String parenttype, String parentdoctype) {
   private void addStructureResource(Document document, Element root, Element resourceElement, String parenttype) {
      // NodeList children = resourceElement.getElementsByTagName("resource"); //getDescendants, need getChildren
      NodeList children = resourceElement.getChildNodes();
      int length = children.getLength();
      for(int i = 0; i < length; i++){
         if(children.item(i).getNodeName().equals("resource")){
            Element resourceChild = (Element) children.item(i);
            String unid = resourceChild.getAttribute("unid");
            FlatResource resource = (FlatResource) content.getResource(unid, language, revision);
            if(resource.hasRevision()){
               String fullid = resourceChild.getAttribute("full");
               String fulltype = parenttype + "/" + resource.getType();
               // String fulldoctype = parentdoctype + "/" + resource.getDocumentType();
               FlatIndexPart part;
               // FILTER - BEGIN
               Iterator fi = filter.values().iterator();
               boolean useResource = true;
               while(fi.hasNext() & useResource){
                  part = (FlatIndexPart) fi.next();
                  useResource = false;
                  // if(part.check(resource.get(part.getProperty(), fullid, fulltype, fulldoctype)))
                  if(part.check(resource.get(part.getProperty(), fullid, fulltype))) useResource = true;
               }
               // FILTER - END
               if(useResource){
                  // INCLUDE - BEGIN
                  Element element = addElement(document, root, "resource");
                  element.setAttribute("unid", unid);
                  Iterator ii = include.values().iterator();
                  while(ii.hasNext()){
                     part = (FlatIndexPart) ii.next();
                     String value = resource.get(part.getProperty(), fullid);
                     if(value.length() > 0) element.setAttribute(part.getName(), value);
                  }
                  // INCLUDE - END
                  // addStructureResource(document, element, resourceChild, fulltype, fulldoctype);
                  addStructureResource(document, element, resourceChild, fulltype);
               } // if useResource
            } // if resource not null
         } // if "resource"
      } // for
   } // function
   private void updateFlat(Document document, Element root) {
      // Build Index
      String[] unids = content.getResources();
      int unidsLength = unids.length;
      for(int u = 0; u < unidsLength; u++){
         FlatResource resource = (FlatResource) content.getResource(unids[u], language, revision);
         if(resource.hasRevision()){
            FlatIndexPart part;
            // FILTER - BEGIN
            Iterator fi = filter.values().iterator();
            boolean useResource = true;
            while(fi.hasNext() & useResource){
               part = (FlatIndexPart) fi.next();
               useResource = false;
               if(part.check(resource.get(part.getProperty()))) useResource = true;
            }
            // FILTER - END
            if(useResource){
               // INCLUDE - BEGIN
               Element element = addElement(document, root, "resource");
               element.setAttribute("unid", unids[u]);
               Iterator ii = include.values().iterator();
               while(ii.hasNext()){
                  part = (FlatIndexPart) ii.next();
                  String value = resource.get(part.getProperty());
                  if(value.length() > 0) element.setAttribute(part.getName(), value);
               }
               // INCLUDE - END
            } // if useResource
         } // if resource not null
      } // for
   }
   private void updateAll(Document document, Element root) {
      // System.out.println("updateAll BEGIN");
      root.setAttribute("description", "Special index containing all Resources ignoring existence of Translations: no filtering by language and no information from Translations and Revisions.");
      // Build Index
      String[] unids = content.getResources();
      int unidsLength = unids.length;
      for(int u = 0; u < unidsLength; u++){
         String unid = unids[u];
         FlatResource resource = (FlatResource) content.getResource(unid, language, revision);
         FlatIndexPart part;
         // INCLUDE - BEGIN
         Element element = addElement(document, root, "resource");
         element.setAttribute("unid", unid);
         Iterator ii = include.values().iterator();
         while(ii.hasNext()){
            part = (FlatIndexPart) ii.next();
            String value = resource.get(part.getProperty());
            if(value.length() > 0) element.setAttribute(part.getName(), value);
         }
         // Add translations with language and title.
         String[] languages = resource.getLanguages();
         for(int l = 0; l < languages.length; l++){
            Element translationElement = addElement(document, element, "translation");
            translationElement.setAttribute("language", languages[l]);
            FlatTranslation translation = resource.getTranslation(languages[l], false);
            if(null != translation){
               FlatRevision revision = translation.getRevision();
               if(null != revision){
                  String title = revision.getTitle();
                  if(title.length() > 0){
                     translationElement.setAttribute("title", title);
                  }
               }
            }
         }
         // INCLUDE - END
      } // for
   }
   private Element addElement(Document document, Element parent, String newElementName) {
      Element newElement = document.createElement(newElementName);
      parent.appendChild(newElement);
      return newElement;
   }
}
