/*
$Id: SSI.java,v 1.5 2003/07/23 13:21:14 gregor Exp $
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
package org.apache.lenya.util;

import java.io.*;


/*
import org.wyona.xpipe.*;
import org.wyona.xpipe.util.*;
import org.wyona.xpipe.filter.Filter;
import org.wyona.xpipe.Processor;
*/

/**
 *
 */
public class SSI {
    static String fileinc = "<!--#include file=\"";
    static String virtinc = "<!--#include virtual=\"";

    /**
     *
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java org.apache.lenya.util.SSI <file> -o file.out");

            return;
        }

        try {
            OutputStream out = System.out;
            out = new FileOutputStream("ssi-out.html");
            new SSI().includeFile(args[0], false, out);
            out.close();
        } catch (Exception e) {
            System.err.println(".main(): " + e);
        }
    }

    /**
     * Read the specified file and parse server side include instructions
     *
     * @param fileName The file path to read in
     * @param virtual Whether the fileName parameter is absolut or relativ to the document root of the web server
     * @param out The OutputStream where to write output to
     */
    public void includeFile(String fileName, boolean virtual, OutputStream out)
        throws IOException {
        if (virtual) {
            String documentRoot = ""; //(String) r_request.get("Request.DOCUMENT_ROOT");

            if (documentRoot != null) {
                fileName = documentRoot + fileName;
            }
        }

        System.err.println("Including file: " + fileName);

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileName));
        parseStream(bis, out);
    }

    /** Parse server side include instruction in the input stream and put the input
     * stream together with the includes to the output stream
     * @param in The input stream with instruction inline
     * @param out The output stream
     */
    public void parseStream(InputStream in, OutputStream out)
        throws IOException {
        int type = 0;
        int count = 0;
        int c = -1;

        while ((c = in.read()) != -1) {
            if ((count < 13) && (c == fileinc.charAt(count))) {
                count++;

                //System.err.println("Matched shared character("+count+"): "+(char) c);
                continue;
            } else if (count == 13) {
                if (c == fileinc.charAt(count)) {
                    type = 19;
                    count++;

                    //System.err.println("Matched file character");
                    continue;
                } else if (c == virtinc.charAt(count)) {
                    type = 22;
                    count++;

                    //System.err.println("Matched virt character");
                    continue;
                } else {
                    out.write(fileinc.substring(0, count).getBytes());
                    type = 0;
                    count = 0;
                }
            } else if (count > 13) {
                int oldcount = count;

                if ((type == 19) && (c == fileinc.charAt(count))) {
                    count++;

                    //System.err.println("Matched file character("+count+"): "+(char)c);
                } else if ((type == 22) && (c == virtinc.charAt(count))) {
                    count++;

                    //System.err.println("Matched virt character("+count+"): "+(char)c);
                } else {
                    String outs = (type == 19) ? fileinc : virtinc;
                    out.write(outs.substring(0, count).getBytes());
                    count = 0;
                    type = 0;
                }

                if (count >= type) {
                    StringBuffer fName = new StringBuffer();

                    while (((c = in.read()) != -1) && (c != '"')) {
                        fName.append((char) c);
                    }

                    while (((c = in.read()) != -1) && (c != '>'))
                        ;

                    includeFile(fName.toString(), (type == 22), out);
                    count = 0;
                    type = 0;

                    continue;
                }

                if (oldcount != count) {
                    continue;
                }
            } else {
                if (count > 0) {
                    out.write(fileinc.substring(0, count).getBytes());
                }

                count = 0;
                type = 0;
            }

            out.write(c);
        }
    }
}
