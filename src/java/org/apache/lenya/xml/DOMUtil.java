/*
  <License>
  </License>
*/

package org.wyona.xml;

import java.io.StringBufferInputStream;
import java.util.StringTokenizer;
import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.apache.log4j.Category;

/**
 * @author Michael Wechner
 * @created 1.7.23
 * @version 1.7.24
 */

public class DOMUtil{

  static Category log=Category.getInstance(DOMUtil.class);
  DOMParserFactory dpf=null;
  XPointerFactory xpf=null;

  /**
   *
   */
  public static void main(String[] args){
    try{
      DOMUtil du=new DOMUtil();
      Document document=du.create("<?xml version=\"1.0\"?><Artikel><Datum><Monat Name=\"Juli\"/><Tag>23</Tag></Datum><Content/></Artikel>");
      new DOMWriter(System.out).printWithoutFormatting(document);
      du.setElementValue(document,"/Artikel/Datum/Tag","25");
      du.setElementValue(document,"/Artikel/Datum/Monat","7");
      du.setElementValue(document,"/Artikel/Datum/Monat","9");
      du.setElementValue(document,"/Artikel/Datm/Mont","13");
      du.setAttributeValue(document,"/Artikel/Datum/Monat/@Name","Oktober");
      du.setAttributeValue(document,"/Artikel/Datu/Monat/@Nam","August");
      du.setElementValue(document,"/Artikel/Datu/Monat","8");
      du.addElement(document,"/Artikel/Datum/Tag","26");
      du.setElementValue(document,"/Artikel/Datum/Tag","24");

      new DOMWriter(System.out).printWithoutFormatting(document);
      System.out.print("\n");
      System.out.print("\n");

      String[] elements = du.getAllElementValues(document, new XPath("/Artikel/Datum/Monat"));
      for (int i=0; i<elements.length;i++) {
        System.out.println("Elements="+elements[i]);
      } 
      
      System.out.print("\n");
      System.out.println("Datum/Monat="+du.getElementValue(document.getDocumentElement(),new XPath("Datum/Monat")));
      System.out.println("Datm="+du.getElementValue(document.getDocumentElement(),new XPath("Datm")));
      //   System.out.println("Datum/Tag="+du.getElementValue(document.getDocumentElement(),new XPath("Datum/Tag")));
      //System.out.println("/Artikel/Datum/Tag="+du.getElementValue(document,new XPath("/Artikel/Datum/Tag")));
      System.out.println("Datum/Monat/@Name="+du.getAttributeValue(document.getDocumentElement(),new XPath("Datum/Monat/@Name")));
    }
    catch(Exception e){
      System.err.println(e);
    }
  }
  /**
   *
   */
  public DOMUtil(){
    dpf=new DOMParserFactory();
    xpf=new XPointerFactory();
  }

  /**
   *
   */
  public Document create(String xml) throws Exception{
    return dpf.getDocument(new StringBufferInputStream(xml));
  }

  /**
   *
   */
  public Element[] select(Document document,String xpath) throws Exception{
    log.debug(".select(): "+xpath);
    Vector nodes=xpf.select(document,"xpointer("+xpath+")");
    Element[] elements=new Element[nodes.size()];
    for(int i=0;i<nodes.size();i++){
      elements[i]=(Element)nodes.elementAt(i);
    }
    return elements;
  }

  /**
   *
   */
  public void replaceText(Element element,String text){
    NodeList nl=element.getChildNodes();
    for(int i=nl.getLength()-1;i>=0;i--){
      element.removeChild(nl.item(i));
    }
    element.appendChild(dpf.newTextNode(element.getOwnerDocument(),text));
  }

  /**
   *
   */
  public String getElementValue(Document document, XPath xpath) throws Exception{
    return getElementValue(document.getDocumentElement(),xpath);
  }

