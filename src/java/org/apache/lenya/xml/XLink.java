/*
<License>
 * =======================================================================
 * Copyright (c) 2000 lenya. All rights reserved.
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
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 * 
 * 4. The name "lenya" must not be used to endorse or promote products
 *    derived from this software without prior written permission.
 *    For written permission , please contact contact@lenya.org
 * 
 * 5. Products derived from this software may not be called "lenya"
 *    nor may "lenya" appear in their names without prior written
 *    permission of lenya. 
 * 
 * 6. Redistributions of any form whatsoever must retain the following
 *    acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 * 
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY 
 * EXPRESS OR IMPLIED, INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND
 * THE IMPLIED WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY YOU AS
 * A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE FOR
 * ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN
 * IF lenya HAS BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE.
 * lenya WILL NOT BE LIABLE FOR ANY THIRD PARTY CLAIMS AGAINST YOU.
 * =======================================================================
</License>
 */

package org.apache.lenya.xml;

import org.w3c.dom.*;

/**
 * @author Michael Wechner, lenya
 * @version 0.4.27
 */
public class XLink
     {
     public String type=null;
     public String href=null;
     public String show=null;
     public String name=null;

     public Element element=null;
/**
 *
 */
     public XLink()
          {
          type="simple";
          show="undefined";
          }
/**
 *
 */
     public XLink(Element element)
          {
          this();
          this.element=element;

          name=element.getNodeName();

          NamedNodeMap attributes=element.getAttributes();
          for(int i=0;i<attributes.getLength();i++)
             {
             Node attribute=attributes.item(i);
            
             if(attribute.getNodeName().equals("xlink:href"))
               {
               href=attribute.getNodeValue();
               }
             else if(attribute.getNodeName().equals("xlink:type"))
               {
               type=attribute.getNodeValue();
               }
             else if(attribute.getNodeName().equals("xlink:show"))
               {
               show=attribute.getNodeValue();
               }
             else
               {
               }
             }
          }
/**
 *
 */
     public Element getXLink(Document document,DOMParserFactory dpf) {
	 return (Element)dpf.cloneNode(document,element,true);
     }
/**
 *
 */
  public String toString(){
    return "XLink: type=\""+type+"\", href=\""+href+"\", show=\""+show+"\", name=\""+name+"\"";
    }
     }
