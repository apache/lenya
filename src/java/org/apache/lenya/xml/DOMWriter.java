/*
 <License>
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

import org.apache.log4j.Category;

/**
 * @author Michael Wechner (http://www.wyona.com)
 * @author Roger Lacher (http://www.wyona.com)
 * @version 0.9.26
 */
public class DOMWriter
     {
     static Category log=Category.getInstance(DOMWriter.class);
     PrintWriter out=null;
     String encoding=null;
/**
 *
 */
     public static void main(String[] args)
          {
          if(args.length != 1)
            {
            System.err.println("Usage: java org.wyona.xml.DOMWriter \"file.xml\"");
            System.err.println("Description: Reads \"file.xml\" and writes it to standard output");
            return;
            }
          DOMParserFactory dpf=new DOMParserFactory();
          Document document=null;
          try
            {
            document=dpf.getDocument(args[0]);
            }
          catch(FileNotFoundException e)
            {
            System.err.println("No such file: "+e.getMessage());
            return;
            }
          catch(Exception e)
            {
            System.err.println(e.getMessage());
            return;
            }

          try
            {
            //new DOMWriter(System.out,"utf-8").printWithoutFormatting(document);
            new DOMWriter(System.out,"iso-8859-1").printWithoutFormatting(document);
            }
          catch(Exception e)
            {
            System.err.println(e.getMessage());
            return;
            }
          log.fatal("\n");
          

          log.fatal(".main(): System.exit(0)");
          System.exit(0);

          new DOMWriter(new PrintWriter(System.out)).print(document);
          System.out.print("\n");

          XPointerFactory xpf=new XPointerFactory();
          try
            {
            Vector nodes=xpf.select(document.getDocumentElement(),"xpointer(/Example/People/Person/City)");
            String[] values=xpf.getNodeValues(nodes);
            for(int i=0;i<values.length;i++)
               {
               System.out.println(values[i]);
               }
            Document doc=dpf.getDocument();
            Element root=dpf.newElementNode(doc,"Root");
//
            for(int i=0;i<values.length;i++)
               {
               root.appendChild(dpf.newTextNode(doc,values[i]));
               }
//
/*
            Vector text=new Vector();
            xpf.getText((Node)nodes.elementAt(0),text);
            String string="";
            for(int i=0;i<text.size();i++)
               { 
               string=string+(String)text.elementAt(i);
               }
            root.appendChild(dpf.newTextNode(doc,string));
*/
            doc.appendChild(root);
            new DOMWriter(new PrintWriter(System.out)).print(doc);
            System.out.print("\n");
            }
          catch(Exception e)
            {
            System.err.println(e);
            }
          }
/**
 *
 */
     public DOMWriter(PrintWriter out)
          {
          this.out=out;
          }
/**
 *
 */
     public DOMWriter(PrintWriter out,String encoding)
          {
          this(out);
          this.encoding=encoding;
          }
/**
 *
 */
     public DOMWriter(OutputStream os) throws Exception
          {
          this(os,"utf-8");
          //this.encoding="utf-8";
          }
/**
 *
 */
     public DOMWriter(OutputStream os,String encoding) throws Exception
          {
          out=new PrintWriter(new BufferedWriter(new OutputStreamWriter(os,XMLEncToJavaEnc.getJava(encoding))));
          this.encoding=encoding;
          }
/**
 *
 */
     public void print(Node node)
          {
          if(node == null)
            {
            return;
            }
          short type=node.getNodeType();
          switch(type)
                {
                case Node.DOCUMENT_NODE:
                     {
                     out.print("<?xml version=\"1.0\"");
                     if(encoding != null)
                       {
                       out.print(" encoding=\""+encoding+"\"");
                       }
                     out.print("?>\n\n");
                     print(((Document)node).getDocumentElement());
                     out.flush();
                     break;
                     }
                case Node.ELEMENT_NODE:
                     {
                     out.print("<"+node.getNodeName());
                     NamedNodeMap attributes=node.getAttributes();
                     for(int i=0;i<attributes.getLength();i++)
                        {
                        Node attribute=attributes.item(i);
                        out.print(" "+attribute.getNodeName()+"=\""+Normalize.normalize(attribute.getNodeValue())+"\"");
                        }
                     if(node.hasChildNodes())
                       {
                       out.print(">");
                       NodeList children=node.getChildNodes();
                       for(int i=0;i<children.getLength();i++)
                          {
                          print(children.item(i));
                          }
                       out.print("</"+node.getNodeName()+">");
                       }
                     else
                       {
                       out.print("/>");
                       }
                     break;
                     }
                case Node.TEXT_NODE:
                     {
                     out.print(Normalize.normalize(node.getNodeValue()));
                     break;
                     }
                case Node.COMMENT_NODE:
                     {
                     out.print("<!--"+node.getNodeValue()+"-->");
                     break;
                     }
                default:
                     {
                     System.err.println(this.getClass().getName()+".print(): Node type not implemented: "+type);
                     break;
                     }
                }
          }
/**
 *
 */
     public void printWithoutFormatting(Node node)
          {
          if(node == null)
            {
            return;
            }
          short type=node.getNodeType();
          switch(type)
                {
                case Node.DOCUMENT_NODE:
                     {
                     out.print("<?xml version=\"1.0\"");
                     if(encoding != null)
                       {
                       out.print(" encoding=\""+encoding+"\"");
                       }
                     out.print("?>\n\n");
					 Element root=((Document)node).getDocumentElement();
					 root.setAttribute("xmlns:xlink","http://www.w3.org/xlink");
                     printWithoutFormatting(root);
                     out.flush();
                     break;
                     }
                case Node.ELEMENT_NODE:
                     {
                     out.print("<"+node.getNodeName());
                     NamedNodeMap attributes=node.getAttributes();
                     for(int i=0;i<attributes.getLength();i++)
                        {
                        Node attribute=attributes.item(i);
                        out.print(" "+attribute.getNodeName()+"=\""+replaceSpecialCharacters(attribute.getNodeValue())+"\"");
                        }
                     if(node.hasChildNodes())
                       {
                       out.print(">");
                       NodeList children=node.getChildNodes();
                       for(int i=0;i<children.getLength();i++)
                          {
                          printWithoutFormatting(children.item(i));
                          }
                       out.print("</"+node.getNodeName()+">");
                       }
                     else
                       {
                       out.print("/>");
                       }
                     break;
                     }
                case Node.TEXT_NODE:
                     {
                     out.print(replaceSpecialCharacters(node.getNodeValue()));
                     break;
                     }
                case Node.COMMENT_NODE:
                     {
                     out.print("<!--"+node.getNodeValue()+"-->");
                     break;
                     }
                default:
                     {
                     System.err.println(this.getClass().getName()+".print(): Node type not implemented: "+type);
                     break;
                     }
                }
          }
/**
 *
 */
     public String replaceSpecialCharacters(String s)
          {
                  StringBuffer str = new StringBuffer();

        int len = (s != null) ? s.length() : 0;
        for ( int i = 0; i < len; i++ )
        {
            char ch = s.charAt(i);
            switch ( ch )
            {
                case '<':
                {
                    //str.append("&lt;");
                    str.append("&#60;");
                    break;
                }
                case '>':
                {
                    //str.append("&gt;");
                    str.append("&#62;");
                    break;
                }
                case '&':
                {
                    //str.append("&amp;");
                    str.append("&#38;");
                    break;
                }
                default:
                {
                    str.append(ch);
                }
            }
        }
        return (str.toString());
          }
     }
