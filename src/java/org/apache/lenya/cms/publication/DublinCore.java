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

/**
 * @author Christian Egli
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann</a>
 * @version $Id: DublinCore.java,v 1.23 2004/02/17 14:03:52 egli Exp $
 */
public interface DublinCore {

    String ELEMENT_TITLE = "title";
    String ELEMENT_CREATOR = "creator";
    String ELEMENT_SUBJECT = "subject";
    String ELEMENT_DESCRIPTION = "description";
    String ELEMENT_PUBLISHER = "publisher";
    String ELEMENT_CONTRIBUTOR = "contributor";
    String ELEMENT_DATE = "date";
    String ELEMENT_TYPE = "type";
    String ELEMENT_FORMAT = "format";
    String ELEMENT_IDENTIFIER = "identifier";
    String ELEMENT_SOURCE = "source";
    String ELEMENT_LANGUAGE = "language";
    String ELEMENT_RELATION = "relation";
    String ELEMENT_COVERAGE = "coverage";
    String ELEMENT_RIGHTS = "rights";

    String TERM_AUDIENCE = "audience";
    String TERM_ALTERNATIVE = "alternative";
    String TERM_TABLEOFCONTENTS = "tableOfContents";
    String TERM_ABSTRACT = "abstract";
    String TERM_CREATED = "created";
    String TERM_VALID = "valid";
    String TERM_AVAILABLE = "available";
    String TERM_ISSUED = "issued";
    String TERM_MODIFIED = "modified";
    String TERM_EXTENT = "extent";
    String TERM_MEDIUM = "medium";
    String TERM_ISVERSIONOF = "isVersionOf";
    String TERM_HASVERSION = "hasVersion";
    String TERM_ISREPLACEDBY = "isReplacedBy";
    String TERM_REPLACES = "replaces";
    String TERM_ISREQUIREDBY = "isRequiredBy";
    String TERM_REQUIRES = "requires";
    String TERM_ISPARTOF = "isPartOf";
    String TERM_HASPART = "hasPart";
    String TERM_ISREFERENCEDBY = "isReferencedBy";
    String TERM_REFERENCES = "references";
    String TERM_ISFORMATOF = "isFormatOf";
    String TERM_HASFORMAT = "hasFormat";
    String TERM_CONFORMSTO = "conformsTo";
    String TERM_SPATIAL = "spatial";
    String TERM_TEMPORAL = "temporal";
    String TERM_MEDIATOR = "mediator";
    String TERM_DATEACCEPTED = "dateAccepted";
    String TERM_DATECOPYRIGHTED = "dateCopyrighted";
    String TERM_DATESUBMITTED = "dateSubmitted";
    String TERM_EDUCATIONLEVEL = "educationLevel";
    String TERM_ACCESSRIGHTS = "accessRights";
    String TERM_BIBLIOGRAPHICCITATION = "bibliographicCitation";

    /**
     * Save the meta data.
     *
     * @throws DocumentException if the meta data could not be made persistent.
     */
    void save() throws DocumentException;

    /**
     * Get the creator
     * 
     * @return the creator
     * 
     * @throws DocumentException if an error occurs
     */
    String getCreator() throws DocumentException;

    /**
     * Set the DC creator
     * 
     * @param creator the Creator
     * 
     * @throws DocumentException if an error occurs
     */
    void setCreator(String creator) throws DocumentException;

    /**
     * Get the title
     * 
     * @return the title
     * 
     * @throws DocumentException if an error occurs
     */
    String getTitle() throws DocumentException;

    /**
     * Set the DC title
     * 
     * @param title the title
     * 
     * @throws DocumentException if an error occurs
     */
    void setTitle(String title) throws DocumentException;

    /**
     * Get the description
     * 
     * @return the description
     * 
     * @throws DocumentException if an error occurs
     */
    String getDescription() throws DocumentException;

    /**
     * Set the DC Description
     * 
     * @param description the description
     * 
     * @throws DocumentException if an error occurs
     */
    void setDescription(String description) throws DocumentException;