  /**
   *
   */
  public String getElementValue(Element element, XPath xpath) throws Exception {
    String value="";
    NodeList nl=getElement(element, xpath).getChildNodes();
    for(int i=0;i<nl.getLength();i++){
      short nodeType=nl.item(i).getNodeType();
      if(nodeType == Node.TEXT_NODE){
        value=value+nl.item(i).getNodeValue();
      } else{
        log.warn("XPath "+xpath+" contains node types other than just TEXT_NODE");
      }
    }
    return value;
  }

  /**
   *
   */
  public Element getElement(Element element, XPath xpath) throws Exception {
    log.debug(xpath);
    if(xpath.parts.length > 0){
      NodeList nl=element.getElementsByTagName(xpath.parts[0]);
      if(nl.getLength() == 0){
        throw new Exception("There are no elements with Name \""+xpath.parts[0]+"\".");
      } else if (nl.getLength() == 1) {
        log.debug("There is one element with Name \""+xpath.parts[0]+"\" ("+xpath.parts.length+").");
        if(xpath.parts.length == 1){
          return (Element)nl.item(0);
        } else {
          String newXPathString=xpath.parts[1];
          for(int i=2;i<xpath.parts.length;i++){
            newXPathString=newXPathString+"/"+xpath.parts[i];
          }
          return getElement((Element)nl.item(0),new XPath(newXPathString));
        }
      } else{
        throw new Exception("There are more elements than one with Name \""+xpath.parts[0]+"\".");
      }
    }
    return null;
  }

  /**
   * get all elements with |xpath|, xpath has to start with the root node
   *
   * @param document a value of type 'Document'
   * @param xpath a value of type 'XPath'
   * @return a value of type 'Element[]'
   * @exception Exception if an error occurs
   */
  public Element[] getAllElements(Document document, XPath xpath) throws Exception {
    Vector nodes=xpf.select(document.getDocumentElement(),"xpointer("+xpath.toString()+")");
    Element[] elements=new Element[nodes.size()];
    for (int i=0;i<nodes.size();i++) {
      elements[i]=(Element)nodes.elementAt(i);
    } 
    return elements;
  }

  /**
   * get all elements values from |xpath|, xpath has to start with the root node
   *
   * @param document a value of type 'Document'
   * @param xpath a value of type 'XPath'
   * @return a value of type 'String[]'
   * @exception Exception if an error occurs
   */
  public String[] getAllElementValues(Document document, XPath xpath) throws Exception {
    Vector nodes=xpf.select(document.getDocumentElement(),"xpointer("+xpath.toString()+")");
    log.debug("n elements "+nodes.size());
    String[] values=new String[nodes.size()];
    for (int i=0;i<nodes.size();i++) {
      values[i]=getElementValue((Element)nodes.elementAt(i));
    } 
    return values;
  }


  /**
   *
   */
  public String getElementValue(Element element) throws Exception {
    String value="";
    NodeList nl=element.getChildNodes();
    for(int i=0;i<nl.getLength();i++){
      short nodeType=nl.item(i).getNodeType();
      if(nodeType == Node.TEXT_NODE){
        value=value+nl.item(i).getNodeValue();
      } else{
        log.warn("Element "+element.getNodeName()+" contains node types other than just TEXT_NODE");
      }
    }
    return value;
  }

//   public Element[] getAllElements(Element element, XPath xpath) throws Exception {
//     log.debug(xpath);
//     if (xpath.parts.length > 0){
//       NodeList nl = element.getElementsByTagName(xpath.parts[0]);
//       log.debug("nodelist: "+nl.getLength());
//       if (nl.getLength() == 0){
//         throw new Exception("There are no elements with Name \""+xpath.parts[0]+"\".");
//       } else {
//         log.debug("xpath: "+xpath.parts.length);
//         Vector el = null;
//         if (xpath.parts.length == 1){
//           log.debug("path is 1 long");
//           for (int i=0; i<nl.getLength(); i++) {
//             el.addElement( nl.item(i) );
//           } 
//         } else {
//           String newXPathString=xpath.parts[1];
//           for(int i=2;i<xpath.parts.length;i++){
//             newXPathString=newXPathString+"/"+xpath.parts[i];
//           }
//           XPath newxpath = new XPath(newXPathString);
//           log.debug("newxpath: "+newxpath.toString());
//           for (int i=0; i<nl.getLength(); i++) {
//             log.debug("node: "+i+" "+ nl.item(i));
//             el.addElement( getElement((Element)nl.item(i), newxpath) );
//           } 
//         }
//         log.debug("--------returning: "+el.size());
//         return (Element[])el.toArray();
//       }
//     }
//     return null;
//   }

