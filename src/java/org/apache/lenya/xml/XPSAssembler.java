/*
 * $Id: XPSAssembler.java,v 1.7 2003/03/04 17:58:25 gregor Exp $
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

import org.apache.log4j.Category;

import org.w3c.dom.*;

//import org.lenya.xml.*;
import org.lenya.net.*;

import java.io.*;

import java.net.*;

import java.util.*;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner
 * @version 2002.5.28 XLink Processor (Nesting, Caching, Java)
 */
public class XPSAssembler implements XPSInclude {
    static Category log = Category.getInstance(XPSAssembler.class);
    DOMParserFactory dpf = null;
    XPointerFactory xpf = null;
    Configuration conf = null;
    ProxyManager pm = null;
    String XPSEXCEPTION_ELEMENT_NAME = "XPSEXCEPTION"; // better would be a namespace xps:XLinkException

    /**
     * Creates a new XPSAssembler object.
     */
    public XPSAssembler() {
        dpf = new DOMParserFactory();
        xpf = new XPointerFactory();
        conf = new Configuration();
        pm = new ProxyManager();

        if ((conf.proxyHost != null) && (conf.proxyPort != null)) {
            Properties sp = System.getProperties();
            sp.put("proxySet", "true");
            sp.put("proxyHost", conf.proxyHost);
            sp.put("proxyPort", conf.proxyPort);
        }
    }

