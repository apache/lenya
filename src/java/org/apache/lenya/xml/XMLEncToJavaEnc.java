/*
$Id: XMLEncToJavaEnc.java,v 1.10 2004/02/02 02:50:36 stefano Exp $
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

import java.util.Hashtable;


/**
 * DOCUMENT ME!
 *
 * @author Roger Lacher, lenya
 * @version 0.5.8.
 * @deprecated 
 */
public class XMLEncToJavaEnc extends Hashtable {
    private static String DEFAULT_ENCODING = "utf-8";

    private XMLEncToJavaEnc() {
        // JAVA supports a lot more...
        // see:
        // http://lithium.lenya.ch/documentations/jdk1.2.2/docs/guide/internat/encoding.doc.html
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