  /**
   * Return the value of an attribte named e.g. "this/myelement/@myattribute"
   *
   * @author Martin Lüthi
   * @version 2001.10.30
   *
   * @param element a value of type 'Element'
   * @param xpath a value of type 'XPath'
   * @return a value of type 'String'
   * @exception Exception if an error occurs
   */
  public String getAttributeValue(Element element, XPath xpath) throws Exception{
    Element el=getElement(element, new XPath(xpath.getElementName()));
    return el.getAttribute(xpath.getName());
  }

  /**
   * Describe 'setElementValue' method here.
   *
   * @param document a value of type 'Document'
   * @param xpath a value of type 'String'
   * @param value a value of type 'String'
   * @exception Exception if an error occurs
   */
  public void setElementValue(Document document,String xpath,String value) throws Exception{
    Element[] elements=select(document,xpath);
    if(elements.length >= 1){
      if(elements.length > 1){
        log.warn("There are more elements than one with XPath \""+xpath+"\". The value of the first element will be replaced");
      }
      replaceText(elements[0],value);
    } 
    else{
      XPath xp=new XPath(xpath);
      log.warn("XPath does not exist, but will be created: "+xp);
      Element element=(Element)createNode(document,xp);
      replaceText(element,value);
    }
  }

  /**
   *
   */
  public void addElement(Document document,String xpath,String value) throws Exception{
    XPath xp=new XPath(xpath);
    Node parent=createNode(document,xp.getParent());
    Element element=dpf.newElementNode(document,xp.getName());
    parent.appendChild(element);
    if(value != null){
      element.appendChild(dpf.newTextNode(element.getOwnerDocument(),value));
    }
  }

  /**
   *
   */
  public void setAttributeValue(Document document,String xpath,String value) throws Exception{
    Vector nodes=xpf.select(document,"xpointer("+xpath+")");
    if(nodes.size() >= 1){
      Attr attribute=(Attr)nodes.elementAt(0);
      attribute.setValue(value);
    } 
    else{
      XPath xp=new XPath(xpath);
      log.debug("XPath does not exist, but will be created: "+xp);
      Attr attribute=(Attr)createNode(document,xp);
      attribute.setValue(value);
    }
  }

  /**
   *
   */
  public void setValue(Document document,XPath xpath,String value) throws Exception{
    short type=xpath.getType();
    if(type==Node.ATTRIBUTE_NODE){
      setAttributeValue(document,xpath.toString(),value);
    }
    else if(type== Node.ELEMENT_NODE){
      setElementValue(document,xpath.toString(),value);
    }
    else{
      log.error("No such type: "+type);
    }
  }

  /**
   *
   */
  public Node createNode(Document document,XPath xpath) throws Exception{
    log.debug(xpath);
    Node node=null;
    Vector nodes=xpf.select(document,"xpointer("+xpath+")");

    if(nodes.size() >= 1){
      node=(Node)nodes.elementAt(0);
    }
    else{
      Node parentNode=createNode(document,xpath.getParent());
      if(xpath.getType() == Node.ATTRIBUTE_NODE){
        ((Element)parentNode).setAttribute(xpath.getNameWithoutPredicates(),"null");
        node=((Element)parentNode).getAttributeNode(xpath.getNameWithoutPredicates());
      }
      else{
        node=dpf.newElementNode(document,xpath.getNameWithoutPredicates());
        parentNode.appendChild(node);
      }
    }
    return node;
  }
}
