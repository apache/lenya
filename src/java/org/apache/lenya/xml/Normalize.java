/*
$Id: Normalize.java,v 1.9 2003/07/23 13:21:29 gregor Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.xml;


/**
 * @author Michael Wechner (http://cocoon.apache.org/lenya)
 * @version 0.9.14
 * @deprecated 
 */
public class Normalize {
    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        System.out.println(Normalize.normalize("&"));
        System.out.println(Normalize.denormalize("Z&#252;rich"));
        System.out.println(Normalize.denormalize("Z&252;rich &#38; Region&#787dj&#356;"));
    }

    /**
     *
     */
    public static String normalize(String s) {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);

            switch (ch) {
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

            case 225: // aacute
             {
                sb.append("&#225;");

                break;
            }

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

            case 233: // eacute
             {
                sb.append("&#233;");

                break;
            }

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

            default:sb.append(ch);
            }
        }

        return sb.toString();
    }

    /**
     *
     */
    public static String denormalize(String s) {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);

            if (ch == '&') {
                StringBuffer substring = new StringBuffer();
                int length = i + 6;

                if (length > s.length()) {
                    length = s.length();
                }

                for (int k = i; k < length; k++) {
                    substring.append(s.charAt(k));

                    if (s.charAt(k) == ';') {
                        break;
                    }
                }

                if (substring.length() > 3) {
                    if ((substring.charAt(1) != '#') ||
                            (substring.charAt(substring.length() - 1) != ';')) {
                        sb.append(ch);
                    } else {
                        int ascii = 0;
                        int power = 1;

                        for (int j = substring.length() - 2; j >= 2; j--) {
                            try {
                                Integer integer = new Integer("" + substring.charAt(j));
                                ascii = ascii + (power * integer.intValue());
                            } catch (Exception e) {
                                ascii = -1;

                                break;
                            }

                            power = power * 10;
                        }

                        if (ascii >= 0) {
                            char character = (char) ascii;
                            sb.append(character);
                            i = (i + substring.length()) - 1;
                        } else {
                            sb.append(ch);
                        }
                    }
                } else {
                    sb.append(ch);
                }
            } else {
                sb.append(ch);
            }
        }

        return sb.toString();
    }
}
