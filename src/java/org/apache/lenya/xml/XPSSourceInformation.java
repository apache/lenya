/*
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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner
 * @version $Id: XPSSourceInformation.java,v 1.17 2004/02/02 02:50:36 stefano Exp $
 */
public class XPSSourceInformation {
    static Category log = Category.getInstance(XPSSourceInformation.class);
    public int lineNumber = -1;
    public URL url = null;
    public XPSSourceInformation parentInfo = null;
    public Vector children = null;
    String offset = null;
    String cocoon = null;

    /**
     * Creates a new XPSSourceInformation object.
     *
     * @param fileURL DOCUMENT ME!
     * @param cocoon DOCUMENT ME!
     */
    public XPSSourceInformation(String fileURL, String cocoon) {
        this.cocoon = cocoon;

        parentInfo = null;
        offset = "++";
        children = new Vector();

        try {
            url = new URL(fileURL);
        } catch (MalformedURLException e) {
            log.error(e);
        }
    }

    /**
     * Creates a new XPSSourceInformation object.
     *
     * @param urlString DOCUMENT ME!
     * @param parentInfo DOCUMENT ME!
     * @param cocoon DOCUMENT ME!
     */
    public XPSSourceInformation(String urlString, XPSSourceInformation parentInfo, String cocoon) {
        this.cocoon = cocoon;

        this.parentInfo = parentInfo;
        offset = "++";
        children = new Vector();

        try {
            if (urlString.indexOf("/") == 0) {
                url = new URL("file:" + urlString);
            } else if (urlString.indexOf("cocoon:") == 0) {
                if (cocoon != null) {
                    url = new URL(cocoon + "/" + urlString.substring(7)); // remove "cocoon:" protocol
                    log.warn("Protocol 7789: COCOON (" + urlString +
                        ") -- will be transformed into http: " + url);
                } else {
                    log.error("No cocoon base set!");
                }
            } else {
                url = new URL(urlString);
            }

            String p = url.getProtocol();

            // Does not make sense, because it will be either file or http, and else an Exception will be thrown!
            if (!(p.equals("http") || p.equals("file") || p.equals("class"))) {
                log.error("This type of protocol is not supported yet: " + p);
            }
        } catch (MalformedURLException e) // let's hope it's a relative path
         {
            log.debug("1079: " + e + " -- Let's hope it's a relative path!");

            File parent = new File(parentInfo.url.getFile());

            // transform URI to system-independent path and create absolute path
            try {
                int index = urlString.indexOf("#");
                String xpointer = "";
                String relativePath = urlString;

                if (index != -1) {
                    relativePath = urlString.substring(0, index);
                    xpointer = urlString.substring(index);
                }

                relativePath = relativePath.replace('/', File.separatorChar);

                File file = new File(parent.getParentFile(), relativePath);

                url = new URL("file", null, -1, file.getCanonicalPath() + xpointer);

                log.info("Concatenated URL: " + url);
            } catch (MalformedURLException exception) {
                log.error(exception);
            } catch (IOException exception) {
                log.error(exception);
            }
        }

        if (url.getProtocol().equals("http")) {
        } else if (url.getProtocol().equals("file")) {
            if (parentInfo.url.getProtocol().equals("http")) {
                String protocol = parentInfo.url.getProtocol();
                String host = parentInfo.url.getHost();
                int port = parentInfo.url.getPort();

                try {
                    if (url.getRef() != null) {
                        url = new URL(protocol, host, port, url.getFile() + "#" + url.getRef());
                    } else {
                        url = new URL(protocol, host, port, url.getFile());
                    }
                } catch (MalformedURLException e) {
                    log.error(e);
                }
            }
        } else {
            log.error("EXCEPTION: 0.2.21");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        String cocoon = null;
        XPSSourceInformation xpssf = new XPSSourceInformation(args[0], cocoon);
        System.out.println(xpssf);
    }

    /**
     * DOCUMENT ME!
     *
     * @param child DOCUMENT ME!
     */
    public void addChild(XPSSourceInformation child) {
        children.addElement(child);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString() {
        return toString("", offset);
    }

    /**
     * DOCUMENT ME!
     *
     * @param index DOCUMENT ME!
     * @param offset DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString(String index, String offset) {
        String s = index + url.toString() + "\n";

        for (int i = 0; i < children.size(); i++) {
            XPSSourceInformation child = (XPSSourceInformation) children.elementAt(i);
            s = s + child.toString(index + offset, offset);
        }

        return s;
    }

    /**
     * Check for XInclude Loops
     *
     * @param xpssf DOCUMENT ME!
     * @param url DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean checkLoop(XPSSourceInformation xpssf, URL url) {
        //System.err.println(xpssf.url.getFile()+" "+url.getFile());
        if (xpssf.url.getFile().equals(url.getFile())) {
            if (xpssf.parentInfo != null) {
                return true;
            } else {
                return false; // This is just the request (it can be dummy.xml but also something real)
            }
        }

        if (xpssf.parentInfo != null) {
            return checkLoop(xpssf.parentInfo, url);
        }

        return false;
    }
}
