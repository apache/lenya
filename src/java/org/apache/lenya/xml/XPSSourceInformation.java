/*
 * $Id: XPSSourceInformation.java,v 1.13 2003/04/24 13:53:14 gregor Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
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
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.apache.lenya.xml;

import org.apache.log4j.Category;

import java.io.File;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Vector;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner
 * @version 2002.5.30
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
                    log.warn("Protocol 7789: COCOON (" + urlString + ") -- will be transformed into http: " + url);
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
     * DOCUMENT ME!
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
