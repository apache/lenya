/*
 * <License>
 * =======================================================================
 * Copyright (c) 2000 wyona. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 * 
 * 3. All advertising materials mentioning features or use of this
 *    software must display the following acknowledgment:
 *    "This product includes software developed by wyona (http://www.wyona.org)"
 * 
 * 4. The name "wyona" must not be used to endorse or promote products
 *    derived from this software without prior written permission.
 *    For written permission , please contact contact@wyona.org
 * 
 * 5. Products derived from this software may not be called "wyona"
 *    nor may "wyona" appear in their names without prior written
 *    permission of wyona. 
 * 
 * 6. Redistributions of any form whatsoever must retain the following
 *    acknowledgment:
 *    "This product includes software developed by wyona (http://www.wyona.org)"
 * 
 * THIS SOFTWARE IS PROVIDED BY wyona "AS IS" WITHOUT ANY WARRANTY 
 * EXPRESS OR IMPLIED, INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND
 * THE IMPLIED WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE. wyona WILL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY YOU AS
 * A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL wyona BE LIABLE FOR
 * ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN
 * IF wyona HAS BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE.
 * wyona WILL NOT BE LIABLE FOR ANY THIRD PARTY CLAIMS AGAINST YOU.
 * =======================================================================
</License>
 */

package org.wyona.xml;

import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import org.wyona.xml.*;
import org.wyona.xml.xpointer.*;
import org.wyona.util.StringUtil;
import org.wyona.xml.parser.XercesParser;

import org.apache.log4j.Category;

/**
 * @author Michael Wechner, wyona
 * @version 0.4.16
 */
