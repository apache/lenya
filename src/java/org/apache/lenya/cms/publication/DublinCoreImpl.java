/*
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Access dublin core meta data in documents.
 * This class uses the dublin core specification from 2003-03-04.
 *
 * @author <a href="mailto:gregor@apache.org">Gregor J. Rothfuss</a>
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann</a>
 * @version $Id: DublinCoreImpl.java,v 1.7 2004/02/02 18:02:13 andreas Exp $
 */
public class DublinCoreImpl implements DublinCore {
    private Document cmsdocument;
    private File infofile;

    private Map elements = new HashMap();
    private Map terms = new HashMap();

    private static final String META = "meta";

    // Dublin Core Elements

    private static final String DC_NAMESPACE = "http://purl.org/dc/elements/1.1/";
    private static final String DC_PREFIX = "dc";

    private static final String[] ELEMENTS =
        {
            ELEMENT_TITLE,
            ELEMENT_CREATOR,
            ELEMENT_SUBJECT,
            ELEMENT_DESCRIPTION,
            ELEMENT_PUBLISHER,
            ELEMENT_CONTRIBUTOR,
            ELEMENT_DATE,
            ELEMENT_TYPE,
            ELEMENT_FORMAT,
            ELEMENT_IDENTIFIER,
            ELEMENT_SOURCE,
            ELEMENT_LANGUAGE,
            ELEMENT_RELATION,
            ELEMENT_COVERAGE,
            ELEMENT_RIGHTS };

    // Dublin Core Terms

    private static final String DCTERMS_NAMESPACE = "http://purl.org/dc/terms/";
    private static final String DCTERMS_PREFIX = "dcterms";

    private static final String[] TERMS =
        {
            TERM_AUDIENCE,
            TERM_ALTERNATIVE,
            TERM_TABLEOFCONTENTS,
            TERM_ABSTRACT,
            TERM_CREATED,
            TERM_VALID,
            TERM_EXTENT,
            TERM_AVAILABLE,
            TERM_ISSUED,
            TERM_MODIFIED,
            TERM_EXTENT,
            TERM_MEDIUM,
            TERM_ISVERSIONOF,
            TERM_HASVERSION,
            TERM_ISREPLACEDBY,
            TERM_REPLACES,
            TERM_ISREQUIREDBY,
            TERM_REQUIRES,
            TERM_ISPARTOF,
            TERM_HASPART,
            TERM_ISREFERENCEDBY,
            TERM_REFERENCES,
            TERM_ISFORMATOF,
            TERM_HASFORMAT,
            TERM_CONFORMSTO,
            TERM_SPATIAL,
            TERM_TEMPORAL,
            TERM_MEDIATOR,
            TERM_DATEACCEPTED,
            TERM_DATECOPYRIGHTED,
            TERM_DATESUBMITTED,
            TERM_EDUCATIONLEVEL,
            TERM_ACCESSRIGHTS,
            TERM_BIBLIOGRAPHICCITATION };

    /** 
     * Creates a new instance of Dublin Core
     * 
     * @param aDocument the document for which the Dublin Core instance is created.
     * 
     * @throws DocumentException if an error occurs
     */
    protected DublinCoreImpl(Document aDocument) throws DocumentException {
        this.cmsdocument = aDocument;
        infofile =
            cmsdocument.getPublication().getPathMapper().getFile(
                cmsdocument.getPublication(),
                cmsdocument.getArea(),
                cmsdocument.getId(),
                cmsdocument.getLanguage());

        org.w3c.dom.Document doc = null;
        try {
            doc = DocumentHelper.readDocument(infofile);
        } catch (ParserConfigurationException e) {
            throw new DocumentException(e);
        } catch (SAXException e) {
            throw new DocumentException(e);
        } catch (IOException e) {
            throw new DocumentException(e);
        }

        // FIXME: what if "lenya:meta" element doesn't exist yet? Currently a NullPointerException will be thrown!
        Element metaElement = getMetaElement(doc);

        String[] namespaces = { DC_NAMESPACE, DCTERMS_NAMESPACE };
        String[] prefixes = { DC_PREFIX, DCTERMS_PREFIX };
        String[][] arrays = { ELEMENTS, TERMS };
        Map[] maps = { elements, terms };

        for (int type = 0; type < 2; type++) {
            NamespaceHelper helper = new NamespaceHelper(namespaces[type], prefixes[type], doc);
            String[] elementNames = arrays[type];
            for (int i = 0; i < elementNames.length; i++) {
                Element[] children = helper.getChildren(metaElement, elementNames[i]);
                String[] values = new String[children.length];
                for (int valueIndex = 0; valueIndex < children.length; valueIndex++) {
                    values[valueIndex] = DocumentHelper.getSimpleElementText(children[valueIndex]);
                }
                maps[type].put(elementNames[i], values);
            }
        }
    }

