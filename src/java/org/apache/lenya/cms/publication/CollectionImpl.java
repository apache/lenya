/*
$Id: CollectionImpl.java,v 1.1 2003/12/04 14:17:21 andreas Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.cms.publication;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Implementation of a Collection.
 *
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann</a>
 */
public class CollectionImpl extends DefaultDocument implements Collection {
    
    /**
     * Ctor.
     * @param publication A publication.
     * @param id The document ID.
     * @param area The area the document belongs to.
     * @throws DocumentException when something went wrong.
     */
    protected CollectionImpl(Publication publication, String id, String area) throws DocumentException {
        super(publication, id, area);
        load();
    }

    /**
     * Ctor.
     * @param publication A publication.
     * @param id The document ID.
     * @param area The area the document belongs to.
     * @param language The language of the document.
     * @throws DocumentException when something went wrong.
     */
    protected CollectionImpl(Publication publication, String id, String area, String language) throws DocumentException {
        super(publication, id, area, language);
        load();
    }

    private List documents = new ArrayList();
    
    /**
     * @see org.apache.lenya.cms.publication.Collection#getDocuments()
     */
    public Document[] getDocuments() throws DocumentException {
        return (Document[]) documents.toArray(new Document[documents.size()]);
    }

    /**
     * @see org.apache.lenya.cms.publication.Collection#add(org.apache.lenya.cms.publication.Document)
     */
    public void add(Document document) throws DocumentException {
        documents.add(document);
        save();
    }

    /**
     * @see org.apache.lenya.cms.publication.Collection#add(int, org.apache.lenya.cms.publication.Document)
     */
    public void add(int position, Document document) throws DocumentException {
        documents.add(position, document);
        save();
    }

    /**
     * @see org.apache.lenya.cms.publication.Collection#remove(org.apache.lenya.cms.publication.Document)
     */
    public void remove(Document document) throws DocumentException {
        if (!documents.contains(document)) {
            throw new DocumentException(
                "Collection [" + this +"] does not contain document [" + document + "]");
        }
        documents.remove(document);
        save();
    }

    private boolean isLoaded = false;

    /**
     * Loads the collection from its XML source.
     * @throws DocumentException when something went wrong.
     */
    protected void load() throws DocumentException {
        if (!isLoaded) {
            NamespaceHelper helper;
            try {
                helper = getNamespaceHelper();

                Element collectionElement = helper.getDocument().getDocumentElement();
                Element[] documentElements = helper.getChildren(collectionElement);

                DocumentBuilder builder = getPublication().getDocumentBuilder();
                for (int i = 0; i < documentElements.length; i++) {
                    String documentId = documentElements[i].getAttribute(ATTRIBUTE_ID);
                    String url =
                        builder.buildCanonicalUrl(
                            getPublication(),
                            getArea(),
                            documentId,
                            getLanguage());
                    Document document = builder.buildDocument(getPublication(), url);
                    add(document);
                }
            } catch (DocumentException e) {
                throw e;
            } catch (Exception e) {
                throw new DocumentException(e);
            }
            isLoaded = true;
        }
    }

    /**
     * Saves the XML source of this collection.
     * @throws DocumentException when something went wrong.
     */
    protected void save() throws DocumentException {
        NamespaceHelper helper;
        try {
            helper = getNamespaceHelper();
            Element collectionElement = helper.getDocument().getDocumentElement();
            
            Document[] documents = getDocuments();
            
            for (int i = 0; i < documents.length; i++) {
                Element documentElement = helper.createElement(ELEMENT_DOCUMENT);
                documentElement.setAttribute(ATTRIBUTE_ID, documents[i].getId());
                collectionElement.appendChild(documentElement);
            }
            
            DocumentHelper.writeDocument(helper.getDocument(), getFile());
            
        } catch (DocumentException e) {
            throw e;
        } catch (Exception e) {
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
     */
    protected NamespaceHelper getNamespaceHelper()
        throws DocumentException, ParserConfigurationException, SAXException, IOException {

        NamespaceHelper helper;

        if (exists()) {
            File file = getFile();
            org.w3c.dom.Document document = DocumentHelper.readDocument(file);
            helper = new NamespaceHelper(Collection.NAMESPACE, Collection.DEFAULT_PREFIX, document);
        } else {
            helper =
                new NamespaceHelper(
                    Collection.NAMESPACE,
                    Collection.DEFAULT_PREFIX,
                    ELEMENT_COLLECTION);
        }
        return helper;
    }

    /**
     * @see org.apache.lenya.cms.publication.Collection#contains(org.apache.lenya.cms.publication.Document)
     */
    public boolean contains(Document document) throws DocumentException {
        return documents.contains(document);
    }

}
