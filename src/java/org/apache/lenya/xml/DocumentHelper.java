/*
 * $Id: DocumentHelper.java,v 1.9 2003/03/04 19:44:56 gregor Exp $
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

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

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

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Various utility methods to work with JAXP.
 *
 * @author andreas
 */
public class DocumentHelper {

    /**
     * Creates a non-validating and namespace-aware DocumentBuilder.
     *
     * @return A new DocumentBuilder object.
     * @throws ParserConfigurationException
     */
    public static DocumentBuilder createBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        return factory.newDocumentBuilder();
    }

    /**
     * Creates a document. A xmlns:prefix="namespaceUri" attribute is added to
     * the document element.
     *
     * @param namespaceUri The namespace URL of the root element.
     * @param qualifiedName The qualified name of the root element.
     * @param documentType The type of document to be created or null. When doctype is not null,
     *        its Node.ownerDocument attribute is set to the document being created.
     * @return A new Document object.
     * @throws DOMException
     * @see org.w3c.dom.DOMImplementation#createDocument(String, String, DocumentType)
     */
    public static Document createDocument(String namespaceUri, String qualifiedName,
            DocumentType documentType)
        throws DOMException, ParserConfigurationException {
            
        DocumentBuilder builder = createBuilder();
        Document document = builder.getDOMImplementation().createDocument(
                namespaceUri, qualifiedName, documentType);
        
        // add xmlns:prefix attribute
        
        String name = "xmlns";
        int index = qualifiedName.indexOf(":");
        if (index > -1)
            name += ":" + qualifiedName.substring(0, index);
        
        document.getDocumentElement().setAttribute(name, namespaceUri);
        
        return document;
    }
    
    /**
     * Reads a document from a file.
     * @return A document.
     * @param file The file to load the document from.
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */    
    public static Document readDocument(File file)
            throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder builder = createBuilder();
        return builder.parse(file);
    }
    
    /**
     * Reads a document from a URL.
     * @return A document.
     * @param url The URL to load the document from.
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public static Document readDocument(URL url)
            throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder builder = createBuilder();
        return builder.parse(url.toString());
    }
    
    /** Writes a document to a file. A new file is created if it does not exist.
     *
     * @param document The document to save.
     * @param file The file to save the document to.
     * @throws IOException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */    
    public static void writeDocument(Document document, File file)
            throws TransformerConfigurationException, TransformerException, IOException {
                
        file.createNewFile();
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(file);
        getTransformer().transform(source, result);
    }
    
    /** Writes a document to a writer.
     *
     * @param document The document to write.
     * @param writer The writer to write the document to.
     * @throws IOException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */    
    public static void writeDocument(Document document, Writer writer)
            throws TransformerConfigurationException, TransformerException, IOException {
                
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(writer);
        getTransformer().transform(source, result);
    }
    
    protected static Transformer getTransformer()
            throws TransformerConfigurationException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        return transformer;
    }
    
    /**
     * Creates a document type.
     * @param qualifiedName The qualified name of the document type.
     * @param publicId The public identifier.
     * @param systemId The system identifier.
     * @return
     * @see DOMImplementation.createDocumentType(String, String, String)
     */
    public DocumentType createDocumentType(String qualifiedName, String publicId, String systemId)
            throws ParserConfigurationException {
        DocumentBuilder builder = createBuilder();
        return builder.getDOMImplementation().createDocumentType(
                qualifiedName, publicId, systemId);
    }
    
    
    /**
     * Returns the first child element of an element that belong to a certain namespace
     * or <code>null</code> if none exists.
     * @param element The parent element.
     * @param namespaceUri The namespace that the childen must belong to.
     * @return The first child element or <code>null</code> if none exists.
     */    
    public static Element getFirstChild(Element element, String namespaceUri) {
        return getFirstChild(element, namespaceUri, "*");
    }
    
    /**
     * Returns the first child element of an element that belongs to a certain namespace
     * and has a certain local name or <code>null</code> if none exists.
     * @param element The parent element.
     * @param namespaceUri The namespace that the childen must belong to.
     * @return The child elements.
     * @param localName The local name of the children.
     * @return The child element or <code>null</code> if none exists.
     */    
    public static Element getFirstChild(Element element, String namespaceUri, String localName) {
        Element children[] = getChildren(element, namespaceUri, localName);
        if (children.length > 0)
            return children[0];
        else
            return null;
    }

    /**
     * Returns all child elements of an element that belong to a certain namespace.
     * @param element The parent element.
     * @param namespaceUri The namespace that the childen must belong to.
     * @return The child elements.
     */    
    public static Element[] getChildren(Element element, String namespaceUri) {
        return getChildren(element, namespaceUri, "*");
    }
    
    /**
     * Returns all child elements of an element that belong to a certain namespace
     * and have a certain local name.
     * @param element The parent element.
     * @param namespaceUri The namespace that the childen must belong to.
     * @return The child elements.
     * @param localName The local name of the children.
     * @return The children.
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
}
