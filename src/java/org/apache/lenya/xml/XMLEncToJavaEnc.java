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

/* $Id: XMLEncToJavaEnc.java,v 1.12 2004/03/01 16:18:23 gregor Exp $  */

package org.apache.lenya.xml;

import java.util.Hashtable;


/**
 * DOCUMENT ME!
 * @deprecated 
 */
public class XMLEncToJavaEnc extends Hashtable {
    private static String DEFAULT_ENCODING = "utf-8";

    private XMLEncToJavaEnc() {
        // JAVA supports a lot more...
        // http://www.w3.org/International/O-charset-list.html
        //
        put("ascii", "ASCII");
        put("iso-8859-1", "ISO8859_1"); // Latin 1
        put("iso-8859-2", "ISO8859_2"); // Latin 2
        put("iso-8859-3", "ISO8859_3");
        put("iso-8859-4", "ISO8859_4");
        put("iso-8859-5", "ISO8859_5");
        put("iso-8859-6", "ISO8859_6");
        put("iso-8859-7", "ISO8859_7");
        put("iso-8859-8", "ISO8859_8");
        put("iso-8859-9", "ISO8859_9");
        put("big-5", "Big5"); // Traditional Chinese

        put("cp-874", "Cp874"); // IBM Thai

        //put("cp-932",???);
        put("cp-950", "Cp950"); // PC Chinese (Hongkong, Taiwan)
        put("cp-1250", "Cp1250"); // Windows Eastern Europe
        put("cp-1251", "Cp1251"); // Windows Cyrillic
        put("cp-1252", "Cp1252"); // Windows Latin 1
        put("cp-1253", "Cp1253"); // Windows Greek
        put("cp-1255", "Cp1255"); // Windows Hebrew
        put("cp-1256", "Cp1256"); // Windows Arabic
        put("cp-1257", "Cp1257"); // Windows Baltic
        put("cp-1258", "Cp1258"); // Windows Vietnamese
        put("euc-jp", "EUC_JP"); // JIS0201, 0208, 0212, EUC Encoding, Japanese
        put("euc-kr", "EUC_KR"); // KS C 5601, EUC Encoding, Korean

        put("iso-2022-jp", "ISO2022JP"); // JIS0201, 0208, 0212, ISO2022 Encoding, Japanese
        put("iso-2022-kr", "ISO2022KR"); // ISO 2022 KR, Korean

        put("koi8-r", "KOI8_R"); // KOI8-R, Russian
        put("shift_jis", "SJIS"); // Shift-JIS, Japanese

        put("utf-8", "UTF8");

        put("euc-tw", "EUC_TW"); // CNS11643 (Plane 1-3), T. Chinese, EUC encoding
        put("x-mac-roman", "MacRoman"); // Macintosh Roman
        put("x-mac-ce", "MacCentralEurope"); // Macintosh Latin-2
        put("x-mac-greek", "MacGreek"); // Macintosh Greek
        put("x-mac-turkish", "MacTurkish"); // Macintosh Turkish
        put("x-mac-cyrillic", "MacCyrillic"); // Macintosh Cyrillic
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        System.out.println(XMLEncToJavaEnc.getJava("utf-8"));
    }

    /**
     * DOCUMENT ME!
     *
     * @param xmlencoding DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String getJava(String xmlencoding) {

        try {
            return ((String) ((new XMLEncToJavaEnc()).get(xmlencoding.toLowerCase())));
        } catch (Exception e) {
            System.err.println("Unsupported Encoding; reverting to " + DEFAULT_ENCODING);

            return DEFAULT_ENCODING;
        }
    }
}
