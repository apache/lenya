package org.apache.lenya.cms.content.flat;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.lenya.util.Globals;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
/**
 * 
 * @author solprovider
 * @since 1.3
 */
class FlatIndex {
   private static final String LANGUAGE_ALL = "ALLINDEX";
   FlatContent content;
   volatile Map files = new HashMap(); // files(language) = filename String
   String structure = "";
   String revision = "live";
   Map filter = new HashMap();
   Map include = new HashMap();
   String name = "";
   boolean isAll = false;
   FlatIndex(FlatContent content) {
      // System.out.println("FlatIndex ALL");
      this.content = content;
      isAll = true;
      include.put("type", new FlatIndexPart("type"));
      include.put("id", new FlatIndexPart("id"));
   }
   FlatIndex(String name, Configuration config, FlatContent content) {
      // System.out.println("FlatIndex " + name);
      this.name = name;
      this.content = content;
      structure = config.getAttribute("structure", structure);
      revision = config.getAttribute("revision", revision);
      // For each element
      Configuration[] children = config.getChildren();
      for(int c = 0; c < children.length; c++){
         Configuration child = children[c];
         String element = child.getName();
         if(element.equalsIgnoreCase("flter")){
            FlatIndexPart part = new FlatIndexPart(child);
            filter.put(part.getName(), part);
         }
         if(element.equalsIgnoreCase("include")){
            FlatIndexPart part = new FlatIndexPart(child);
            include.put(part.getName(), part);
         }
      }
      include.put("unid", new FlatIndexPart("unid"));
      include.put("type", new FlatIndexPart("type"));
      include.put("doctype", new FlatIndexPart("doctype"));
      include.put("id", new FlatIndexPart("id"));
      include.put("title", new FlatIndexPart("title"));
      include.put("href", new FlatIndexPart("href"));
      include.put("extension", new FlatIndexPart("extension"));
   }
   void update() {
      // System.out.println("FlatIndexer.update");
      if(isAll){
         update("");
         return;
      }
      String[] languages = content.getLanguages();
      int languagesLength = languages.length;
      for(int l = 0; l < languagesLength; l++){
         String language = languages[l];
         update(language);
      }
   }
   /**
    * 
    * @param language
    * @return Absolute filename of index
    */
   String update(String language) {
      // System.out.println("FlatIndex.update " + name + " language=" + language);
      // Init Document
      Document document;
      try{
         document = org.apache.lenya.xml.DocumentHelper.createDocument("", "index", null);
      }catch(javax.xml.parsers.ParserConfigurationException pce){
         System.out.println("FlatIndex: ParserConfigurationException");
         return "";
      }
      Element root = document.getDocumentElement();
      root.setAttribute("name", name);
      root.setAttribute("language", language);
      // Update
      if(structure.length() > 0){
         include.put("fullid", new FlatIndexPart("fullid"));
         updateStructure(document, root, language);
      }else if(0 < this.name.length()){
         updateFlat(document, root, language);
      }else{
         // SitetreeGenerator called without src attribute.
         // Special case: all Resources disregarding Translations
         updateAll(document, root);
      }
      // Get File
      File indexFile = getFile(language);
      // Write document
      // System.out.println("FlatIndex: Writing " + indexFile.getAbsolutePath());
      try{
         org.apache.lenya.xml.DocumentHelper.writeDocument(document, indexFile);
         // System.out.println("Indexing " + name + "_" + language + " completed.");
      }catch(javax.xml.transform.TransformerConfigurationException tce){
         System.out.println("FlatIndex: TransformerConfigurationException" + name + "_" + language);
      }catch(javax.xml.transform.TransformerException te){
         System.out.println("FlatIndex: TransformerException" + name + "_" + language);
      }catch(java.io.IOException ioe){
         System.out.println("FlatIndex: IOException" + name + "_" + language);
      }catch(Exception e){
         System.out.println("FlatIndex: Unknown Exception - " + name + "_" + language + "\n" + e.getLocalizedMessage());
      }
      return indexFile.getAbsolutePath();
   }
   private synchronized File getFile(String language) {
      File indexFile;
      if(isAll){
         if(files.containsKey(LANGUAGE_ALL)){
            indexFile = new File((String) files.get(LANGUAGE_ALL));
         }else{
            try{
               indexFile = File.createTempFile("all", ".xml");
            }catch(IOException e){
               indexFile = new File(Globals.getPublication().getContentDirectory(), "all.xml");
            }
            indexFile.deleteOnExit();
            files.put(LANGUAGE_ALL, indexFile.getAbsolutePath());
         }
      }else{
         if(files.containsKey(language)){
            indexFile = new File((String) files.get(language));
         }else{
            try{
               indexFile = File.createTempFile(name, "_" + language + ".xml");
            }catch(IOException e){
               indexFile = new File(Globals.getPublication().getContentDirectory(), name + "_" + language + ".xml");
            }
            indexFile.deleteOnExit();
            files.put(language, indexFile.getAbsolutePath());
         }
      }
      // System.out.println("FlatIndex.getFile " + indexFile.getName());
      return indexFile;
   }
   private void updateStructure(Document document, Element root, String language) {
      // System.out.println("FlatIndexer.updateStructure " + name + " Structure='" + structure + "'");
      FlatRelations relations = content.getRelations(structure);
      if(null == relations){
         System.out.println("FlatIndexer.updateStructure " + name + " Structure '" + structure + "' was null");
         return;
      }
      Element resourceElement = relations.getRoot();
      if(null == resourceElement){
         System.out.println("Error creating Index " + name + "_" + language + ".  Could not find Structure " + structure);
      }else{
         addStructureResource(document, root, resourceElement, "", language);
      }
   }
   /**
    * Add child "resource" elements of resourceElement to root, and recurse for each. Must pass filters.
    */
   // private void addStructureResource(Document document, Element root, Element resourceElement, String parenttype, String parentdoctype) {
   private void addStructureResource(Document document, Element root, Element resourceElement, String parenttype, String language) {
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
                  addStructureResource(document, element, resourceChild, fulltype, language);
               } // if useResource
            } // if resource not null
         } // if "resource"
      } // for
   } // function
   private void updateFlat(Document document, Element root, String language) {
      // System.out.println("FlatIndexer.updateFlat " + name);
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
      // System.out.println("FlatIndexer.updateAll");
      root.setAttribute("description", "Special index containing all Resources ignoring existence of Translations: no filtering by language and no information from Translations and Revisions.");
      // Build Index
      String[] unids = content.getResources();
      int unidsLength = unids.length;
      for(int u = 0; u < unidsLength; u++){
         String unid = unids[u];
         FlatResource resource = (FlatResource) content.getResource(unid);
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
         if(null != languages){
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
         }
         // INCLUDE - END
      } // for
   }
   private Element addElement(Document document, Element parent, String newElementName) {
      Element newElement = document.createElement(newElementName);
      parent.appendChild(newElement);
      return newElement;
   }
   String getFilename(String language) {
      // System.out.println("FlatIndex.getFilename " + name + " l=" + language);
      if(isAll) language = LANGUAGE_ALL;
      String filename = (String) files.get(language);
      if(null == filename) filename = update(language);
      return filename;
   }
   String getStructure() {
      return structure;
   }
   String getRevision() {
      return revision;
   }
}
