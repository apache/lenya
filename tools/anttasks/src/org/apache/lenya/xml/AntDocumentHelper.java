package org.apache.lenya.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * Slightly modified version of Lenya's DocumentBuilder to be used in Ant tasks.
 */
public class AntDocumentHelper {

    /**
     * Creates a non-validating and namespace-aware DocumentBuilder.
     * @return A new DocumentBuilder object.
     * @throws ParserConfigurationException if an error occurs
     */
    public static DocumentBuilder createBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder;
    }

    /**
     * Creates a document. A xmlns:prefix="namespaceUri" attribute is added to the document element.
     * @param namespaceUri The namespace URL of the root element.
     * @param qualifiedName The qualified name of the root element.
     * @param documentType The type of document to be created or null. When doctype is not null, its
     *            Node.ownerDocument attribute is set to the document being created.
     * @return A new Document object.
     * @throws DOMException if an error occurs
     * @throws ParserConfigurationException if an error occurs
     * @see org.w3c.dom.DOMImplementation#createDocument(String, String, DocumentType)
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
        FileOutputStream out = new FileOutputStream(file);
        StreamResult result = new StreamResult(out);
        getTransformer(document.getDoctype()).transform(source, result);
        out.close();
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
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
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

}
