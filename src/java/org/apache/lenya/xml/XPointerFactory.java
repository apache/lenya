/*
$Id
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

import org.apache.lenya.xml.xpointer.*;

import org.apache.log4j.Category;

import org.w3c.dom.*;

import java.io.*;

import java.util.*;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner, lenya
 * @version 0.4.16
 */
public class XPointerFactory {
    static Category log = Category.getInstance(XPointerFactory.class);
    XPointer xpointer = null;

    /**
     * Creates a new XPointerFactory object.
     */
    public XPointerFactory() {
        Properties properties = new Properties();
        String propertiesFileName = "conf.properties";

        try {
            properties.load(XPointerFactory.class.getResourceAsStream(propertiesFileName));
        } catch (Exception e) {
            log.fatal(": Failed to load properties from resource: " + propertiesFileName);
        }

        String xpointerName = properties.getProperty("XPointer");

        if (xpointerName == null) {
            log.fatal(": No XPointer specified in " + propertiesFileName);
        }

        try {
            Class xpointerClass = Class.forName(xpointerName);
            xpointer = (XPointer) xpointerClass.newInstance();
        } catch (Exception e) {
            log.fatal(": " + e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        XPointerFactory xpf = new XPointerFactory();
        DOMParserFactory dpf = new DOMParserFactory();

        if (args.length != 2) {
            System.err.println("Usage: java " + xpf.getClass().getName() +
                " example.xml \"/Example/People/Person[1]/Name\"");

            return;
        }

        Document document = null;

        try {
            document = dpf.getDocument(args[0]);
        } catch (FileNotFoundException e) {
            System.err.println("No such file or directory: " + e.getMessage());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        String xpath = args[1];

        try {
            Vector nodes = xpf.select(document.getDocumentElement(), "xpointer(" + xpath + ")");
            String[] values = xpf.getNodeValues(nodes);

            for (int i = 0; i < nodes.size(); i++) {
                System.out.println(((Node) nodes.elementAt(i)).getNodeName() + ": " + values[i]);
            }
        } catch (Exception e) {
            System.err.println(xpf.getClass().getName() + ".main(): " + e);
        }

        Document doc = xpf.employees();

        try {
            Vector nodes = xpf.select(doc.getDocumentElement(), "xpointer(/Employees/Employee[2])");
            String[] values = xpf.getNodeValues(nodes);

            for (int i = 0; i < nodes.size(); i++) {
                System.out.println(((Node) nodes.elementAt(i)).getNodeName() + ": " + values[i]);
            }

            Element leviElement = (Element) nodes.elementAt(0);
            leviElement.appendChild(dpf.newTextNode(doc, " Brucker"));
        } catch (Exception e) {
            System.err.println(xpf.getClass().getName() + ".main(): " + e);
        }

        new DOMWriter(new PrintWriter(System.out)).print(doc);
        System.out.println("");
    }

    /**
     * DOCUMENT ME!
     *
     * @param reference DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @exception MalformedXPointerException xpointer(xpath)
     */
    public Vector parse(String reference) throws MalformedXPointerException {
        Vector xpaths = new Vector();
        tokenize(reference, xpaths);

        return xpaths;
    }

    /**
     * DOCUMENT ME!
     *
     * @param node DOCUMENT ME!
     * @param reference DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @exception Exception ...
     */
    public Vector select(Node node, String reference) throws Exception {
        Vector xpaths = parse(reference);

        Vector nodes = new Vector();

        for (int i = 0; i < xpaths.size(); i++) {
            Vector n = xpointer.select(node, (String) xpaths.elementAt(i));

            for (int j = 0; j < n.size(); j++) {
                nodes.addElement(n.elementAt(j));
            }
        }

        return nodes;
    }

    /**
     * DOCUMENT ME!
     *
     * @param xpointer DOCUMENT ME!
     * @param xpaths DOCUMENT ME!
     *
     * @exception MalformedXPointerException xpointer(xpath)xpointer(xpath)
     */
    public void tokenize(String xpointer, Vector xpaths)
        throws MalformedXPointerException {
        if ((xpointer.indexOf("xpointer(") == 0) &&
                (xpointer.charAt(xpointer.length() - 1) == ')')) {
            String substring = xpointer.substring(9, xpointer.length());
            int i = substring.indexOf(")xpointer(");

            if (i >= 0) {
                xpaths.addElement(substring.substring(0, i));
                tokenize(substring.substring(i + 1, substring.length()), xpaths);
            } else {
                xpaths.addElement(substring.substring(0, substring.length() - 1));

                return;
            }
        } else {
            throw new MalformedXPointerException(xpointer);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param nodes DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public String[] getNodeValues(Vector nodes) throws Exception {
        String[] values = new String[nodes.size()];

        for (int i = 0; i < values.length; i++) {
            Node node = (Node) nodes.elementAt(i);
            short type = node.getNodeType();

            switch (type) {
            case Node.ELEMENT_NODE: {
                values[i] = getElementValue((Element) node);

                break;
            }

            case Node.ATTRIBUTE_NODE: {
                values[i] = node.getNodeValue();

                break;
            }

            default:
                values[i] = "";
                throw new Exception("Neither ELEMENT nor ATTRIBUTE: " + type);
            }
        }

        return values;
    }

    /**
     * DOCUMENT ME!
     *
     * @param element DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getElementValue(Element element) {
        String value = "";
        NodeList nl = element.getChildNodes();

        for (int k = 0; k < nl.getLength(); k++) {
            short nodeType = nl.item(k).getNodeType();

            if (nodeType == Node.TEXT_NODE) {
                value = value + nl.item(k).getNodeValue();
            } else if (nodeType == Node.ELEMENT_NODE) {
                value = value + getElementValue((Element) nl.item(k));
            } else {
                System.err.println("EXCEPTION: " + this.getClass().getName() +
                    ".getElementValue(): No TEXT_NODE");
            }
        }

        return value;
    }

    /**
     * DOCUMENT ME!
     *
     * @param element DOCUMENT ME!
     * @param text DOCUMENT ME!
     */
    public void getElementValue(Element element, Vector text) {
        NodeList nl = element.getChildNodes();

        for (int k = 0; k < nl.getLength(); k++) {
            short nodeType = nl.item(k).getNodeType();

            if (nodeType == Node.TEXT_NODE) {
                text.addElement(nl.item(k).getNodeValue());
            } else if (nodeType == Node.ELEMENT_NODE) {
                getElementValue((Element) nl.item(k), text);
            } else {
                System.err.println("EXCEPTION: " + this.getClass().getName() +
                    ".getElementValue(): No TEXT_NODE");
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Document employees() {
        DOMParserFactory dpf = new DOMParserFactory();
        Document doc = dpf.getDocument();
        Element michi = dpf.newElementNode(doc, "Employee");
        michi.setAttribute("Id", "0");
        michi.appendChild(dpf.newTextNode(doc, "Michi"));

        Element levi = dpf.newElementNode(doc, "Employee");
        levi.setAttribute("Id", "1");
        levi.appendChild(dpf.newTextNode(doc, "Levi"));

        Element employees = dpf.newElementNode(doc, "Employees");
        employees.appendChild(dpf.newTextNode(doc, "\n"));
        employees.appendChild(michi);
        employees.appendChild(dpf.newTextNode(doc, "\n"));
        employees.appendChild(levi);
        employees.appendChild(dpf.newTextNode(doc, "\n"));
        doc.appendChild(employees);

        return doc;
    }
}
