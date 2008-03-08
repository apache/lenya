package org.apache.lenya.cms.content.flat;
import java.io.File;
import java.util.HashMap;
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
public class FlatRelations {
   File file;
   Document relations;
   Element root;
   Map map = new HashMap();
   public FlatRelations(File file) {
      // System.out.println("FlatRelations FILE=" + file.getName() + (file.exists() ? " EXISTS" : " MISSING"));
      this.file = file;
      if(file.exists()){
         reset();
      }
   }
   public void reset() {
      map = new HashMap(); // Initialize map before chance of errors.
      try{
         relations = DocumentHelper.readDocument(file);
         root = relations.getDocumentElement();
         addResourceToMap(root);
         // TODO: Log Catches properly
      }catch(javax.xml.parsers.ParserConfigurationException pce){
         System.out.println("FlatRelations 1: Could not parse " + file.toString());
      }catch(org.xml.sax.SAXException saxe){
         System.out.println("FlatRelations 2: Could not parse " + file.toString());
      }catch(java.io.IOException ioe){
         // File not found. Expected for non-Lenya-1.3 pubs.
      }
   }
   private void addResourceToMap(Element root) {
      NodeList resources = root.getElementsByTagName("resource");
      int length = resources.getLength();
      for(int i = 0; i < length; i++){
         Element resource = (Element) resources.item(i);
         map.put(resource.getAttribute("full"), resource.getAttribute("unid"));
         // System.out.println("FlatRelations " + resource.getAttribute("unid") + "=" + resource.getAttribute("full"));
         // Recurse is unnecessary as getElementsByTagName returns all descendants.
         // addResourceToMap(resource);
      }
   }
   public Element getRoot() {
      return root;
   }
   public String getUNID(String id) {
      if(null == map){
         System.out.println("FlatRelations.getUNID map=null");
         return "";
      }
      if(null == id){
         System.out.println("FlatRelations.getUNID unid=null");
         return "";
      }
      update();
      // Fix ID to start with one and only one slash.
      String workId = id;
      int pos = workId.indexOf('/');
      if(pos == 0){
         while('/' == workId.charAt(pos)){
            pos++;
         }
         workId = workId.substring(pos);
      }
      workId = '/' + workId;
      if(map.containsKey(workId)){
         // System.out.println("FlatRelations.getUNID FOUND " + id + " -> " + workId + " -> " + (String) map.get(workId));
         return (String) map.get(workId);
      }
      // System.out.println("FlatRelations.getUNID MISSING " + id + " -> " + workId);
      return "";
   }
   private void update() {
      // WORK: Reset if file modified
   }
}
