/*
$Id: DublinCore.java,v 1.13 2003/08/14 10:45:34 egli Exp $
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

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * A publication.
 *
 * @author <a href="mailto:gregor@apache.org">Gregor J. Rothfuss</a>
 */
public class DublinCore {
    private Document cmsdocument;
    private File infofile;
    private Date timestamp = new Date(0);

    private HashMap map = new HashMap();

    private static final String DC_NAMESPACE =
        "http://purl.org/dc/elements/1.1/";
    
    private static final String CREATOR = "creator";
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String IDENTIFIER = "identifier";
    private static final String SUBJECT = "subject";
    private static final String PUBLISHER = "publisher";
    private static final String DATEISSUED = "dateissued";
    private static final String DATEPUBLISHED = "datepublished";
    private static final String RIGHTS = "rights";

    private static final String[] fields =
        {
            CREATOR,
            TITLE,
            DESCRIPTION,
            IDENTIFIER,
            SUBJECT,
            PUBLISHER,
            DATEISSUED,
            DATEPUBLISHED,
            RIGHTS };

    /** 
     * Creates a new instance of Dublin Core
     * 
     * @param aDocument the document for which the Dublin Core instance is created.
     */
    protected DublinCore(Document aDocument) {
        this.cmsdocument = aDocument;
        this.infofile =
            cmsdocument.getPublication().getPathMapper().getFile(
                cmsdocument.getPublication(),
                Publication.AUTHORING_AREA,
                cmsdocument.getId(),
                cmsdocument.getLanguage());
    }

    /**
     * Check if the persistent dublin core data has been changed since the last access
     * If yes reload the data. 
     * 
     * @throws DocumentException if the persistent data could not be loaded.
     */
    private void checkValidity() throws DocumentException {
        Date modificationDate = new Date(infofile.lastModified());
        if (modificationDate.after(timestamp)) {
            timestamp = modificationDate;

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
            NodeList nodelist = null;
            String value = null;
            
            for (int i = 0; i < fields.length; i++) {
                    nodelist = doc.getElementsByTagNameNS(DC_NAMESPACE, fields[i]);
                    value = nodelist.item(0).getFirstChild().getNodeValue();
                    map.put(fields[i], value);
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
        NodeList nodelist = null;
        String value = null;

        for (int i = 0; i < fields.length; i++) {
            nodelist = doc.getElementsByTagNameNS(DC_NAMESPACE, fields[i]);
            nodelist.item(0).getFirstChild().setNodeValue(
                (String)map.get(fields[i]));
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
     * Get the creator
     * 
     * @return the creator
     * 
     * @throws DocumentException if an error occurs
     */
    public String getCreator() throws DocumentException {
        checkValidity();
        return (String)map.get(CREATOR);
    }

    /**
     * Set the DC creator
     * 
     * @param creator the Creator
     */
    public void setCreator(String creator) {
        map.put(CREATOR, creator);
    }

    /**
     * Get the title
     * 
     * @return the title
     * 
     * @throws DocumentException if an error occurs
     */
    public String getTitle() throws DocumentException {
        checkValidity();
        return (String)map.get(TITLE);
    }

    /**
     * Set the DC title
     * 
     * @param title the title
     */
    public void setTitle(String title) {
        map.put(TITLE, title);
    }

    /**
     * Get the description
     * 
     * @return the description
     * 
     * @throws DocumentException if an error occurs
     */
    public String getDescription() throws DocumentException {
        checkValidity();
        return (String)map.get(DESCRIPTION);
    }

    /**
     * Set the DC Description
     * 
     * @param description the description
     */
    public void setDescription(String description) {
        map.put(DESCRIPTION, description);
    }

    /**
     * Get the identifier
     * 
     * @return the identifier
     * 
     * @throws DocumentException if an error occurs
     */
    public String getIdentifier() throws DocumentException {
        checkValidity();
        return (String)map.get(IDENTIFIER);
    }

    /**
     * Set the DC Identifier
     * 
     * @param identifier the identifier
     */
    public void setIdentifier(String identifier) {
        map.put(IDENTIFIER, identifier);
    }

    /**
     * Get the subject.
     * 
     * @return the subject
     * 
     * @throws DocumentException if an error occurs
     */
    public String getSubject() throws DocumentException {
        checkValidity();
        return (String)map.get(SUBJECT);
    }

    /**
     * Set the DC Subject
     * 
     * @param subject the subject
     */
    public void setSubject(String subject) {
        map.put(SUBJECT, subject);
    }

    /**
     * Get the publisher
     * 
     * @return the publisher
     * 
     * @throws DocumentException if an error occurs
     */
    public String getPublisher() throws DocumentException {
        checkValidity();
        return (String)map.get(PUBLISHER);
    }

    /**
     * Set the publisher
     * 
     * @param publisher the publisher
     */
    public void setPublisher(String publisher) {
        map.put(PUBLISHER, publisher);
    }

    /**
     * Get the date of issue
     * 
     * @return the date of issue
     * 
     * @throws DocumentException if an error occurs
     */
    public String getDateIssued() throws DocumentException {
        checkValidity();
        return (String)map.get(DATEISSUED);
    }

    /**
     * Set the date of issue
     * 
     * @param dateIssued the date of issue
     */
    public void setDateIssued(String dateIssued) {
        map.put(DATEISSUED, dateIssued);
    }

    /**
     * Get the date of publication.
     * 
     * @return the date of publication
     * 
     * @throws DocumentException if an error occurs
     */
    public String getDatePublished() throws DocumentException {
        checkValidity();
        return (String)map.get(DATEPUBLISHED);
    }

    /**
     * Set the publication date
     * 
     * @param datePublished the date of publication
     */
    public void setDatePublished(String datePublished) {
        map.put(DATEPUBLISHED, datePublished);
    }

    /**
     * Get the rights
     * 
     * @return the rights
     * 
     * @throws DocumentException if an error occurs
     */
    public String getRights() throws DocumentException {
        checkValidity();
        return (String)map.get(RIGHTS);
    }

    /**
     * Set the DC Rights
     * 
     * @param rights the rights
     */
    public void setRights(String rights) {
        map.put(RIGHTS, rights);
    }

}