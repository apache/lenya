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

import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Access dublin core meta data in documents.
 * This class uses the dublin core specification from 2003-03-04.
 *
 * @author <a href="mailto:gregor@apache.org">Gregor J. Rothfuss</a>
 * @version $Id: DublinCoreImpl.java,v 1.4 2004/01/30 18:36:53 andreas Exp $
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

    private static final String ELEMENT_TITLE = "title";
    private static final String ELEMENT_CREATOR = "creator";
    private static final String ELEMENT_SUBJECT = "subject";
    private static final String ELEMENT_DESCRIPTION = "description";
    private static final String ELEMENT_PUBLISHER = "publisher";
    private static final String ELEMENT_CONTRIBUTOR = "contributor";
    private static final String ELEMENT_DATE = "date";
    private static final String ELEMENT_TYPE = "type";
    private static final String ELEMENT_FORMAT = "format";
    private static final String ELEMENT_IDENTIFIER = "identifier";
    private static final String ELEMENT_SOURCE = "source";
    private static final String ELEMENT_LANGUAGE = "language";
    private static final String ELEMENT_RELATION = "relation";
    private static final String ELEMENT_COVERAGE = "coverage";
    private static final String ELEMENT_RIGHTS = "rights";

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

    private static final String TERM_AUDIENCE = "audience";
    private static final String TERM_ALTERNATIVE = "alternative";
    private static final String TERM_TABLEOFCONTENTS = "tableOfContents";
    private static final String TERM_ABSTRACT = "abstract";
    private static final String TERM_CREATED = "created";
    private static final String TERM_VALID = "valid";
    private static final String TERM_AVAILABLE = "available";
    private static final String TERM_ISSUED = "issued";
    private static final String TERM_MODIFIED = "modified";
    private static final String TERM_EXTENT = "extent";
    private static final String TERM_MEDIUM = "medium";
    private static final String TERM_ISVERSIONOF = "isVersionOf";
    private static final String TERM_HASVERSION = "hasVersion";
    private static final String TERM_ISREPLACEDBY = "isReplacedBy";
    private static final String TERM_REPLACES = "replaces";
    private static final String TERM_ISREQUIREDBY = "isRequiredBy";
    private static final String TERM_REQUIRES = "requires";
    private static final String TERM_ISPARTOF = "isPartOf";
    private static final String TERM_HASPART = "hasPart";
    private static final String TERM_ISREFERENCEDBY = "isReferencedBy";
    private static final String TERM_REFERENCES = "references";
    private static final String TERM_ISFORMATOF = "isFormatOf";
    private static final String TERM_HASFORMAT = "hasFormat";
    private static final String TERM_CONFORMSTO = "conformsTo";
    private static final String TERM_SPATIAL = "spatial";
    private static final String TERM_TEMPORAL = "temporal";
    private static final String TERM_MEDIATOR = "mediator";
    private static final String TERM_DATEACCEPTED = "dateAccepted";
    private static final String TERM_DATECOPYRIGHTED = "dateCopyrighted";
    private static final String TERM_DATESUBMITTED = "dateSubmitted";
    private static final String TERM_EDUCATIONLEVEL = "educationLevel";
    private static final String TERM_ACCESSRIGHTS = "accessRights";
    private static final String TERM_BIBLIOGRAPHICCITATION = "bibliographicCitation";

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
                Element child = helper.getFirstChild(metaElement, elementNames[i]);
                if (child != null) {
                    String value = DocumentHelper.getSimpleElementText(child);
                    maps[type].put(elementNames[i], value);
                }
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
                Element element = helper.getFirstChild(metaElement, elementNames[i]);
                if (element != null) {
                    metaElement.removeChild(element);
                }
                String value = (String) maps[type].get(elementNames[i]);
                if (value != null && !"".equals(value.trim())) {
                    element = helper.createElement(elementNames[i], value);
                    metaElement.appendChild(element);
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
     * Get the creator
     * 
     * @return the creator
     * 
     * @throws DocumentException if an error occurs
     */
    public String getCreator() throws DocumentException {
        return (String) elements.get(ELEMENT_CREATOR);
    }

    /**
     * Set the DC creator
     * 
     * @param creator the Creator
     */
    public void setCreator(String creator) {
        elements.put(ELEMENT_CREATOR, creator);
    }

    /**
     * Get the title
     * 
     * @return the title
     * 
     * @throws DocumentException if an error occurs
     */
    public String getTitle() throws DocumentException {
        return (String) elements.get(ELEMENT_TITLE);
    }

    /**
     * Set the DC title
     * 
     * @param title the title
     */
    public void setTitle(String title) {
        elements.put(ELEMENT_TITLE, title);
    }

    /**
     * Get the description
     * 
     * @return the description
     * 
     * @throws DocumentException if an error occurs
     */
    public String getDescription() throws DocumentException {
        return (String) elements.get(ELEMENT_DESCRIPTION);
    }

    /**
     * Set the DC Description
     * 
     * @param description the description
     */
    public void setDescription(String description) {
        elements.put(ELEMENT_DESCRIPTION, description);
    }

    /**
     * Get the identifier
     * 
     * @return the identifier
     * 
     * @throws DocumentException if an error occurs
     */
    public String getIdentifier() throws DocumentException {
        return (String) elements.get(ELEMENT_IDENTIFIER);
    }

    /**
     * Set the DC Identifier
     * 
     * @param identifier the identifier
     */
    public void setIdentifier(String identifier) {
        elements.put(ELEMENT_IDENTIFIER, identifier);
    }

    /**
     * Get the subject.
     * 
     * @return the subject
     * 
     * @throws DocumentException if an error occurs
     */
    public String getSubject() throws DocumentException {
        return (String) elements.get(ELEMENT_SUBJECT);
    }

    /**
     * Set the DC Subject
     * 
     * @param subject the subject
     */
    public void setSubject(String subject) {
        elements.put(ELEMENT_SUBJECT, subject);
    }

    /**
     * Get the publisher
     * 
     * @return the publisher
     * 
     * @throws DocumentException if an error occurs
     */
    public String getPublisher() throws DocumentException {
        return (String) elements.get(ELEMENT_PUBLISHER);
    }

    /**
     * Set the publisher
     * 
     * @param publisher the publisher
     */
    public void setPublisher(String publisher) {
        elements.put(ELEMENT_PUBLISHER, publisher);
    }

    /**
     * Get the date of issue
     * 
     * @return the date of issue
     * 
     * @throws DocumentException if an error occurs
     */
    public String getDateIssued() throws DocumentException {
        return (String) terms.get(TERM_ISSUED);
    }

    /**
     * Set the date of issue
     * 
     * @param dateIssued the date of issue
     */
    public void setDateIssued(String dateIssued) {
        terms.put(TERM_ISSUED, dateIssued);
    }

    /**
     * Get the date of creation
     * 
     * @return the date of creation
     * 
     * @throws DocumentException if an error occurs
     */
    public String getDateCreated() throws DocumentException {
        return (String) terms.get(TERM_CREATED);
    }

    /**
     * Set the date of creation
     * 
     * @param dateCreated the date of creation
     */
    public void setDateCreated(String dateCreated) {
        terms.put(TERM_CREATED, dateCreated);
    }

    /**
     * Get the rights
     * 
     * @return the rights
     * 
     * @throws DocumentException if an error occurs
     */
    public String getRights() throws DocumentException {
        return (String) elements.get(ELEMENT_RIGHTS);
    }

    /**
     * Set the DC Rights
     * 
     * @param rights the rights
     */
    public void setRights(String rights) {
        elements.put(ELEMENT_RIGHTS, rights);
    }

    /**
     * Get isReferencedBy
     * 
     * @return isReferencedBy
     * 
     * @throws DocumentException if an error occurs
     */
    public String getIsReferencedBy() throws DocumentException {
        return (String) terms.get(TERM_ISREFERENCEDBY);
    }

    /**
     * Set isReferencedBy
     * 
     * @param isReferencedBy isReferencedBy
     */
    public void setIsReferencedBy(String isReferencedBy) {
        terms.put(TERM_ISREFERENCEDBY, isReferencedBy);
    }
}
