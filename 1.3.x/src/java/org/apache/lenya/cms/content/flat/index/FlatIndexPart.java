package org.apache.lenya.cms.content.flat.index;

import java.util.StringTokenizer;
import org.w3c.dom.Element;

public class FlatIndexPart {
   String name="";
   String property = "";
   String value = "";

   public FlatIndexPart(Element element){
      if(element.hasAttribute("property")){
         property = element.getAttribute("property");
         name = property;
      }
      if(element.hasAttribute("name")) property = element.getAttribute("name");

      if(null != element.getFirstChild()){
         value = element.getFirstChild().getNodeValue();
         if(null == value) value = "";
      }
   }
   public FlatIndexPart(String variable){
      name = variable;
      property = variable;
   }
   public String getName(){ return name; }
   public String getProperty(){ return property; }
   public String getValue(){ return value; }

   public boolean check(String test){
//System.out.print(" T=" + test + " V=" + value);
      if(test.length() < 1) return true;
      if(test.equals("/")) return true; //Structured indexes add slash prefix.
      if(test.equalsIgnoreCase("true")) return true;
      String token;
      StringTokenizer tokens = new StringTokenizer(value, "|,;");
      do{
         token = tokens.nextToken();
         if(test.equals(token)) return true;
         if(test.endsWith(token)) return true;
      }while(tokens.hasMoreTokens());
//System.out.print(" FALSE ");
      return false;
   }
}
