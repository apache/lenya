/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

/* $Id: Normalize.java,v 1.10 2004/03/01 16:18:23 gregor Exp $  */

package org.apache.lenya.xml;


/**
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