    /**
     * Save the meta data.
     *
     * @throws DocumentException if the meta data could not be made persistent.
     */
    public void save() throws DocumentException {
        org.w3c.dom.Document doc = null;
        try {
            doc = DocumentHelper.readDocument(infofile);
        } catch (ParserConfigurationException e) {
            throw new DocumentException(e);
        } catch (SAXException e) {
            throw new DocumentException(e);
        } catch (IOException e) {
            throw new DocumentException(e);
        }

        Element metaElement = getMetaElement(doc);

        String[] namespaces = { DC_NAMESPACE, DCTERMS_NAMESPACE };
        String[] prefixes = { DC_PREFIX, DCTERMS_PREFIX };
        String[][] arrays = { ELEMENTS, TERMS };
        Map[] maps = { elements, terms };

        for (int type = 0; type < 2; type++) {
            NamespaceHelper helper = new NamespaceHelper(namespaces[type], prefixes[type], doc);
            String[] elementNames = arrays[type];
            for (int i = 0; i < elementNames.length; i++) {
                Element[] children = helper.getChildren(metaElement, elementNames[i]);
                for (int valueIndex = 0; valueIndex < children.length; valueIndex++) {
                    metaElement.removeChild(children[valueIndex]);
                }
                String[] values = (String[]) maps[type].get(elementNames[i]);
                for (int valueIndex = 0; valueIndex < values.length; valueIndex++) {
                    Element valueElement =
                        helper.createElement(elementNames[i], values[valueIndex]);
                    metaElement.appendChild(valueElement);
                }
            }
        }

        try {
            DocumentHelper.writeDocument(doc, infofile);
        } catch (TransformerConfigurationException e) {
            throw new DocumentException(e);
        } catch (TransformerException e) {
            throw new DocumentException(e);
        } catch (IOException e) {
            throw new DocumentException(e);
        }

    }

    /**
     * Returns the Lenya meta data element.
     * @param doc The XML document.
     * @return A DOM element.
     */
    protected Element getMetaElement(org.w3c.dom.Document doc) {
        NamespaceHelper namespaceHelper =
            new NamespaceHelper(PageEnvelope.NAMESPACE, PageEnvelope.DEFAULT_PREFIX, doc);
        Element documentElement = doc.getDocumentElement();
        Element metaElement = namespaceHelper.getFirstChild(documentElement, META);
        return metaElement;
    }

    /**
     * Returns the first value for a certain key.
     * @param key The key.
     * @return A string.
     */
    public String getFirstValue(String key) {
        String value = null;
        String[] values = (String[]) elements.get(key);
        if (values != null && values.length > 0) {
            value = values[0];
        }
        return value;
    }

    /**
     * Get the creator
     * 
     * @return the creator
     * 
     * @throws DocumentException if an error occurs
     */
    public String getCreator() throws DocumentException {
        return getFirstValue(ELEMENT_CREATOR);
    }