    /**
     * Get the identifier
     * 
     * @return the identifier
     * 
     * @throws DocumentException if an error occurs
     */
    String getIdentifier() throws DocumentException;

    /**
     * Set the DC Identifier
     * 
     * @param identifier the identifier
     * 
     * @throws DocumentException if an error occurs
     */
    void setIdentifier(String identifier) throws DocumentException;

    /**
     * Get the subject.
     * 
     * @return the subject
     * 
     * @throws DocumentException if an error occurs
     */
    String getSubject() throws DocumentException;

    /**
     * Set the DC Subject
     * 
     * @param subject the subject
     * 
     * @throws DocumentException if an error occurs
     */
    void setSubject(String subject) throws DocumentException;

    /**
     * Get the publisher
     * 
     * @return the publisher
     * 
     * @throws DocumentException if an error occurs
     */
    String getPublisher() throws DocumentException;

    /**
     * Set the publisher
     * 
     * @param publisher the publisher
     * 
     * @throws DocumentException if an error occurs
     */
    void setPublisher(String publisher) throws DocumentException;

    /**
     * Get the date of issue
     * 
     * @return the date of issue
     * 
     * @throws DocumentException if an error occurs
     */
    String getDateIssued() throws DocumentException;

    /**
     * Set the date of issue
     * 
     * @param dateIssued the date of issue
     * 
     * @throws DocumentException if an error occurs
     */
    void setDateIssued(String dateIssued) throws DocumentException;

    /**
     * Get the date of creation
     * 
     * @return the date of creation
     * 
     * @throws DocumentException if an error occurs
     */
    String getDateCreated() throws DocumentException;

    /**
     * Set the date of creation
     * 
     * @param dateCreated the date of creation
     * 
     * @throws DocumentException if an error occurs
     */
    void setDateCreated(String dateCreated) throws DocumentException;

    /**
     * Get the rights
     * 
     * @return the rights
     * 
     * @throws DocumentException if an error occurs
     */
    String getRights() throws DocumentException;

    /**
     * Set the DC Rights
     * 
     * @param rights the rights
     * 
     * @throws DocumentException if an error occurs
     */
    void setRights(String rights) throws DocumentException;

    /**
     * Get isReferencedBy
     * 
     * @return isReferencedBy
     * 
     * @throws DocumentException if an error occurs
     */
    String getIsReferencedBy() throws DocumentException;

    /**
     * Set isReferencedBy
     * 
     * @param isReferencedBy isReferencedBy
     * 
     * @throws DocumentException if an error occurs
     */
    void setIsReferencedBy(String isReferencedBy) throws DocumentException;

    /**
     * Returns the values for a certain key.
     * @param key The key.
     * @return An array of strings.
     * @throws DocumentException when something went wrong.
     */
    String[] getValues(String key) throws DocumentException;

    /**
     * Returns the first value for a certain key.
     * @param key The key.
     * @return A string.
     */
    String getFirstValue(String key) throws DocumentException;

    /**
     * Adds a value for a certain key.
     * @param key The key.
     * @param value The value to add.
     * @throws DocumentException when something went wrong.
     */
    void addValue(String key, String value) throws DocumentException;

	/**
	 * Add all values for a certain key.
	 * 
	 * @param key The key
	 * @param values The value to add
	 * @throws DocumentException if something went wrong
	 */
	void addValues(String key, String[] values) throws DocumentException;

    /**
     * Removes a specific value for a certain key.
     * @param key The key.
     * @param value The value to remove.
     * @throws DocumentException when something went wrong.
     */
    void removeValue(String key, String value) throws DocumentException;

    /**
     * Removes all values for a certain key.
     * @param key The key.
     * @throws DocumentException when something went wrong.
     */
    void removeAllValues(String key) throws DocumentException;
    
	/**
	 * Replace the contents of the current dublin core by the 
	 * contents of other.
	 * @param other
	 */
    void replaceBy(DublinCore other) throws DocumentException;

}
