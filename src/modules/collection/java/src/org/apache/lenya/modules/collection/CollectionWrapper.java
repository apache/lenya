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
package org.apache.lenya.modules.collection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Document wrapper for collection functionality.
 */
public class CollectionWrapper extends AbstractLogEnabled implements Collection {

    private Document delegate;

    protected static final String[] TYPES = { TYPE_CHILDREN, TYPE_MANUAL, TYPE_LINK };
    protected static final String ATTRIBUTE_ALL_LANGUAGES = "allLanguages";

    private boolean allLanguages = false;

    /**
     * Ctor.
     * @param doc The document.
     * @param logger The logger.
     */
    public CollectionWrapper(Document doc, Logger logger) {
        enableLogging(logger);
        this.delegate = doc;
    }

    public Document getDelegate() {
        return this.delegate;
    }

    private List documentsList;

    /**
     * Returns the list that holds the documents. Use this method to invoke lazy
     * loading.
     * @return A list.
     * @throws DocumentException when something went wrong.
     */
    protected List documents() throws DocumentException {
        load();
        return this.documentsList;
    }

    /**
     * @see org.apache.lenya.modules.collection.Collection#getDocuments()
     */
    public Document[] getDocuments() throws DocumentException {
        return (Document[]) documents().toArray(new Document[documents().size()]);
    }

    /**
     * @see org.apache.lenya.modules.collection.Collection#add(org.apache.lenya.cms.publication.Document)
     */
    public void add(Document document) throws DocumentException {
        documents().add(document);
    }

    /**
     * @see org.apache.lenya.modules.collection.Collection#add(int,
     *      org.apache.lenya.cms.publication.Document)
     */
    public void add(int position, Document document) throws DocumentException {
        documents().add(position, document);
    }

    /**
     * @see org.apache.lenya.modules.collection.Collection#remove(org.apache.lenya.cms.publication.Document)
     */
    public void remove(Document document) throws DocumentException {
        if (!documents().contains(document)) {
            throw new DocumentException("Collection [" + this + "] does not contain document ["
                    + document + "]");
        }
        documents().remove(document);
    }

    private boolean isLoaded = false;

