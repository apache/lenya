package org.apache.lenya.cms.content.flat;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.lenya.xml.DocumentHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class FlatRelations {
   File file;
   Document relations;
   Element root;
   Map map;

   public FlatRelations(File pfile){
      file = pfile;
      reset();
   }

   public void reset(){
      try{
         relations = DocumentHelper.readDocument(file);
         map = new HashMap();
         root = relations.getDocumentElement();
         addResourceToMap(root);
//WORK: Log Catches properly
      }catch(javax.xml.parsers.ParserConfigurationException pce){
         System.out.println("FlatRelations 1: Could not parse " + file.toString());
      }catch(org.xml.sax.SAXException saxe){
         System.out.println("FlatRelations 2: Could not parse " + file.toString());
      }catch(java.io.IOException ioe){
         // File not found.  Expected for non-Lenya1.3 pubs.
      }
   }
   private void addResourceToMap(Element root){
         NodeList resources = root.getElementsByTagName("resource");
         int length = resources.getLength();
         for(int i = 0; i < length; i++){
            Element resource = (Element) resources.item(i);
            map.put(resource.getAttribute("full"), resource.getAttribute("unid"));
            addResourceToMap(resource);
         }
   }
   public Element getRoot(){ return root; }
   public String getUNID(String unid){
      update();
      if(map.containsKey(unid)) return (String) map.get(unid);
      return "";
   }
   private void update(){
//WORK: Reset if file modified
   }

}
