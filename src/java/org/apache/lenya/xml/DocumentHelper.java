/*
 * $Id: DocumentHelper.java,v 1.1 2003/02/07 15:48:08 ah Exp $
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

import java.io.File;
import java.io.IOException;
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
import org.xml.sax.SAXException;

/**
 * Various utility methods to work with JAXP.
 *
 * @author andreas
 */
public class DocumentHelper {

    /**
     * Creates a validating and namespace-aware DocumentBuilder.
     *
     * @return A new DocumentBuilder object.
     * @throws ParserConfigurationException
     */
    public static DocumentBuilder createBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);

        return factory.newDocumentBuilder();
    }

    /**
     * Creates a document. A xmlns:prefix="namespaceUri" attribute is added to
     * the document element.
     *
     * @param namespaceUri The namespace URI of the root element.
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
                
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        
        file.createNewFile();

        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
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
    
}