public class XPointerFactory
     {
     static Category log=Category.getInstance(XPointerFactory.class);
     XPointer xpointer=null;
/**
 *
 */
     public  static void main(String[] args)
          {
          XPointerFactory xpf=new XPointerFactory();
          DOMParserFactory dpf=new DOMParserFactory();
          if(args.length != 2)
            {
            System.err.println("Usage: java "+xpf.getClass().getName()+" example.xml \"/Example/People/Person[1]/Name\"");
            return;
            }

          Document document=null;
          try
            {
            document=dpf.getDocument(args[0]);
            }
          catch(FileNotFoundException e)
            {
            System.err.println("No such file or directory: "+e.getMessage());
            }
          catch(Exception e)
            {
            System.err.println(e.getMessage());
            }
          String xpath=args[1];
          try
            {
            Vector nodes=xpf.select(document.getDocumentElement(),"xpointer("+xpath+")");
            String[] values=xpf.getNodeValues(nodes);
            for(int i=0;i<nodes.size();i++)
               {
               System.out.println(((Node)nodes.elementAt(i)).getNodeName()+": "+values[i]);
               }
            }
          catch(Exception e)
            {
            System.err.println(xpf.getClass().getName()+".main(): "+e);
            }

          Document doc=xpf.employees();
          try
            {
            Vector nodes=xpf.select(doc.getDocumentElement(),"xpointer(/Employees/Employee[2])");
            String[] values=xpf.getNodeValues(nodes);
            for(int i=0;i<nodes.size();i++)
               {
               System.out.println(((Node)nodes.elementAt(i)).getNodeName()+": "+values[i]);
               }

            Element leviElement=(Element)nodes.elementAt(0);
            leviElement.appendChild(dpf.newTextNode(doc," Brucker"));
            }
          catch(Exception e)
            {
            System.err.println(xpf.getClass().getName()+".main(): "+e);
            }
          new DOMWriter(new PrintWriter(System.out)).print(doc);
          System.out.println("");
          }
/**
 *
 */
     public XPointerFactory()
          {
          Properties properties=new Properties();
          String propertiesFileName="conf.properties";
          try
            {
            properties.load(XPointerFactory.class.getResourceAsStream(propertiesFileName));
            }
          catch(Exception e)
            {
            log.fatal(": Failed to load properties from resource: "+propertiesFileName);
            log.fatal("System.exit(0)");
            System.exit(0);
            }

          String xpointerName=properties.getProperty("XPointer");
          if(xpointerName == null)
            {
            log.fatal(": No XPointer specified in "+propertiesFileName);
            log.fatal("System.exit(0)");
            System.exit(0);
            }

          try
            {
            Class xpointerClass=Class.forName(xpointerName);
            xpointer=(XPointer)xpointerClass.newInstance();
            }
          catch(Exception e)
            {
            log.fatal(": "+e);
            log.fatal("System.exit(0)");
            System.exit(0);
            }
          }
/**
 * @exception MalformedXPointerException xpointer(xpath)
 */
     public Vector parse(String reference) throws MalformedXPointerException
          {
          Vector xpaths=new Vector();
          tokenize(reference,xpaths);
          return xpaths;
          }
/**
 * @exception Exception ...
 */
     public Vector select(Node node,String reference) throws Exception
          {
          Vector xpaths=parse(reference);

          Vector nodes=new Vector();
          for(int i=0;i<xpaths.size();i++)
             {
             Vector n=xpointer.select(node,(String)xpaths.elementAt(i));
             //Vector n=xpointer.select(copyToXercesTree(node),(String)xpaths.elementAt(i)); // WORK AROUND
             for(int j=0;j<n.size();j++)
                {
                nodes.addElement(n.elementAt(j));
                //nodes.addElement(copyToDefaultTree((Node)n.elementAt(j))); // WORK AROUND
                }
             }
          return nodes;
          }
/**
 * @exception MalformedXPointerException xpointer(xpath)xpointer(xpath)
 */
     public void tokenize(String xpointer,Vector xpaths) throws MalformedXPointerException
          {
          if((xpointer.indexOf("xpointer(") == 0) && (xpointer.charAt(xpointer.length()-1) == ')'))
            {
            //System.err.println("XPointer: "+xpointer);
            String substring=xpointer.substring(9,xpointer.length());
            int i=substring.indexOf(")xpointer(");
            if(i >= 0)
              {
              xpaths.addElement(substring.substring(0,i));
              tokenize(substring.substring(i+1,substring.length()),xpaths);
              }
            else
              {
              xpaths.addElement(substring.substring(0,substring.length()-1));
              return;
              }
            }
          else
            {
            throw new MalformedXPointerException(xpointer);
            }
          }
/**
 *
 */
     public String[] getNodeValues(Vector nodes) throws Exception
          {
          String[] values=new String[nodes.size()];
          for(int i=0;i<values.length;i++)
             {
             Node node=(Node)nodes.elementAt(i);
             short type=node.getNodeType();
             switch(type)
                   {
                   case Node.ELEMENT_NODE:
                      {
                      values[i]=getElementValue((Element)node);
                      break;
                      }
                   case Node.ATTRIBUTE_NODE:
                      {
                      values[i]=node.getNodeValue();
                      break;
                      }
                   default:
                      values[i]="";
                      throw new Exception("Neither ELEMENT nor ATTRIBUTE: "+type);
                   }
             }
          return values;
          }
/**
 *
 */
     public String getElementValue(Element element)
          {
          String value="";
          NodeList nl=element.getChildNodes();
          for(int k=0;k<nl.getLength();k++)
             {
             short nodeType=nl.item(k).getNodeType();
             if(nodeType == Node.TEXT_NODE)
               {
               value=value+nl.item(k).getNodeValue();
               }
             else if(nodeType == Node.ELEMENT_NODE)
               {
               value=value+getElementValue((Element)nl.item(k));
               }
             else
               {
               System.err.println("EXCEPTION: "+this.getClass().getName()+".getElementValue(): No TEXT_NODE");
               }
             }
          return value;
          }
/**
 *
 */
     public void getElementValue(Element element,Vector text)
          {
          NodeList nl=element.getChildNodes();
          for(int k=0;k<nl.getLength();k++)
             {
             short nodeType=nl.item(k).getNodeType();
             if(nodeType == Node.TEXT_NODE)
               {
               text.addElement(nl.item(k).getNodeValue());
               }
             else if(nodeType == Node.ELEMENT_NODE)
               {
               getElementValue((Element)nl.item(k),text);
               }
             else
               {
               System.err.println("EXCEPTION: "+this.getClass().getName()+".getElementValue(): No TEXT_NODE");
               }
             }
          }
/**
 *
 */
/*
     public void getText(Node node,Vector text)
          {
          short type=node.getNodeType();
          switch(type)
                {
                case Node.TEXT_NODE:
                   {
                   String txt=StringUtil.moveEOLtoWhiteSpace(node.getNodeValue());
                   txt=StringUtil.removeWhiteSpaceFromBeginningAndEnd(txt);
                   if(txt.length() > 1)
                     {
                     text.addElement(txt);
                     }
                   else if(txt.length() == 1)
                     {
                     if(txt.charAt(0) != ' ')
                       {
                       text.addElement(txt);
                       }
                     }
                   break;
                   }
                default:
                   break;
                }
          if(node.hasChildNodes())
            {
            NodeList nl=node.getChildNodes();
            for(int i=0;i<nl.getLength();i++)
               {
               getText(nl.item(i),text);
               }
            }
          }
*/
/**
 *
 */
     public Document employees()
          {
          DOMParserFactory dpf=new DOMParserFactory();
          Document doc=dpf.getDocument();
          Element michi=dpf.newElementNode(doc,"Employee");
          michi.setAttribute("Id","0");
          michi.appendChild(dpf.newTextNode(doc,"Michi"));
          Element levi=dpf.newElementNode(doc,"Employee");
          levi.setAttribute("Id","1");
          levi.appendChild(dpf.newTextNode(doc,"Levi"));
          Element employees=dpf.newElementNode(doc,"Employees");
          employees.appendChild(dpf.newTextNode(doc,"\n"));
          employees.appendChild(michi);
          employees.appendChild(dpf.newTextNode(doc,"\n"));
          employees.appendChild(levi);
          employees.appendChild(dpf.newTextNode(doc,"\n"));
          doc.appendChild(employees);
          
          return doc;
          }
     }
