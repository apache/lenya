/*
 * $Id: NamespaceHelper.java,v 1.1 2003/02/07 15:48:08 ah Exp $
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
package org.wyona.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;


/**
 * A NamespaceHelper object simplifies the creation of elements in a certain
 * namespace. All elements are owned by a document that is passed to the
 * {@link #NamespaceHelper(Document, String, String)} constructor or created
 * using the {@link #NamespaceHelper(String, String, String)} constructor.
 *
 * @author andreas
 */
public class NamespaceHelper {
    private String namespaceUri;
    private String prefix;
    private Document document;

    /**
     * Creates a new instance of NamespaceHelper using an existing document.
     *
     * @param document The document.
     * @param namespaceUri The namespace URI.
     * @param prefix The namespace prefix.
     */
    public NamespaceHelper(String namespaceUri, String prefix, Document document) {
        this.namespaceUri = namespaceUri;
        this.prefix = prefix;
        this.document = document;
    }

    /**
     * Creates a new instance of NamespaceHelper. A new document is created
     * using a document element in the given namespace with the given prefix.
     *
     * @param localName The local name of the document element.
     * @param namespaceUri The namespace URI.
     * @param prefix The namespace prefix.
     */
    public NamespaceHelper(String namespaceUri, String prefix, String localName)
            throws ParserConfigurationException {
        this.namespaceUri = namespaceUri;
        this.prefix = prefix;
        setDocument(DocumentHelper.createDocument(
                getNamespaceURI(), getQualifiedName(localName), null));
    }

    /**
     * Sets the document of this NamespaceHelper.
     * @param document
     */    
    protected void setDocument(Document document) {
        this.document = document;
    }
    
    /**
     * Returns the document that is used to create elements.
     *
     * @return A document object.
     */    
    public Document getDocument() {
        return document;
    }
    
    /**
     * Returns the namespace URI of this NamespaceHelper.
     *
     * @return The namespace URI.
     */    
    public String getNamespaceURI() {
        return namespaceUri;
    }
    
    /**
     * Returns the namespace prefix that is used to create elements.
     *
     * @return The namespace prefix.
     */    
    public String getPrefix() {
        return prefix;
    }
    
    /**
     * Returns the qualified name for a local name using the prefix of this
     * NamespaceHelper.
     *
     * @param localName The local name.
     * @return The qualified name, i.e. prefix:localName.
     */    
    public String getQualifiedName(String localName) {
        return getPrefix() + ":" + localName;
    }
    
    /**
     * Creates an element within the namespace of this NamespaceHelper object with
     * a given local name containing a text node.
     *
     * @param localName DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    public Element createElement(String localName) {
        return getDocument().createElementNS(getNamespaceURI(), getQualifiedName(localName));
    }

    /**
     * Creates an element within the namespace of this NamespaceHelper object with
     * a given local name containing a text node.
     *
     * @param localName DOCUMENT ME!
     * @param text DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    protected Element createElement(String localName, String text) {
        Element element = createElement(localName);
        Text textNode = getDocument().createTextNode(text);
        element.appendChild(textNode);
        return element;
    }

}