    /**
     * Loads the collection from its XML source.
     */
    protected final void load() {
        if (!this.isLoaded) {
            getLogger().debug("Loading: ");
            NamespaceHelper helper;
            try {
                helper = getNamespaceHelper();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            loadXml(helper);
            this.isLoaded = true;
        }
    }

    protected void loadXml(NamespaceHelper helper) {
        try {
            this.documentsList = new ArrayList();

            Element collectionElement = helper.getDocument().getDocumentElement();
            Element[] documentElements = helper
                    .getChildren(collectionElement, ELEMENT_DOCUMENT);

            for (int i = 0; i < documentElements.length; i++) {
                Element documentElement = documentElements[i];
                Document document = loadDocument(documentElement);
                this.documentsList.add(document);
            }
            
            if (collectionElement.hasAttribute(ATTRIBUTE_TYPE)) {
                this.type = collectionElement.getAttribute(ATTRIBUTE_TYPE);
            }
            
            if (collectionElement.hasAttribute(ATTRIBUTE_HREF)) {
                this.href = collectionElement.getAttribute(ATTRIBUTE_HREF);
            }
            
            if (collectionElement.hasAttribute(ATTRIBUTE_ALL_LANGUAGES)) {
                String value = collectionElement.getAttribute(ATTRIBUTE_ALL_LANGUAGES);
                this.allLanguages = Boolean.valueOf(value).booleanValue();
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads a document from an XML element.
     * @param documentElement The XML element.
     * @return A document.
     * @throws DocumentBuildException when something went wrong.
     */
    protected Document loadDocument(Element documentElement) throws DocumentBuildException {
        String documentId = documentElement.getAttribute(ATTRIBUTE_UUID);
        Document document = getDelegate().getFactory().get(getDelegate().getPublication(),
                getDelegate().getArea(), documentId, getDelegate().getLanguage());
        return document;
    }

    /**
     * Saves the collection.
     */
    public final void save() {
        try {
            NamespaceHelper helper = getNamespaceHelper();
            saveXml(helper);
            DocumentHelper.writeDocument(helper.getDocument(), getDelegate().getOutputStream());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param helper Save the XML to the provided namespace helper.
     * @throws TransformerException if an error occurs.
     * @throws DocumentException if an error occurs.
     */
    protected void saveXml(NamespaceHelper helper) throws TransformerException, DocumentException {
        Element collectionElement = helper.getDocument().getDocumentElement();
        if (collectionElement.getAttributeNS(null, ATTRIBUTE_UUID).equals("")
                || collectionElement.getAttribute(ATTRIBUTE_UUID).equals("")) {
            collectionElement.setAttributeNS(null, ATTRIBUTE_UUID, getDelegate().getUUID());
        }
        Element[] existingDocumentElements = helper.getChildren(collectionElement,
                ELEMENT_DOCUMENT);
        for (int i = 0; i < existingDocumentElements.length; i++) {
            collectionElement.removeChild(existingDocumentElements[i]);
        }

        collectionElement.setAttribute(ATTRIBUTE_TYPE, getType());
        collectionElement.setAttribute(ATTRIBUTE_HREF, getHref());
        collectionElement.setAttribute(ATTRIBUTE_ALL_LANGUAGES, Boolean.toString(showAllLanguages()));


        collectionElement.normalize();

        NodeList emptyTextNodes = XPathAPI.selectNodeList(collectionElement, "text()");
        for (int i = 0; i < emptyTextNodes.getLength(); i++) {
            Node node = emptyTextNodes.item(i);
            node = collectionElement.removeChild(node);
        }

        Document[] documents = getDocuments();
        for (int i = 0; i < documents.length; i++) {
            Element documentElement = createDocumentElement(documents[i], helper);
            collectionElement.appendChild(documentElement);
        }
    }

    public String getType() {
        load();
        return this.type;
    }

    /**
     * Creates an element to store a document.
     * @param helper The namespace helper of the document.
     * @param document The document.
     * @return An XML element.
     * @throws DocumentException when something went wrong.
     */
    protected Element createDocumentElement(Document document, NamespaceHelper helper)
            throws DocumentException {
        try {
            Element documentElement = helper.createElement(ELEMENT_DOCUMENT);
            documentElement.setAttributeNS(null, ATTRIBUTE_UUID, document.getUUID());
            return documentElement;
        } catch (final DOMException e) {
            throw new DocumentException(e);
        }
    }

    /**
     * Returns the namespace helper for the XML source.
     * @return A namespace helper.
     * @throws DocumentException when something went wrong.
     * @throws ParserConfigurationException when something went wrong.
     * @throws SAXException when something went wrong.
     * @throws IOException when something went wrong.
     * @throws ServiceException
     */
    protected NamespaceHelper getNamespaceHelper() throws DocumentException,
            ParserConfigurationException, SAXException, IOException, ServiceException {

        NamespaceHelper helper;

        if (getDelegate().getContentLength() > 0) {
            org.w3c.dom.Document document = DocumentHelper.readDocument(getDelegate()
                    .getInputStream());
            helper = new NamespaceHelper(Collection.NAMESPACE, Collection.DEFAULT_PREFIX, document);
        } else {
            helper = initializeNamespaceHelper();
        }
        return helper;
    }

    /**
     * @return A new, empty namespace helper.
     */
    protected NamespaceHelper initializeNamespaceHelper() {
        NamespaceHelper helper;
        try {
            helper = new NamespaceHelper(Collection.NAMESPACE, Collection.DEFAULT_PREFIX,
                    ELEMENT_COLLECTION);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        return helper;
    }

    /**
     * @see org.apache.lenya.modules.collection.Collection#contains(org.apache.lenya.cms.publication.Document)
     */
    public boolean contains(Document document) throws DocumentException {
        return documents().contains(document);
    }

    /**
     * @see org.apache.lenya.modules.collection.Collection#clear()
     */
    public void clear() throws DocumentException {
        documents().clear();
    }

    /**
     * @see org.apache.lenya.modules.collection.Collection#getFirstPosition(org.apache.lenya.cms.publication.Document)
     */
    public int getFirstPosition(Document document) throws DocumentException {
        load();
        if (!contains(document)) {
            throw new DocumentException("The collection [" + this
                    + "] does not contain the document [" + document + "]");
        }
        return documents().indexOf(document);
    }

    /**
     * @see org.apache.lenya.modules.collection.Collection#size()
     */
    public int size() throws DocumentException {
        return documents().size();
    }

    private String type = TYPE_MANUAL;

    public void setType(String type) {
        load();
        if (!Arrays.asList(TYPES).contains(type)) {
            throw new IllegalArgumentException("The type [" + type + "] is not supported!");
        }
        this.type = type;
    }
    
    private String href = "";

    public String getHref() {
        load();
        return this.href;
    }

    public void setHref(String href) {
        load();
        this.href = href;
    }

    /**
     * @return If the rendered page should show children in all languages (<code>true</code>) or
     *         only in the currently requested language (<code>false</code>).
     */
    public boolean showAllLanguages() {
        load();
        return this.allLanguages;
    }

    /**
     * @param all If the rendered page should show children in all languages.
     * @see #includeAllLanguages()
     */
    public void setShowAllLanguages(boolean all) {
        load();
        this.allLanguages = all;
    }

}
