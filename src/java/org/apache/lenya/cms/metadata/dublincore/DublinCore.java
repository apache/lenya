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

/* $Id$  */

package org.apache.lenya.cms.metadata.dublincore;

import org.apache.lenya.cms.publication.DocumentException;

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
     * Returns the values for a certain key.
     * @param key The key.
     * @return An array of strings.
     * @throws DocumentException when something went wrong.
     */
    String[] getValues(String key) throws DocumentException;

    /**
     * Returns the first value for a certain key.
     * @param key The key.
     * @return A string or <code>null</code> if no value is set for this key.
     * @throws DocumentException if an error occurs.
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
	 * @param other The other dublin core object.
	 * @throws DocumentException if an error occurs.
	 */
    void replaceBy(DublinCore other) throws DocumentException;

}
