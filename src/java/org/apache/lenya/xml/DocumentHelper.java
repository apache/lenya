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

package org.apache.lenya.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xml.resolver.tools.CatalogResolver;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

/**
 * Various utility methods to work with JAXP.
 * @version $Id$
 */
public class DocumentHelper {
    
    private static EntityResolver entityResolver = null;
    
    /**
     * @param resolver The entity resolver to use.
     */
    public static void setEntityResolver(EntityResolver resolver) {
        entityResolver = resolver;
    }
    
    /**
     * Creates a non-validating and namespace-aware DocumentBuilder.
     * @return A new DocumentBuilder object.
     * @throws ParserConfigurationException if an error occurs
     */
    public static DocumentBuilder createBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();

        EntityResolver resolver = entityResolver != null ? entityResolver : new CatalogResolver();
        builder.setEntityResolver(resolver);
        return builder;
    }

    /**
     * Creates a document. A xmlns:prefix="namespaceUri" attribute is added to
     * the document element.
     * @param namespaceUri The namespace URL of the root element.
     * @param qualifiedName The qualified name of the root element.
     * @param documentType The type of document to be created or null. When
     *            doctype is not null, its Node.ownerDocument attribute is set
     *            to the document being created.
     * @return A new Document object.
     * @throws DOMException if an error occurs
     * @throws ParserConfigurationException if an error occurs
     * @see org.w3c.dom.DOMImplementation#createDocument(String, String,
     *      DocumentType)
     */
    public static Document createDocument(String namespaceUri, String qualifiedName,
            DocumentType documentType) throws DOMException, ParserConfigurationException {
        DocumentBuilder builder = createBuilder();
        Document document = builder.getDOMImplementation().createDocument(namespaceUri,
                qualifiedName,
                documentType);

        // add xmlns:prefix attribute
        String name = "xmlns";
        int index = qualifiedName.indexOf(":");

        if (index > -1) {
            name += (":" + qualifiedName.substring(0, index));
        }

        document.getDocumentElement().setAttributeNS("http://www.w3.org/2000/xmlns/",
                name,
                namespaceUri);

        return document;
    }

    /**
     * Reads a document from a file.
     * @return A document.
     * @param file The file to load the document from.
     * @throws ParserConfigurationException if an error occurs
     * @throws SAXException if an error occurs
     * @throws IOException if an error occurs
     */
    public static Document readDocument(File file) throws ParserConfigurationException,
            SAXException, IOException {
        DocumentBuilder builder = createBuilder();
        return builder.parse(file);
    }

    /**
     * Reads a document from a URL.
     * @return A document.
     * @param url The URL to load the document from.
     * @throws ParserConfigurationException if an error occurs
     * @throws SAXException if an error occurs
     * @throws IOException if an error occurs
     */
    public static Document readDocument(URL url) throws ParserConfigurationException, SAXException,
            IOException {
        DocumentBuilder builder = createBuilder();
        return builder.parse(url.toString());
    }

    /**
     * Reads a document from a URI.
     * @return A document.
     * @param uri The URI to load the document from.
     * @throws ParserConfigurationException if an error occurs
     * @throws SAXException if an error occurs
     * @throws IOException if an error occurs
     */
    public static Document readDocument(URI uri) throws ParserConfigurationException, SAXException,
            IOException {
        DocumentBuilder builder = createBuilder();
        return builder.parse(uri.toString());
    }

    /**
     * Reads a document from a string.
     * @return A document.
     * @param string The string to load the document from.
     * @param encoding The encoding which is used by the string.
     * @throws ParserConfigurationException if an error occurs
     * @throws SAXException if an error occurs
     * @throws IOException if an error occurs
     */
    public static Document readDocument(String string, String encoding) throws ParserConfigurationException,
            SAXException, IOException {
        DocumentBuilder builder = createBuilder();
        byte bytes[] = string.getBytes(encoding);
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        return builder.parse(stream);
    }

    /**
     * Reads a document from an input stream.
     * @return A document.
     * @param stream The input stream to load the document from.
     * @throws ParserConfigurationException if an error occurs
     * @throws SAXException if an error occurs
     * @throws IOException if an error occurs
     */
    public static Document readDocument(InputStream stream) throws ParserConfigurationException,
            SAXException, IOException {
        DocumentBuilder builder = createBuilder();
        return builder.parse(stream);
    }

    /**
     * Writes a document to a file. A new file is created if it does not exist.
     * @param document The document to save.
     * @param file The file to save the document to.
     * @throws IOException if an error occurs
     * @throws TransformerConfigurationException if an error occurs
     * @throws TransformerException if an error occurs
     */
    public static void writeDocument(Document document, File file)
            throws TransformerConfigurationException, TransformerException, IOException {
        // sanity checks
        if (document == null)
            throw new IllegalArgumentException("illegal usage, parameter document may not be null");
        if (file == null)
            throw new IllegalArgumentException("illegal usage, parameter file may not be null");

        file.getParentFile().mkdirs();
        file.createNewFile();

        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(file);
        getTransformer(document.getDoctype()).transform(source, result);
    }

    /**
     * Writes a document to a writer.
     * @param document The document to write.
     * @param writer The writer to write the document to.
     * @throws TransformerConfigurationException if an error occurs
     * @throws TransformerException if an error occurs
     */
    public static void writeDocument(Document document, Writer writer)
            throws TransformerConfigurationException, TransformerException {

        // sanity checks
        if (document == null)
            throw new IllegalArgumentException("illegal usage of DocumentHelper::writeDocument(), parameter document may not be null");

        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(writer);
        getTransformer(document.getDoctype()).transform(source, result);
    }

    /**
     * Writes a document to an output stream.
     * @param document The document to write.
     * @param outputStream The stream to write the document to.
     * @throws TransformerConfigurationException if an error occurs
     * @throws TransformerException if an error occurs
     */
    public static void writeDocument(Document document, OutputStream outputStream)
            throws TransformerConfigurationException, TransformerException {

        // sanity checks
        if (document == null)
            throw new IllegalArgumentException("illegal usage of DocumentHelper::writeDocument(), parameter document may not be null");

        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(outputStream);
        try {
            getTransformer(document.getDoctype()).transform(source, result);
        }
        finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            }
            catch (Exception ignore) {
                
            }
        }
    }

    /**
     * Get the transformer.
     * @param documentType the document type
     * @return a transformer
     * @throws TransformerConfigurationException if an error occurs
     */
    protected static Transformer getTransformer(DocumentType documentType)
            throws TransformerConfigurationException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");

        if (documentType != null) {
            if (documentType.getPublicId() != null)
                transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, documentType.getPublicId());
            if (documentType.getSystemId() != null)
                transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, documentType.getSystemId());
        }

        return transformer;
    }

    /**
     * Creates a document type.
     * @param qualifiedName The qualified name of the document type.
     * @param publicId The public identifier.
     * @param systemId The system identifier.
     * @return the document type
     * @throws ParserConfigurationException if an error occurs
     * @see org.w3c.dom.DOMImplementation#createDocumentType(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public DocumentType createDocumentType(String qualifiedName, String publicId, String systemId)
            throws ParserConfigurationException {
        DocumentBuilder builder = createBuilder();

        return builder.getDOMImplementation().createDocumentType(qualifiedName, publicId, systemId);
    }

    /**
     * Returns the first child element of an element that belong to a certain
     * namespace or <code>null</code> if none exists.
     * @param element The parent element.
     * @param namespaceUri The namespace that the childen must belong to.
     * @return The first child element or <code>null</code> if none exists.
     */
    public static Element getFirstChild(Element element, String namespaceUri) {
        return getFirstChild(element, namespaceUri, "*");
    }

    /**
     * Returns the first child element of an element that belongs to a certain
     * namespace and has a certain local name or <code>null</code> if none
     * exists.
     * @param element The parent element.
     * @param namespaceUri The namespace that the childen must belong to.
     * @param localName The local name of the children.
     * @return The child element or <code>null</code> if none exists.
     */
    public static Element getFirstChild(Element element, String namespaceUri, String localName) {
        Element[] children = getChildren(element, namespaceUri, localName);

        if (children.length > 0) {
            return children[0];
        }
        return null;
    }

    /**
     * Returns all child elements of an element, regardless of the namespace.
     * @param element The parent element.
     * @return The child elements.
     */
    public static Element[] getChildren(Element element) {
        List childElements = new ArrayList();
        NodeList children = element.getElementsByTagName("*");

        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i).getParentNode() == element) {
                childElements.add(children.item(i));
            }
        }

        return (Element[]) childElements.toArray(new Element[childElements.size()]);
    }

    /**
     * Returns all child elements of an element that belong to a certain
     * namespace.
     * @param element The parent element.
     * @param namespaceUri The namespace that the childen must belong to.
     * @return The child elements.
     */
    public static Element[] getChildren(Element element, String namespaceUri) {
        return getChildren(element, namespaceUri, "*");
    }

    /**
     * Returns all child elements of an element that belong to a certain
     * namespace and have a certain local name.
     * @param element The parent element.
     * @param namespaceUri The namespace that the childen must belong to.
     * @param localName The local name of the children.
     * @return The child elements.
     */
    public static Element[] getChildren(Element element, String namespaceUri, String localName) {
        List childElements = new ArrayList();
        NodeList children = element.getElementsByTagNameNS(namespaceUri, localName);

        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i).getParentNode() == element) {
                childElements.add(children.item(i));
            }
        }

        return (Element[]) childElements.toArray(new Element[childElements.size()]);
    }

    /**
     * Returns the text inside an element. Only the child text nodes of this
     * element are collected.
     * @param element The element.
     * @return The text inside the element.
     */
    public static String getSimpleElementText(Element element) {
        StringBuffer buffer = new StringBuffer();
        NodeList children = element.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);

            if (child instanceof Text) {
                buffer.append(child.getNodeValue());
            }
        }

        return buffer.toString();
    }

    /**
     * Replaces all child nodes of an element by a single text node.
     * @param element The element.
     * @param text The text to insert.
     */
    public static void setSimpleElementText(Element element, String text) {
        NodeList children = element.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            element.removeChild(child);
        }

        Node textNode = element.getOwnerDocument().createTextNode(text);
        element.appendChild(textNode);
    }

    /**
     * Returns all following sibling elements of an element that belong to a
     * certain namespace.
     * @param element The parent element.
     * @param namespaceUri The namespace that the childen must belong to.
     * @return The following sibling elements.
     */
    public static Element[] getNextSiblings(Element element, String namespaceUri) {
        return getNextSiblings(element, namespaceUri, "*");
    }

    /**
     * Returns all following sibling elements of an element that belong to a
     * certain namespace. and have a certain local name.
     * @param element The parent element.
     * @param namespaceUri The namespace that the childen must belong to.
     * @param localName The local name of the children.
     * @return The following sibling elements.
     */
    public static Element[] getNextSiblings(Element element, String namespaceUri, String localName) {
        List childElements = new ArrayList();
        Element parent = (Element) element.getParentNode();
        Element[] children = getChildren(parent, namespaceUri, localName);

        int l = children.length;
        for (int i = 0; i < children.length; i++) {
            if (children[i] == element) {
                l = i;
            }
            if (i > l) {
                childElements.add(children[i]);
            }
        }

        return (Element[]) childElements.toArray(new Element[childElements.size()]);
    }

    /**
     * Returns all preceding sibling elements of an element that belong to a
     * certain namespace.
     * @param element The parent element.
     * @param namespaceUri The namespace that the childen must belong to.
     * @return The preceding sibling elements.
     */
    public static Element[] getPrecedingSiblings(Element element, String namespaceUri) {
        return getPrecedingSiblings(element, namespaceUri, "*");
    }

    /**
     * Returns all preceding sibling elements of an element that belong to a
     * certain namespace. and have a certain local name.
     * @param element The parent element.
     * @param namespaceUri The namespace that the childen must belong to.
     * @param localName The local name of the children.
     * @return The preceding sibling elements.
     */
    public static Element[] getPrecedingSiblings(Element element, String namespaceUri,
            String localName) {
        List childElements = new ArrayList();
        Element parent = (Element) element.getParentNode();
        Element[] children = getChildren(parent, namespaceUri, localName);

        int i = 0;
        while (children[i] != element && i < children.length) {
            childElements.add(children[i]);
            i++;
        }

        return (Element[]) childElements.toArray(new Element[childElements.size()]);
    }
}