    public XPSAssembler(String includeoption) {
        this();
        conf.INCLUDE = includeoption;
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        XPSAssembler xpsa = new XPSAssembler();

        if (args.length != 1) {
            System.err.println("Usage:");
            System.err.println(" java " + xpsa.getClass().getName() +
                " \"../x/xps/samples/tbs/xml/invoices/invoice.xml\"");
            System.err.println(" java " + xpsa.getClass().getName() + " \"file:/...\"");
            System.err.println(" java " + xpsa.getClass().getName() + " \"http://localhost/...\"");

            return;
        }

        String cocoon = null; // e.g. http://127.0.0.1:8080/lenya/nwt
        Document document = xpsa.assemble(args[0], cocoon);

        try {
            OutputStream out = System.out;
            new DOMWriter(out, "utf-8").printWithoutFormatting(document);

            System.out.print("\n");
            out.flush();
        } catch (Exception e) {
            System.err.println(xpsa.getClass().getName() + ".main(): " + e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param reference DOCUMENT ME!
     * @param cocoon DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Document assemble(String reference, String cocoon) {
        File workingDirectory = new File(System.getProperty("user.dir"));
        XPSSourceInformation sourceInfo = new XPSSourceInformation("file:" + workingDirectory +
                "/dummy.xml", cocoon);
        String[] args = new String[1];
        args[0] = reference;

        XPSSourceInformation currentInfo = new XPSSourceInformation(args[0], sourceInfo, cocoon);
        deleteFromCache(currentInfo.url);

        Vector nodes = include(args, sourceInfo);
        log.debug(sourceInfo);

        Node node = (Node) nodes.elementAt(0);

        return node.getOwnerDocument();
    }

    /**
     * Remove file from cache
     *
     * @param url DOCUMENT ME!
     */
    public void deleteFromCache(URL url) {
        if (conf.cacheFolder != null) {
            File cacheFile = getCacheFile(url);

            if (cacheFile.isFile()) {
                log.info(".deleteFromCache(): " + cacheFile.getAbsolutePath());
                cacheFile.delete();
            } else {
                log.warn(".deleteFromCache(): No such file in cache: " +
                    cacheFile.getAbsolutePath());
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param document DOCUMENT ME!
     * @param reference DOCUMENT ME!
     * @param cocoon DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Document assemble(Document document, String reference, String cocoon) {
        Element root = document.getDocumentElement();
        Document assembledDocument = dpf.getDocument();
        Element assembledRoot = (Element) dpf.cloneNode(assembledDocument, root, false);
        assembledDocument.appendChild(assembledRoot);

        File workingDirectory = new File(System.getProperty("user.dir"));
        XPSSourceInformation sourceInfo = new XPSSourceInformation("file:" + workingDirectory +
                "/dummy.xml", cocoon);
        XPSSourceInformation currentInfo = new XPSSourceInformation(reference, sourceInfo, cocoon);
        NodeList nl = root.getChildNodes();

        for (int i = 0; i < nl.getLength(); i++) {
            traverse(assembledRoot, nl.item(i), sourceInfo, currentInfo);
        }

        return assembledDocument;
    }

    /**
     * DOCUMENT ME!
     *
     * @param currentInfo DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public InputStream readXML(XPSSourceInformation currentInfo)
        throws Exception {
        InputStream is = null;
        File cacheFile = null;
        long originalFileLastModified = 0;

        String protocol = currentInfo.url.getProtocol();

        URL url = null;

        if (conf.cacheFolder != null) // Check cache
         {
            cacheFile = getCacheFile(currentInfo.url);

            if (protocol.equals("file")) {
                File originalFile = new File(currentInfo.url.getFile());
                originalFileLastModified = originalFile.lastModified();
            } else if (protocol.equals("http")) {
                pm.set(currentInfo.url.getHost()); // install proxy if necessary
                originalFileLastModified = currentInfo.url.openConnection().getLastModified();
            } else {
                log.error("No such protocol: " + protocol);
            }

            if (cacheFile.isFile() && (cacheFile.lastModified() >= originalFileLastModified)) {
		// File already exists in cache and is newer than original File
                url = new URL("file:" + cacheFile.getAbsolutePath());
            } else { // File does not exist in cache
                url = new URL(currentInfo.url.toString());
            }
        } else { // No cache folder specified
            url = new URL(currentInfo.url.toString());
        }

        // Read Document
        is = url.openStream();

        return is;
    }

    /**
     * DOCUMENT ME!
     *
     * @param currentInfo DOCUMENT ME!
     * @param newDocument DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean tryWritingToCache(XPSSourceInformation currentInfo, Document newDocument) {
        File cacheFile = null;

        if (conf.cacheFolder != null) // Check cache
         {
            cacheFile = getCacheFile(currentInfo.url);
        }

        if (cacheFile != null) {
            String protocol = currentInfo.url.getProtocol();

            if (!cacheFile.exists()) { // Write "Xlink" to cache
                return writeToCache(protocol, cacheFile, newDocument);
            } else { // cacheFile exists
                long originalFileLastModified = 0;
                String p = currentInfo.url.getProtocol();

                if (p.equals("file")) {
                    File originalFile = new File(currentInfo.url.getFile());
                    originalFileLastModified = originalFile.lastModified();
                } else if (p.equals("http")) {
                    try {
                        originalFileLastModified = currentInfo.url.openConnection().getLastModified();
                    } catch (IOException e) {
                        log.error("originalFileLastModified: " + e);
                    }
                } else {
                    log.error("No such protocol: " + p);
                }

                if (cacheFile.lastModified() < originalFileLastModified) {
		    // File in cache is older than original File
                    return writeToCache(protocol, cacheFile, newDocument);
                }
            }
        }

        return false;
    }

    /**
     * param args args[0]=url
     *
     * @param args DOCUMENT ME!
     * @param sourceInfo DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Vector include(String[] args, XPSSourceInformation sourceInfo) {
        XPSSourceInformation currentInfo = new XPSSourceInformation(args[0], sourceInfo,
                sourceInfo.cocoon);

        sourceInfo.addChild(currentInfo);

        if (currentInfo.checkLoop(sourceInfo, currentInfo.url)) {
            log.warn(".include(): EXCEPTION: Loop detected: " + sourceInfo.url.getFile());

            return null;
        }

        Document document = null;
        Vector nodes = new Vector();
        Document newDocument = dpf.getDocument();

        try {
            InputStream is = readXML(currentInfo);
            document = dpf.getDocument(is);
        } catch (Exception e) {
            log.warn(".include() 1056: " + e);

            Element newRoot = dpf.newElementNode(newDocument, XPSEXCEPTION_ELEMENT_NAME);
            newRoot.appendChild(dpf.newTextNode(newDocument, "" + e));
            nodes.addElement(newRoot);

            return nodes;
        }

        Element root = document.getDocumentElement();
        Element newRoot = (Element) dpf.cloneNode(newDocument, root, false);
        newDocument.appendChild(newRoot);

        NodeList nl = root.getChildNodes();

        for (int i = 0; i < nl.getLength(); i++) {
            traverse(newRoot, nl.item(i), sourceInfo, currentInfo);
        }

        boolean writtenToCache = tryWritingToCache(currentInfo, newDocument);

        if (currentInfo.url.getRef() == null) // No XPointer specified
         {
            nodes.addElement(newRoot);
        } else // XPointer
         {
            try {
                nodes = xpf.select(newRoot, currentInfo.url.getRef());

                for (int i = 0; i < nodes.size(); i++) {
                    short nodeType = ((Node) nodes.elementAt(i)).getNodeType();

                    switch (nodeType) {
                    case Node.ELEMENT_NODE:break;

                    case Node.ATTRIBUTE_NODE: {
                        Node attribute = (Node) nodes.elementAt(i);
                        nodes.removeElementAt(i);
                        nodes.insertElementAt(dpf.newTextNode(attribute.getOwnerDocument(),
                                attribute.getNodeValue()), i);

                        break;
                    }

                    default: {
                        log.error(".include(): Node Type (" + nodeType +
                            ") can't be a child of Element");
                        nodes.removeElementAt(i);

                        break;
                    }
                    }
                }
            } catch (Exception e) {
                log.error(".include() 34543: " + e);
            }
        }

        return nodes;
    }

    /**
     * Traverses recursively and looks for XLinks and includes the returned NodeList
     *
     * @param newParent DOCUMENT ME!
     * @param orgChild DOCUMENT ME!
     * @param sourceInfo DOCUMENT ME!
     * @param currentInfo DOCUMENT ME!
     */
    public void traverse(Node newParent, Node orgChild, XPSSourceInformation sourceInfo,
        XPSSourceInformation currentInfo) {
        Vector newChildren = new Vector();
        short nodeType = orgChild.getNodeType();
        boolean noXLink = true;

        switch (nodeType) {
        case Node.ELEMENT_NODE: {
            XLink xlink = new XLink((Element) orgChild);

            if (xlink.href == null) {
                Element newElement = (Element) dpf.cloneNode(newParent.getOwnerDocument(),
                        orgChild, false);
                newChildren.addElement(newElement);
            } else {
                noXLink = false;
                log.debug(".traverse(): xlink:href=\"" + xlink.href + "\"");

                NodeList nl = processXLink(xlink, (Element) orgChild, currentInfo);

                for (int i = 0; i < nl.getLength(); i++) {
                    newChildren.addElement(dpf.cloneNode(newParent.getOwnerDocument(), nl.item(i),
                            true));
                }
            }

            break;
        }

        case Node.COMMENT_NODE: {
            newChildren.addElement(dpf.newCommentNode(newParent.getOwnerDocument(),
                    orgChild.getNodeValue()));

            break;
        }

        case Node.TEXT_NODE: {
            newChildren.addElement(dpf.newTextNode(newParent.getOwnerDocument(),
                    orgChild.getNodeValue()));

            break;
        }

        default: {
            log.fatal(".traverse(): Node type not implemented: " + nodeType + " (" +
                currentInfo.url + ")");

            break;
        }
        }

        for (int i = 0; i < newChildren.size(); i++) {
            newParent.appendChild((Node) newChildren.elementAt(i));
        }

        if (orgChild.hasChildNodes() && noXLink) {
            NodeList nl = orgChild.getChildNodes();

            for (int i = 0; i < nl.getLength(); i++) {
                traverse((Node) newChildren.elementAt(0), nl.item(i), sourceInfo, currentInfo);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param xlink DOCUMENT ME!
     * @param orgChild DOCUMENT ME!
     * @param currentInfo DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public NodeList processXLink(XLink xlink, Element orgChild, XPSSourceInformation currentInfo) {
        NodeList nl = null;

        if (!xlink.show.equals("embed")) {
            nl = noNodesReturnedFromXLink(xlink);
        } else {
            Vector args = new Vector();
            String includeClassName = includeClassName(xlink.href, args);
            String[] arguments = new String[args.size()];

            for (int i = 0; i < args.size(); i++) {
                arguments[i] = (String) args.elementAt(i);
            }

            Vector newChildren = null;

            try {
                if (includeClassName.equals(this.getClass().getName())) {
                    newChildren = include(arguments, currentInfo);
                } else {
                    Class includeClass = Class.forName(includeClassName);
                    XPSInclude xpsInclude = (XPSInclude) includeClass.newInstance();
                    newChildren = xpsInclude.include(arguments, currentInfo);
                }
            } catch (Exception e) {
                log.error(".processXLink(): " + e);
            }

            if (newChildren != null) // Returned nodes from XLink
             {
                if (newChildren.size() > 0) {
                    Node firstChild = (Node) newChildren.elementAt(0);
                    Document xlinkedDocument = firstChild.getOwnerDocument();
                    Element dummyRoot = dpf.newElementNode(xlinkedDocument, "DummyRoot");

                    if (conf.INCLUDE.equals("replace")) {
                        for (int i = 0; i < newChildren.size(); i++) {
                            dummyRoot.appendChild((Node) newChildren.elementAt(i));
                        }
                    } else if (conf.INCLUDE.equals("embed")) {
                        dummyRoot.appendChild(xlink.getXLink(xlinkedDocument, dpf));

                        for (int i = 0; i < newChildren.size(); i++) {
                            dummyRoot.appendChild((Node) newChildren.elementAt(i));
                        }
                    } else if (conf.INCLUDE.equals("enclose")) {
                        Element xlinkCopy = xlink.getXLink(xlinkedDocument, dpf);

                        for (int i = 0; i < newChildren.size(); i++) {
                            xlinkCopy.appendChild((Node) newChildren.elementAt(i));
                        }

                        dummyRoot.appendChild(xlinkCopy);
                    }

                    nl = dummyRoot.getChildNodes();
                }
            }

            if (nl == null) {
                nl = noNodesReturnedFromXLink(xlink);
            }

            if (nl.getLength() == 0) {
                nl = noNodesReturnedFromXLink(xlink);
            }
        }

        return nl;
    }

    /**
     * DOCUMENT ME!
     *
     * @param xlink DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public NodeList noNodesReturnedFromXLink(XLink xlink) {
        log.warn("No nodes returned from XLink: " + xlink);

        Document dummyDocument = dpf.getDocument();
        Element dummyRoot = dpf.newElementNode(dummyDocument, "DummyRoot");
        Element element = xlink.getXLink(dummyDocument, dpf);
        dummyRoot.appendChild(element);

        Element exceptionElement = dpf.newElementNode(dummyDocument, XPSEXCEPTION_ELEMENT_NAME);
        exceptionElement.appendChild(dpf.newElementNode(dummyDocument, "NoNodesReturnedFromXLink"));
        dummyRoot.appendChild(exceptionElement);

        return dummyRoot.getChildNodes();
    }

    /**
     * DOCUMENT ME!
     *
     * @param href DOCUMENT ME!
     * @param args DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String includeClassName(String href, Vector args) {
        String icn = null;

        if (href.indexOf(conf.JAVA_ZONE) == 0) {
            log.debug(".includeClassName(): java class: " + href);
            icn = href.substring(conf.JAVA_ZONE.length(), href.length());

            StringTokenizer st = new StringTokenizer(icn, "?");
            icn = st.nextToken();

            if (st.countTokens() == 1) {
                args.addElement(st.nextToken());
            }
        } else {
            icn = this.getClass().getName();
            args.addElement(href);
        }

        log.debug(".includeClassName(): class name: " + icn);

        return icn;
    }

    /**
     * DOCUMENT ME!
     *
     * @param url DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public File getCacheFile(URL url) {
        String cacheFile = null;
        String protocol = url.getProtocol();

        if (protocol.equals("file")) {
            cacheFile = protocol + "/" + url.getFile();
        } else if (protocol.equals("http")) {
            cacheFile = protocol + "/" + url.getHost() + "/" + url.getPort() + "/" + url.getFile();
        } else {
            log.error("No such protocol: " + protocol);
        }

        return new File(conf.cacheFolder + "/" + cacheFile);
    }

    /**
     *
     */
    public boolean writeToCache(String protocol, File cacheFile, Document newDocument) {
        if (protocol.equals("http") && !conf.cacheHTTP) {
            // Do not cache HTTP
            return false;
        }

        File cacheFileParent = new File(cacheFile.getParent());

        if (!cacheFileParent.isDirectory()) {
            cacheFileParent.mkdirs();
        }

        try {
            OutputStream out = new FileOutputStream(cacheFile.getAbsolutePath());

            new DOMWriter(out, "iso-8859-1").printWithoutFormatting(newDocument);
            out.close();

            return true;
        } catch (Exception e) {
            log.error(".include(): " + e);
        }

        return false;
    }
}
