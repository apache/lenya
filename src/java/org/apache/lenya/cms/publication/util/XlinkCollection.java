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

package org.apache.lenya.cms.publication.util;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.xml.NamespaceHelper;
import org.apache.lenya.xml.XLink;
import org.apache.log4j.Category;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Implementation of a Collection. In the collection are xlink inserted.
 * @version $Id$
 */
public class XlinkCollection extends CollectionImpl {

    private static final Category log = Category.getInstance(XlinkCollection.class);

    /**
     * Ctor.
     * @param map A document identity map.
     * @param id The document ID.
     * @param area The area the document belongs to.
     * @throws DocumentException when something went wrong.
     */
    public XlinkCollection(DocumentIdentityMap map, String id, String area)
            throws DocumentException {
        super(map, id, area);
    }

    /**
     * Ctor.
     * @param map A document identity map.
     * @param id The document ID.
     * @param area The area the document belongs to.
     * @param language The language of the document.
     * @throws DocumentException when something went wrong.
     */
    public XlinkCollection(DocumentIdentityMap map, String id, String area, String language)
            throws DocumentException {
        super(map, id, area, language);
    }

    /**
     * (non-Javadoc)
     * @see org.apache.lenya.cms.publication.util.CollectionImpl#createDocumentElement(org.apache.lenya.cms.publication.Document,
     *      org.apache.lenya.xml.NamespaceHelper)
     */
    protected Element createDocumentElement(Document document, NamespaceHelper helper)
            throws DocumentException {
        Element element = super.createDocumentElement(document, helper);
        String path = getXlinkHref(document);
        element.setAttributeNS(XLink.XLINK_NAMESPACE, "xlink:" + XLink.ATTRIBUTE_HREF, path);
        element.setAttributeNS(XLink.XLINK_NAMESPACE, "xlink:" + XLink.ATTRIBUTE_SHOW, "embed");
        element.normalize();
        return element;
    }

    /**
     * Returns the href attribute string for a certain document.
     * @param document The document.
     * @return A string.
     * @throws DocumentException when something went wrong.
     */
    protected String getXlinkHref(Document document) throws DocumentException {
        String path = null;
        try {
            path = document.getFile().getCanonicalPath();
        } catch (IOException e) {
            throw new DocumentException("Couldn't found the file path for the document ["
                    + document + "]", e);
        }
        return path;
    }

    /**
     * Adds the XLink namespace declaration to the document element.
     * @see org.apache.lenya.cms.publication.util.CollectionImpl#getNamespaceHelper()
     */
    protected NamespaceHelper getNamespaceHelper() throws DocumentException,
            ParserConfigurationException, SAXException, IOException {
        NamespaceHelper helper = super.getNamespaceHelper();
        if (!exists()) {
            Element collectionElement = helper.getDocument().getDocumentElement();
            String namespaceDeclaration = collectionElement.getAttributeNS(
                    "http://www.w3.org/2000/xmlns/", "xlink");
            if (namespaceDeclaration == null || !namespaceDeclaration.equals(XLink.XLINK_NAMESPACE)) {
                collectionElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xlink",
                        XLink.XLINK_NAMESPACE);
            }
        }
        return helper;
    }

}