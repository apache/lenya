/*
 * $Id: XMLEncToJavaEnc.java,v 1.3 2003/03/04 17:46:47 gregor Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 wyona. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by wyona (http://www.wyona.org)"
 *
 * 4. The name "wyona" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@wyona.org
 *
 * 5. Products derived from this software may not be called "wyona" nor may "wyona"
 *    appear in their names without prior written permission of wyona.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by wyona (http://www.wyona.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY wyona "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. wyona WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL wyona BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF wyona HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. wyona WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Wyona includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.lenya.xml;

import java.util.*;


/**
 * DOCUMENT ME!
 *
 * @author Roger Lacher, wyona
 * @version 0.5.8.
 */
public class XMLEncToJavaEnc extends Hashtable {
    private static String DEFAULT_ENCODING = "utf-8";

    private XMLEncToJavaEnc() {
        // JAVA supports a lot more...
        // see:
        // http://lithium.wyona.ch/documentations/jdk1.2.2/docs/guide/internat/encoding.doc.html
        // and
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

        //put("asmo-708",???);
        put("euc-jp", "EUC_JP"); // JIS0201, 0208, 0212, EUC Encoding, Japanese
        put("euc-kr", "EUC_KR"); // KS C 5601, EUC Encoding, Korean

        put("iso-2022-jp", "ISO2022JP"); // JIS0201, 0208, 0212, ISO2022 Encoding, Japanese
        put("iso-2022-kr", "ISO2022KR"); // ISO 2022 KR, Korean

        put("koi8-r", "KOI8_R"); // KOI8-R, Russian
        put("shift_jis", "SJIS"); // Shift-JIS, Japanese

        put("utf-8", "UTF8");

        //put("ucs-2.",???);
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
        String javaencoding;

        try {
            return ((String) ((new XMLEncToJavaEnc()).get(xmlencoding.toLowerCase())));
        } catch (Exception e) {
            System.err.println("Unsupported Encoding; reverting to " + DEFAULT_ENCODING);

            return DEFAULT_ENCODING;
        }
    }
}
