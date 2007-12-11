/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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

/* $Id$  */

package org.apache.lenya.xml;

import java.io.OutputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.lenya.util.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * A NamespaceHelper object simplifies the creation of elements in a certain
 * namespace. All elements are owned by a document that is passed to the
 * {@link #NamespaceHelper(String, String, Document)} constructor or created
 * using the {@link #NamespaceHelper(String, String, String)} constructor.
 */
public class NamespaceHelper {
    private String namespaceUri;
    private String prefix;
    private Document document;

    /**
     * Creates a new instance of NamespaceHelper using an existing document. The
     * document is not affected. If the prefix is <code>null</code>, the default namespace
     * is used.
     * @param _document The document.
     * @param _namespaceUri The namespace URI.
     * @param _prefix The namespace prefix.
     */
    public NamespaceHelper(String _namespaceUri, String _prefix, Document _document) {

        Assert.notNull("namespace URI", _namespaceUri);
        Assert.notNull("DOM", _document);

        this.namespaceUri = _namespaceUri;
        this.prefix = _prefix;
        this.document = _document;
    }

    /**
     * <p>
     * Creates a new instance of NamespaceHelper. A new document is created
     * using a document element in the given namespace with the given prefix. If
     * the prefix is null, the default namespace is used.
     * </p>
     * <p>
     * NamespaceHelper("http://www.w3.org/2000/svg", "svg", "svg"):<br/>
     * &lt;?xml version="1.0"&gt;<br/> &lt;svg:svg
     * xmlns:svg="http://www.w3.org/2000/svg"&gt;<br/> &lt;/svg:svg&gt;
     * </p>
     * @param localName The local name of the document element.
     * @param _namespaceUri The namespace URI.
     * @param _prefix The namespace prefix.
     * @throws ParserConfigurationException if an error occured
     */
    public NamespaceHelper(String _namespaceUri, String _prefix, String localName)
            throws ParserConfigurationException {
        this(_namespaceUri, _prefix, DocumentHelper.createDocument(_namespaceUri, getQualifiedName(
                _prefix, localName), null));
    }

    /**
     * Returns the document that is used to create elements.
     * @return A document object.
     */
    public Document getDocument() {
        return this.document;
    }

    /**
     * Returns the namespace URI of this NamespaceHelper.
     * @return The namespace URI.
     */
    public String getNamespaceURI() {
        return this.namespaceUri;
    }

    /**
     * Returns the namespace prefix that is used to create elements.
     * @return The namespace prefix.
     */
    public String getPrefix() {
        return this.prefix;
    }

    /**
     * Returns the qualified name for a local name using the prefix of this
     * NamespaceHelper.
     * @param prefix The namespace prefix.
     * @param localName The local name.
     * @return The qualified name, i.e. prefix:localName.
     */
    public static String getQualifiedName(String prefix, String localName) {
        if (prefix == null || prefix.equals("")) {
            return localName;
        }
        return prefix + ":" + localName;
    }

    /**
     * <p>
     * Creates an element within the namespace of this NamespaceHelper object
     * with a given local name containing a text node.<br/>
     * </p>
     * <p>
     * <code>createElement("text")</code>: <code>&lt;prefix:text/&gt;<code>.
     * </p>
     * @param localName The local name of the element.
     * @return A new element.
     */
    public Element createElement(String localName) {
        return getDocument().createElementNS(getNamespaceURI(),
                getQualifiedName(getPrefix(), localName));
    }

    /**
     * <p>
     * Creates an element within the namespace of this NamespaceHelper object
     * with a given local name containing a text node.
     * </p>
     * <p>
     * <code>createElement("text", "Hello World!")</code>:
     * <code>&lt;prefix:text&gt;Hello World!&lt;/prefix:text&gt;</code>.
     * </p>
     * @param localName The local name of the element.
     * @param text The text for the text node inside the element.
     * @return A new element containing a text node.
     */
    public Element createElement(String localName, String text) {
        Element element = createElement(localName);
        Text textNode = getDocument().createTextNode(text);
        element.appendChild(textNode);

        return element;
    }

    /**
     * Returns all children of an element in the namespace of this
     * NamespaceHelper.
     * @param element The parent element.
     * @return the children.
     */
    public Element[] getChildren(Element element) {
        return DocumentHelper.getChildren(element, getNamespaceURI());
    }

    /**
     * Returns all children of an element with a local name in the namespace of
     * this NamespaceHelper.
     * @param element The parent element.
     * @param localName The local name of the children to return.
     * @return the children.
     */
    public Element[] getChildren(Element element, String localName) {
        return DocumentHelper.getChildren(element, getNamespaceURI(), localName);
    }

    /**
     * Returns the first childr of an element with a local name in the namespace
     * of this NamespaceHelper or <code>null</code> if none exists.
     * @param element The parent element.
     * @param localName The local name of the children to return.
     * @return the first child.
     */
    public Element getFirstChild(Element element, String localName) {
        return DocumentHelper.getFirstChild(element, getNamespaceURI(), localName);
    }

    /**
     * Returns the next siblings of an element with a local name in the
     * namespace of this NamespaceHelper or <code>null</code> if none exists.
     * @param element The parent element.
     * @param localName The local name of the children to return.
     * @return the next siblings.
     */
    public Element[] getNextSiblings(Element element, String localName) {
        return DocumentHelper.getNextSiblings(element, getNamespaceURI(), localName);
    }

    /**
     * Returns the preceding siblings of an element with a local name in the
     * namespace of this NamespaceHelper or <code>null</code> if none exists.
     * @param element The parent element.
     * @param localName The local name of the children to return.
     * @return the preceding siblings.
     */
    public Element[] getPrecedingSiblings(Element element, String localName) {
        return DocumentHelper.getPrecedingSiblings(element, getNamespaceURI(), localName);
    }
    
    /**
     * Saves the XML.
     * @param stream The stream to write to.
     */
    public void save(OutputStream stream) {
        try {
            DocumentHelper.writeDocument(getDocument(), stream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
