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

/* $Id: DublinCore.java,v 1.25 2004/03/01 16:18:17 gregor Exp $  */

package org.apache.lenya.cms.publication;

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
     * @deprecated Use {@link #getValues(java.lang.String)} or {@link #getFirstValue(java.lang.String)} instead.
     */
    String getCreator() throws DocumentException;

    /**
     * Set the DC creator
     * 
     * @param creator the Creator
     * 
     * @throws DocumentException if an error occurs
     * @deprecated Use {@link #addValue(java.lang.String, java.lang.String)} or {@link #setValue(java.lang.String, java.lang.String)} instead.
     */
    void setCreator(String creator) throws DocumentException;

    /**
     * Get the title
     * 
     * @return the title
     * 
     * @throws DocumentException if an error occurs
     * @deprecated Use {@link #getValues(java.lang.String)} or {@link #getFirstValue(java.lang.String)} instead.
     */
    String getTitle() throws DocumentException;

    /**
     * Set the DC title
     * 
     * @param title the title
     * 
     * @throws DocumentException if an error occurs
     * @deprecated Use {@link #addValue(java.lang.String, java.lang.String)} or {@link #setValue(java.lang.String, java.lang.String)} instead.
     */
    void setTitle(String title) throws DocumentException;

    /**
     * Get the description
     * 
     * @return the description
     * 
     * @throws DocumentException if an error occurs
     * @deprecated Use {@link #getValues(java.lang.String)} or {@link #getFirstValue(java.lang.String)} instead.
     */
    String getDescription() throws DocumentException;

    /**
     * Set the DC Description
     * 
     * @param description the description
     * 
     * @throws DocumentException if an error occurs
     * @deprecated Use {@link #addValue(java.lang.String, java.lang.String)} or {@link #setValue(java.lang.String, java.lang.String)} instead.
     */
    void setDescription(String description) throws DocumentException;

    /**
     * Get the identifier
     * 
     * @return the identifier
     * 
     * @throws DocumentException if an error occurs
     * @deprecated Use {@link #getValues(java.lang.String)} or {@link #getFirstValue(java.lang.String)} instead.
     */
    String getIdentifier() throws DocumentException;

    /**
     * Set the DC Identifier
     * 
     * @param identifier the identifier
     * 
     * @throws DocumentException if an error occurs
     * @deprecated Use {@link #addValue(java.lang.String, java.lang.String)} or {@link #setValue(java.lang.String, java.lang.String)} instead.
     */
    void setIdentifier(String identifier) throws DocumentException;

    /**
     * Get the subject.
     * 
     * @return the subject
     * 
     * @throws DocumentException if an error occurs
     * @deprecated Use {@link #getValues(java.lang.String)} or {@link #getFirstValue(java.lang.String)} instead.
     */
    String getSubject() throws DocumentException;

    /**
     * Set the DC Subject
     * 
     * @param subject the subject
     * 
     * @throws DocumentException if an error occurs
     * @deprecated Use {@link #addValue(java.lang.String, java.lang.String)} or {@link #setValue(java.lang.String, java.lang.String)} instead.
     */
    void setSubject(String subject) throws DocumentException;

    /**
     * Get the publisher
     * 
     * @return the publisher
     * 
     * @throws DocumentException if an error occurs
     * @deprecated Use {@link #getValues(java.lang.String)} or {@link #getFirstValue(java.lang.String)} instead.
     */
    String getPublisher() throws DocumentException;

    /**
     * Set the publisher
     * 
     * @param publisher the publisher
     * 
     * @throws DocumentException if an error occurs
     * @deprecated Use {@link #addValue(java.lang.String, java.lang.String)} or {@link #setValue(java.lang.String, java.lang.String)} instead.
     */
    void setPublisher(String publisher) throws DocumentException;

    /**
     * Get the date of issue
     * 
     * @return the date of issue
     * 
     * @throws DocumentException if an error occurs
     * @deprecated Use {@link #getValues(java.lang.String)} or {@link #getFirstValue(java.lang.String)} instead.
     */
    String getDateIssued() throws DocumentException;

    /**
     * Set the date of issue
     * 
     * @param dateIssued the date of issue
     * 
     * @throws DocumentException if an error occurs
     * @deprecated Use {@link #addValue(java.lang.String, java.lang.String)} or {@link #setValue(java.lang.String, java.lang.String)} instead.
     */
    void setDateIssued(String dateIssued) throws DocumentException;

    /**
     * Get the date of creation
     * 
     * @return the date of creation
     * 
     * @throws DocumentException if an error occurs
     * @deprecated Use {@link #getValues(java.lang.String)} or {@link #getFirstValue(java.lang.String)} instead.
     */
    String getDateCreated() throws DocumentException;

    /**
     * Set the date of creation
     * 
     * @param dateCreated the date of creation
     * 
     * @throws DocumentException if an error occurs
     * @deprecated Use {@link #addValue(java.lang.String, java.lang.String)} or {@link #setValue(java.lang.String, java.lang.String)} instead.
     */
    void setDateCreated(String dateCreated) throws DocumentException;

    /**
     * Get the rights
     * 
     * @return the rights
     * 
     * @throws DocumentException if an error occurs
     * @deprecated Use {@link #getValues(java.lang.String)} or {@link #getFirstValue(java.lang.String)} instead.
     */
    String getRights() throws DocumentException;

    /**
     * Set the DC Rights
     * 
     * @param rights the rights
     * 
     * @throws DocumentException if an error occurs
     * @deprecated Use {@link #addValue(java.lang.String, java.lang.String)} or {@link #setValue(java.lang.String, java.lang.String)} instead.
     */
    void setRights(String rights) throws DocumentException;

    /**
     * Get isReferencedBy
     * 
     * @return isReferencedBy
     * 
     * @throws DocumentException if an error occurs
     * @deprecated Use {@link #getValues(java.lang.String)} or {@link #getFirstValue(java.lang.String)} instead.
     */
    String getIsReferencedBy() throws DocumentException;

    /**
     * Set isReferencedBy
     * 
     * @param isReferencedBy isReferencedBy
     * 
     * @throws DocumentException if an error occurs
     * @deprecated Use {@link #addValue(java.lang.String, java.lang.String)} or {@link #setValue(java.lang.String, java.lang.String)} instead.
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
     * Sets the value for a certain key. All existing values will be removed.
     * @param key The key.
     * @param value The value to set.
     * @throws DocumentException when something went wrong.
     */
    void setValue(String key, String value) throws DocumentException;

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
