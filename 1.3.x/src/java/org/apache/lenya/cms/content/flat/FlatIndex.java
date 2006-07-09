package org.apache.lenya.cms.content.flat;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.lenya.cms.content.Content;
import org.apache.lenya.cms.content.Resource;
import org.apache.lenya.cms.content.flat.index.FlatIndexPart;
import org.apache.lenya.xml.DocumentHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class FlatIndex {
   File configurationFile;
   File indexFile;
   FlatContent content;
   String indexName;
   String language = "en";
   String structure = "";
   String revision = "live";
   Map filter = new HashMap();
   Map include = new HashMap();

   public FlatIndex(FlatContent pcontent, File indexDirectory, String pindexName, String planguage){
      indexName = pindexName;
      configurationFile = new File(indexDirectory, indexName + ".xconf");
      indexFile = new File(indexDirectory, indexName + "_" + planguage + ".xml");
      content = pcontent;
      language = planguage;
//      loadConfiguration();
   }
   private void loadConfiguration(){
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
   public String getIndexFilename(){
      if(!indexFile.exists()) update();
      return indexFile.getPath();
   }
   public void update(){
//TODO: Add publication ID to the log.
System.out.println("Indexer updating " + indexName + "(" + language + ")");
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
      //Update
      if(structure.length() > 0){
         include.put("fullid", new FlatIndexPart("fullid"));
         updateStructure(document, root);
      }else updateFlat(document, root);
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
//System.out.println("Indexing " + indexName + "_" + language + " Completed");
   }
   private void updateStructure(Document document, Element root){
      FlatRelations relations = content.getRelations(structure);
      Element resourceElement = relations.getRoot();
      if(null == resourceElement){
         System.out.println("Error creating Index " + indexName + "_" + language + ".  Could not find Structure " + structure);
      }else{
         addStructureResource(document, root, resourceElement, "", "");
      }
   }
/**
 * Add child "resource" elements of resourceElement to root, and recurse for each.  Must pass filters.
 */
   private void addStructureResource(Document document, Element root, Element resourceElement, String parenttype, String parentdoctype){
//      NodeList children = resourceElement.getElementsByTagName("resource"); //getDescendants, need getChildren
      NodeList children = resourceElement.getChildNodes();
      int length = children.getLength();
      for(int i = 0; i < length; i++){
         if(children.item(i).getNodeName().equals("resource")){
            Element resourceChild = (Element) children.item(i);
            String unid = resourceChild.getAttribute("unid");
            FlatResource resource = 
                  (FlatResource) content.getResource(unid, language, revision);
            if(resource.hasRevision()){
               String fullid = resourceChild.getAttribute("full");
               String fulltype = parenttype + "/" + resource.getType();
               String fulldoctype = parentdoctype + "/" + resource.getDocumentType();
               FlatIndexPart part;
               //FILTER - BEGIN
               Iterator fi = filter.values().iterator();
               boolean useResource = true;
               while(fi.hasNext() & useResource){
                  part = (FlatIndexPart) fi.next();
                  useResource = false;
                  if(part.check(resource.get(part.getProperty(), fullid, fulltype, fulldoctype))) useResource = true;
               }
               //FILTER - END
               if(useResource){
                  //INCLUDE - BEGIN
                  Element element = addElement(document, root, "resource");
                  element.setAttribute("unid", unid);
                  Iterator ii = include.values().iterator();
                  while(ii.hasNext()){
                     part = (FlatIndexPart) ii.next();
                     String value = resource.get(part.getProperty(), fullid);
                     if(value.length() > 0) element.setAttribute(part.getName(), value);
                  }
                  //INCLUDE - END
                  addStructureResource(document, element, resourceChild, fulltype, fulldoctype);
               }  //if useResource
            } //if resource not null
         }  //if "resource"
      }  //for
   }  //function
   private void updateFlat(Document document, Element root){
      // Build Index
      String[] unids = content.getResources();
      int unidsLength = unids.length;
      for(int u = 0; u < unidsLength; u++){
         FlatResource resource = (FlatResource) content.getResource(unids[u], language, revision);
         if(resource.hasRevision()){
            FlatIndexPart part;
            //FILTER - BEGIN
            Iterator fi = filter.values().iterator();
            boolean useResource = true;
            while(fi.hasNext() & useResource){
               part = (FlatIndexPart) fi.next();
               useResource = false;
               if(part.check(resource.get(part.getProperty()))) useResource = true;
            }
            //FILTER - END
            if(useResource){
               //INCLUDE - BEGIN
               Element element = addElement(document, root, "resource");
               element.setAttribute("unid", unids[u]);
               Iterator ii = include.values().iterator();
               while(ii.hasNext()){
                  part = (FlatIndexPart) ii.next();
                  String value = resource.get(part.getProperty());
                 if(value.length() > 0) element.setAttribute(part.getName(), value);
               }
               //INCLUDE - END
            }  //if useResource
         } //if resource not null
      }
   }
   private Element addElement(Document document, Element parent, String newElementName){
      Element newElement = document.createElement(newElementName);
      parent.appendChild(newElement);
      return newElement;
   }
}
