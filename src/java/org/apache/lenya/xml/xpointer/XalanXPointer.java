package org.wyona.xml.xpointer;

import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import org.wyona.xml.*;

import org.apache.log4j.Category;

/**
 * @author Michael Wechner, wyona
 * @version 0.4.16
 */
public class XalanXPointer implements XPointer{
  static Category log=Category.getInstance(XalanXPointer.class);
/**
 *
 */
     public static void main(String[] args)
          {
          XPointer xpointer=new XalanXPointer();
          if(args.length != 2)
            {
            System.err.println("Usage: java "+xpointer.getClass().getName()+" example.xml \"/Example/People/Person[position() < 2]/Street/@Number\"");
            return;
            }
          DOMParserFactory dpf=new DOMParserFactory();
          Document document=null;
          try
            {
            document=dpf.getDocument(args[0]);
            }
          catch(Exception e)
            {
            System.err.println(xpointer.getClass().getName()+".main(): "+e);
            }
          Element root=document.getDocumentElement();
          String xpath=args[1];
          try
            {
            Vector nodes=xpointer.select(root,xpath);

            for(int i=0;i<nodes.size();i++)
               {
               Node node=(Node)nodes.elementAt(i);
               short type=node.getNodeType();
               if(type == Node.ATTRIBUTE_NODE)
                 {
                 System.out.println("Attribute ("+node.getNodeName()+"): "+node.getNodeValue());
                 }
               else if(type == Node.ELEMENT_NODE)
                 {
                 System.out.println("Element ("+node.getNodeName()+"): "+node.getFirstChild().getNodeValue());
                 }
               }
            }
          catch(Exception e)
            {
            System.err.println(e);
            }
          }
/**
 * @exception Exception ...
 */
     public Vector select(Node node,String xpath) throws Exception
          {
          //System.err.println("XalanXPointer: "+xpath);
          //System.err.println("XalanXPointer: "+node.getNodeName());
          NodeList children=node.getChildNodes();
/*
          for(int i=0;i<children.getLength();i++)
             {
             System.err.println(((Node)children.item(i)).getNodeName());
             }
*/

          log.debug(node.getNodeName()+"  "+xpath);
          //NodeList nl=null; //new XPathAPI().selectNodeList(node,xpath);
          NodeList nl=new org.apache.xpath.XPathAPI().selectNodeList(node,xpath);
          //System.err.println("XalanXPointer: "+nl.getLength());

/*
          NodeList kinder=node.getChildNodes();
          for(int i=0;i<kinder.getLength();i++)
             {
             System.err.println(((Node)kinder.item(i)).getNodeName());
             }
*/

          Vector nodes=new Vector();
          for(int i=0;i<nl.getLength();i++)
             {
             nodes.addElement(nl.item(i));
             }
          return nodes;
          }
     }
