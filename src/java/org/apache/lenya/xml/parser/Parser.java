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

/* $Id: Parser.java,v 1.14 2004/03/01 16:18:25 gregor Exp $  */

package org.apache.lenya.xml.parser;

import java.io.InputStream;
import java.io.Reader;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;


/**
 * DOM Parser interface
 */
public interface Parser {
    /**
     * DOCUMENT ME!
     *
     * @param filename DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    Document getDocument(String filename) throws Exception;

    /**
     * DOCUMENT ME!
     *
     * Create a document from a reader.
     * @param is DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    Document getDocument(Reader reader) throws Exception;

    /**
     * DOCUMENT ME!
     *
     * @param is DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    Document getDocument(InputStream is) throws Exception;

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    Document getDocument();

    /**
     * DOCUMENT ME!
     *
     * @param document DOCUMENT ME!
     * @param name DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    Element newElementNode(Document document, String name);

    /**
     * Creates an element with namespace support.
     *
     * @param document The owner document.
     * @param namespaceUri The namespace URI of the element.
     * @param qualifiedName The qualified name of the element.
     *
     * @return An element.
     */
    Element newElementNSNode(Document document, String namespaceUri, String qualifiedName);
        
    /**
     * DOCUMENT ME!
     *
     * @param document DOCUMENT ME!
     * @param data DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    Text newTextNode(Document document, String data);

    /**
     * CDATA
     *
     * @param document DOM Document
     * @param data Text
     *
     * @return CDATASection
     */
    CDATASection newCDATASection(Document document, String data);

    /**
     * DOCUMENT ME!
     *
     * @param document DOCUMENT ME!
     * @param data DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    Comment newCommentNode(Document document, String data);
}
