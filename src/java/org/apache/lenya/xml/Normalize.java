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

import org.wyona.xml.*;
import java.io.*;
import java.util.*;
import org.w3c.dom.*;

/**
 * @author Michael Wechner (http://www.wyona.com)
 * @version 0.9.14
 */
public class Normalize
     {
     public static void main(String[] args)
          {
          System.out.println(Normalize.normalize("&"));
          System.out.println(Normalize.denormalize("Z&#252;rich"));
          System.out.println(Normalize.denormalize("Z&252;rich &#38; Region&#787dj&#356;"));
          }
/**
 *
 */
     public static String normalize(String s)
          {
          StringBuffer sb=new StringBuffer();

          for(int i=0;i<s.length();i++)
             {
             char ch=s.charAt(i);
             switch(ch)
                   {
                   case '&': // 38
                      {
                      sb.append("&#38;");
                      break;
                      }
                   case 60: // <
                      {
                      sb.append("&#60;");
                      break;
                      }
                   case 62: // >
                      {
                      sb.append("&#62;");
                      break;
                      }
                   case 139: // <
                      {
                      sb.append("&#139;");
                      break;
                      }
                   case 155: // >
                      {
                      sb.append("&#155;");
                      break;
                      }
                   case 160: // nbsp
                      {
                      sb.append("&#160;");
                      break;
                      }
                   case 171: // <<
                      {
                      sb.append("&#171;");
                      break;
                      }
                   case 183: // .
                      {
                      sb.append("&#183;");
                      break;
                      }
                   case 187: // >>
                      {
                      sb.append("&#187;");
                      break;
                      }
                   case 196: // Ae
                      {
                      sb.append("&#196;");
                      break;
                      }
                   case 214: // Oe
                      {
                      sb.append("&#214;");
                      break;
                      }
                   case 220: // Ue
                      {
                      sb.append("&#220;");
                      break;
                      }
                   case 223: // Scharfes S
                      {
                      sb.append("&#223;");
                      break;
                      }
/*
                   case 225: // aacute
                      {
                      sb.append("&#225;");
                      break;
                      }
*/
                   case 228: // ae
                      {
                      sb.append("&#228;");
                      break;
                      }
                   case 232: // egrave
                      {
                      sb.append("&#232;");
                      break;
                      }
//
                   case 233: // eacute
                      {
                      sb.append("&#233;");
                      break;
                      }
//
                   case 234: // ecirc
                      {
                      sb.append("&#234;");
                      break;
                      }
                   case 244: // ocirc
                      {
                      sb.append("&#244;");
                      break;
                      }
                   case 246: // oe
                      {
                      sb.append("&#246;");
                      break;
                      }
                   case 252: // ue
                      {
                      sb.append("&#252;");
                      break;
                      }
                   default:
                      {
                      sb.append(ch);
                      }
                   }
             }
          return sb.toString();
          }
/**
 *
 */
     public static String denormalize(String s)
          {
          StringBuffer sb=new StringBuffer();

          for(int i=0;i<s.length();i++)
             {
             char ch=s.charAt(i);
             if(ch == '&')
               {
               StringBuffer substring=new StringBuffer();
               int length=i+6;
               if(length > s.length())
                 {
                 length=s.length();
                 }
               for(int k=i;k<length;k++)
                 {
                 substring.append(s.charAt(k));
                 if(s.charAt(k) == ';')
                   {
                   break;
                   }
                 }
               
               if(substring.length() > 3)
                 {
                 if((substring.charAt(1) != '#') || (substring.charAt(substring.length()-1) != ';'))
                   {
                   sb.append(ch);
                   }
                 else
                   {
                   int ascii=0;
                   int power=1;
                   for(int j=substring.length()-2;j>=2;j--)
                      {
                      //System.out.println(substring.charAt(j));
                      try
                        {
                        Integer integer=new Integer(""+substring.charAt(j));
                        ascii=ascii+power*integer.intValue();
                        }
                      catch(Exception e)
                        {
                        ascii=-1;
                        break;
                        }
                      power=power*10;
                      }
                   if(ascii >= 0)
                     {
                     //System.out.println("|"+substring.toString()+"|");
                     //System.out.println(ascii);
                     char character=(char)ascii;
                     sb.append(character);
                     i=i+substring.length()-1;
                     }
                   else
                     {
                     sb.append(ch);
                     }
                   }
                 }
               else
                 {
                 sb.append(ch);
                 }
               }
             else
               {
               sb.append(ch);
               }
             }
          return sb.toString();
          }
     }