    /**
     * Set the DC creator
     * 
     * @param creator the Creator
     */
    public void setCreator(String creator) {
        try {
            addValue(ELEMENT_CREATOR, creator);
        } catch (DocumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Get the title
     * 
     * @return the title
     * 
     * @throws DocumentException if an error occurs
     */
    public String getTitle() throws DocumentException {
        return getFirstValue(ELEMENT_TITLE);
    }

    /**
     * Set the DC title
     * 
     * @param title the title
     */
    public void setTitle(String title) {
        try {
            addValue(ELEMENT_TITLE, title);
        } catch (DocumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Get the description
     * 
     * @return the description
     * 
     * @throws DocumentException if an error occurs
     */
    public String getDescription() throws DocumentException {
        return getFirstValue(ELEMENT_DESCRIPTION);
    }

    /**
     * Set the DC Description
     * 
     * @param description the description
     */
    public void setDescription(String description) {
        try {
            addValue(ELEMENT_DESCRIPTION, description);
        } catch (DocumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Get the identifier
     * 
     * @return the identifier
     * 
     * @throws DocumentException if an error occurs
     */
    public String getIdentifier() throws DocumentException {
        return getFirstValue(ELEMENT_IDENTIFIER);
    }

    /**
     * Set the DC Identifier
     * 
     * @param identifier the identifier
     */
    public void setIdentifier(String identifier) {
        try {
            addValue(ELEMENT_IDENTIFIER, identifier);
        } catch (DocumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Get the subject.
     * 
     * @return the subject
     * 
     * @throws DocumentException if an error occurs
     */
    public String getSubject() throws DocumentException {
        return getFirstValue(ELEMENT_SUBJECT);
    }

    /**
     * Set the DC Subject
     * 
     * @param subject the subject
     */
    public void setSubject(String subject) {
        try {
            addValue(ELEMENT_SUBJECT, subject);
        } catch (DocumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Get the publisher
     * 
     * @return the publisher
     * 
     * @throws DocumentException if an error occurs
     */
    public String getPublisher() throws DocumentException {
        return getFirstValue(ELEMENT_PUBLISHER);
    }

    /**
     * Set the publisher
     * 
     * @param publisher the publisher
     */
    public void setPublisher(String publisher) {
        try {
            addValue(ELEMENT_PUBLISHER, publisher);
        } catch (DocumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Get the date of issue
     * 
     * @return the date of issue
     * 
     * @throws DocumentException if an error occurs
     */
    public String getDateIssued() throws DocumentException {
        return getFirstValue(TERM_ISSUED);
    }

    /**
     * Set the date of issue
     * 
     * @param dateIssued the date of issue
     */
    public void setDateIssued(String dateIssued) {
        try {
            addValue(TERM_ISSUED, dateIssued);
        } catch (DocumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Get the date of creation
     * 
     * @return the date of creation
     * 
     * @throws DocumentException if an error occurs
     */
    public String getDateCreated() throws DocumentException {
        return getFirstValue(TERM_CREATED);
    }

    /**
     * Set the date of creation
     * 
     * @param dateCreated the date of creation
     */
    public void setDateCreated(String dateCreated) {
        try {
            addValue(TERM_CREATED, dateCreated);
        } catch (DocumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Get the rights
     * 
     * @return the rights
     * 
     * @throws DocumentException if an error occurs
     */
    public String getRights() throws DocumentException {
        return getFirstValue(ELEMENT_RIGHTS);
    }

    /**
     * Set the DC Rights
     * 
     * @param rights the rights
     */
    public void setRights(String rights) {
        try {
            addValue(ELEMENT_RIGHTS, rights);
        } catch (DocumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Get isReferencedBy
     * 
     * @return isReferencedBy
     * 
     * @throws DocumentException if an error occurs
     */
    public String getIsReferencedBy() throws DocumentException {
        return getFirstValue(TERM_ISREFERENCEDBY);
    }

    /**
     * Set isReferencedBy
     * 
     * @param isReferencedBy isReferencedBy
     */
    public void setIsReferencedBy(String isReferencedBy) {
        try {
            addValue(TERM_ISREFERENCEDBY, isReferencedBy);
        } catch (DocumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Returns the element or term values, resp.,  for a certain key.
     * @param key The key.
     * @return An array of strings.
     */
    protected String[] getElementOrTerm(String key) throws DocumentException {
        String[] values;

        List elementList = Arrays.asList(ELEMENTS);
        List termList = Arrays.asList(TERMS);
        if (elementList.contains(key)) {
            values = (String[]) elements.get(key);
        } else if (termList.contains(key)) {
            values = (String[]) terms.get(key);
        } else {
            throw new DocumentException(
                "The key [" + key + "] does not refer to a dublin core element or term!");
        }
        if (values == null) {
            values = new String[0];
        }
        return values;
    }

    /**
     * @see org.apache.lenya.cms.publication.DublinCore#getValues(java.lang.String)
     */
    public String[] getValues(String key) throws DocumentException {
        return getElementOrTerm(key);
    }

    /**
     * @see org.apache.lenya.cms.publication.DublinCore#addValue(java.lang.String, java.lang.String)
     */
    public void addValue(String key, String value) throws DocumentException {
        String[] existingValues = getElementOrTerm(key);
        List list = new ArrayList(Arrays.asList(existingValues));
        list.add(value);
        String[] newValues = (String[]) list.toArray(new String[list.size()]);

        List elementList = Arrays.asList(ELEMENTS);
        List termList = Arrays.asList(TERMS);
        if (elementList.contains(key)) {
            elements.put(key, newValues);
        } else if (termList.contains(key)) {
            terms.put(key, newValues);
        } else {
            throw new DocumentException(
                "The key [" + key + "] does not refer to a dublin core element or term!");
        }
    }

    /**
     * @see org.apache.lenya.cms.publication.DublinCore#removeValue(java.lang.String, java.lang.String)
     */
    public void removeValue(String key, String value) throws DocumentException {
        String[] existingValues = getElementOrTerm(key);
        List list = new ArrayList(Arrays.asList(existingValues));

        if (!list.contains(value)) {
            throw new DocumentException(
                "The key [" + key + "] does not contain the value [" + value + "]!");
        }

        list.remove(value);
        String[] newValues = (String[]) list.toArray(new String[list.size()]);

        List elementList = Arrays.asList(ELEMENTS);
        List termList = Arrays.asList(TERMS);
        if (elementList.contains(key)) {
            elements.put(key, newValues);
        } else if (termList.contains(key)) {
            terms.put(key, newValues);
        } else {
            throw new DocumentException(
                "The key [" + key + "] does not refer to a dublin core element or term!");
        }
    }

    /**
     * @see org.apache.lenya.cms.publication.DublinCore#removeAllValues(java.lang.String)
     */
    public void removeAllValues(String key) throws DocumentException {
        List elementList = Arrays.asList(ELEMENTS);
        List termList = Arrays.asList(TERMS);
        if (elementList.contains(key)) {
            elements.put(key, new String[0]);
        } else if (termList.contains(key)) {
            terms.put(key, new String[0]);
        } else {
            throw new DocumentException(
                "The key [" + key + "] does not refer to a dublin core element or term!");
        }
    }

}
