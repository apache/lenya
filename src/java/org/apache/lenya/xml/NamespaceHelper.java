/*
 * $Id: NamespaceHelper.java,v 1.7 2003/03/04 19:44:56 gregor Exp $
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
 * Wyona includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.lenya.xml;

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
     * Creates a new instance of NamespaceHelper using an existing document. The
     * document is not affected. If you omit the prefix, the default namespace is used.
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
     * <p>
     * Creates a new instance of NamespaceHelper. A new document is created
     * using a document element in the given namespace with the given prefix.
     * If you omit the prefix, the default namespace is used.
     * </p>
     * <p>
     * NamespaceHelper("http://www.w3.org/2000/svg", "svg", "svg"):<br/>
     * &lt;?xml version="1.0"&gt;<br/>
     * &lt;svg:svg xmlns:svg="http://www.w3.org/2000/svg"&gt;<br/>
     * &lt;/svg:svg&gt;
     * </p>
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
     * <p>
     * Creates an element within the namespace of this NamespaceHelper object with
     * a given local name containing a text node.<br/>
     * </p>
     * <p>
     * <code>createElement("text")</code>: <code>&lt;prefix:text/&gt;<code>.
     * </p>
     *
     * @param localName The local name of the element.
     * @return A new element.
     */
    public Element createElement(String localName) {
        return getDocument().createElementNS(getNamespaceURI(), getQualifiedName(localName));
    }

    /**
     * <p>
     * Creates an element within the namespace of this NamespaceHelper object with
     * a given local name containing a text node.
     * </p>
     * <p>
     * <code>createElement("text", "Hello World!")</code>:
     * <code>&lt;prefix:text&gt;Hello World!&lt;/prefix:text&gt;</code>.
     * </p>
     *
     * @param localName The local name of the element.
     * @param text The text for the text node inside the element.
     * @return A new element containing a text node.
     */
    protected Element createElement(String localName, String text) {
        Element element = createElement(localName);
        Text textNode = getDocument().createTextNode(text);
        element.appendChild(textNode);
        return element;
    }

    /**
     * Returns all children of an element in the namespace
     * of this NamespaceHelper.
     *
     * @param element The parent element.
     */
    public Element[] getChildren(Element element) {
        return DocumentHelper.getChildren(element, getNamespaceURI());
    }

    /**
     * Returns all children of an element with a local name in the namespace
     * of this NamespaceHelper.
     *
     * @param element The parent element.
     * @param localName The local name of the children to return.
     */
    public Element[] getChildren(Element element, String localName) {
        return DocumentHelper.getChildren(element, getNamespaceURI(), localName);
    }

    /**
     * Returns the first childr of an element with a local name in the namespace
     * of this NamespaceHelper or <code>null</code> if none exists.
     *
     * @param element The parent element.
     * @param localName The local name of the children to return.
     */
    public Element getFirstChild(Element element, String localName) {
        return DocumentHelper.getFirstChild(element, getNamespaceURI(), localName);
    }

}
