/*
$Id: XercesParser.java,v 1.12 2003/07/23 13:21:26 gregor Exp $
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
package org.apache.lenya.xml.parser;

import org.apache.lenya.xml.DOMWriter;

import org.apache.xerces.dom.*;

//import org.apache.xerces.impl.xs.dom.DOMParser;
import org.apache.xerces.parsers.DOMParser;

import org.w3c.dom.*;

import java.io.*;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner, lenya
 * @version 0.5.5
 * @deprecated replaced by DocumentHelper
 */
public class XercesParser implements Parser {
    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        Parser parser = new XercesParser();

        if (args.length != 1) {
            System.err.println("Usage: java " + parser.getClass().getName() + " example.xml");

            return;
        }

        Document doc = null;

        try {
            doc = parser.getDocument(args[0]);
        } catch (Exception e) {
            System.err.println(e);
        }

        new DOMWriter(new PrintWriter(System.out)).print(doc);
        System.out.println("");

        Document document = parser.getDocument();
        Element michi = parser.newElementNode(document, "Employee");
        michi.setAttribute("Id", "michi");
        michi.appendChild(parser.newTextNode(document, "Michi"));

        Element employees = parser.newElementNode(document, "Employees");
        employees.appendChild(parser.newTextNode(document, "\n"));
        employees.appendChild(michi);
        employees.appendChild(parser.newTextNode(document, "\n"));
        document.appendChild(employees);
        new DOMWriter(new PrintWriter(System.out)).print(document);
        System.out.println("");
    }

    /**
     * DOCUMENT ME!
     *
     * @param filename DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public Document getDocument(String filename) throws Exception {
        DOMParser parser = new DOMParser();

        org.xml.sax.InputSource in = new org.xml.sax.InputSource(filename);
        parser.parse(in);

        return parser.getDocument();
    }

    /**
     * DOCUMENT ME!
     *
     * @param is DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public Document getDocument(InputStream is) throws Exception {
        DOMParser parser = new DOMParser();
        org.xml.sax.InputSource in = new org.xml.sax.InputSource(is);
        parser.parse(in);

        return parser.getDocument();
    }

    /**
     * Creates a document from a reader.
     *
     * @param is DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public Document getDocument(Reader reader) throws Exception {
        DOMParser parser = new DOMParser();
        org.xml.sax.InputSource in = new org.xml.sax.InputSource(reader);
        parser.parse(in);

        return parser.getDocument();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Document getDocument() {
        return new DocumentImpl();
    }

    /**
     * DOCUMENT ME!
     *
     * @param document DOCUMENT ME!
     * @param name DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Element newElementNode(Document document, String name) {
        //return new ElementNSImpl((CoreDocumentImpl) document, name);
        return new ElementImpl((DocumentImpl) document, name);
    }

    /**
     * DOCUMENT ME!
     *
     * @param document DOCUMENT ME!
     * @param data DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Text newTextNode(Document document, String data) {
        return new TextImpl((DocumentImpl) document, data);
    }

    /**
     * DOCUMENT ME!
     *
     * @param document DOCUMENT ME!
     * @param data DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Comment newCommentNode(Document document, String data) {
        return new CommentImpl((DocumentImpl) document, data);
    }
}
