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

package org.apache.lenya.cms.publication;

import java.io.IOException;

import org.apache.lenya.xml.NamespaceHelper;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.XLink;
import org.apache.log4j.Category;
import org.w3c.dom.Element;

/**
 * Implementation of a Collection. In the collection are xlink inserted.
 * @author <a href="mailto:edith@apache.org">Edith Chevrier</a>
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann</a>
 * @version $Id: XlinkCollection.java,v 1.4 2004/04/13 15:08:59 andreas Exp $
 */
public class XlinkCollection extends CollectionImpl {

    private static final Category log = Category.getInstance(XlinkCollection.class);

    /**
     * Ctor.
     * @param publication A publication.
     * @param id The document ID.
     * @param area The area the document belongs to.
     * @throws DocumentException when something went wrong.
     */
    public XlinkCollection(Publication publication, String id, String area)
        throws DocumentException {
        super(publication, id, area);
    }

    /**
     * Ctor.
     * @param publication A publication.
     * @param id The document ID.
     * @param area The area the document belongs to.
     * @param language The language of the document.
     * @throws DocumentException when something went wrong.
     */
    public XlinkCollection(Publication publication, String id, String area, String language)
        throws DocumentException {
        super(publication, id, area, language);
    }

    /** (non-Javadoc)
     * @see org.apache.lenya.cms.publication.CollectionImpl#createDocumentElement(org.apache.lenya.cms.publication.Document, org.apache.lenya.xml.NamespaceHelper)
     **/
    protected Element createDocumentElement(Document document, NamespaceHelper helper)
        throws DocumentException {
        Element element = super.createDocumentElement(document, helper);
        String path = getXlinkHref(document);
        element.setAttributeNS(XLink.XLINK_NAMESPACE, "xlink:href", path);
        element.setAttributeNS(XLink.XLINK_NAMESPACE, "xlink:show", "embed");

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
            throw new DocumentException(
                "Couldn't found the file path for the document [" + document + "]",
                e);
        }
        return path;
    }

    /**
     * Saves the XML source of this collection.
     * @throws DocumentException when something went wrong.
     */
    public void save() throws DocumentException {
        try {
            NamespaceHelper helper = getNamespaceHelper();
            Element collectionElement = helper.getDocument().getDocumentElement();

            if ((collectionElement.getAttribute("xmlns:xlink") == null)
                | (!collectionElement
                    .getAttribute("xmlns:xlink")
                    .equals("http://www.w3.org/1999/xlink"))) {
                collectionElement.setAttribute("xmlns:xlink", XLink.XLINK_NAMESPACE);
            }
            DocumentHelper.writeDocument(helper.getDocument(), getFile());
            super.save();
        } catch (DocumentException e) {
            throw e;
        } catch (Exception e) {
            throw new DocumentException(e);
        }
    }

}
