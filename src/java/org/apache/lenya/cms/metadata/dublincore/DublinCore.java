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

package org.apache.lenya.cms.metadata.dublincore;

import org.apache.lenya.cms.publication.DocumentException;

/**
 * <p>
 * Dublin core metadata interface.
 * </p>
 * <p>
 * The descriptions are citing the <a href="http://www.dublincore.org">Dublin Core website </a>.
 * </p>
 * 
 * @version $Id$
 */
public interface DublinCore {

    /**
     * A name given to the resource. Typically, Title will be a name by which the resource is
     * formally known.
     */
    String ELEMENT_TITLE = "title";

    /**
     * An entity primarily responsible for making the content of the resource. Examples of Creator
     * include a person, an organization, or a service. Typically, the name of a Creator should be
     * used to indicate the entity.
     */
    String ELEMENT_CREATOR = "creator";

    /**
     * A topic of the content of the resource. Typically, Subject will be expressed as keywords, key
     * phrases or classification codes that describe a topic of the resource. Recommended best
     * practice is to select a value from a controlled vocabulary or formal classification scheme.
     */
    String ELEMENT_SUBJECT = "subject";

    /**
     * An account of the content of the resource. Examples of Description include, but is not
     * limited to: an abstract, table of contents, reference to a graphical representation of
     * content or a free-text account of the content.
     */
    String ELEMENT_DESCRIPTION = "description";

    /**
     * An entity responsible for making the resource available. Examples of Publisher include a
     * person, an organization, or a service. Typically, the name of a Publisher should be used to
     * indicate the entity.
     */
    String ELEMENT_PUBLISHER = "publisher";

    /**
     * An entity responsible for making contributions to the content of the resource. Examples of
     * Contributor include a person, an organization, or a service. Typically, the name of a
     * Contributor should be used to indicate the entity.
     */
    String ELEMENT_CONTRIBUTOR = "contributor";

    /**
     * A date of an event in the lifecycle of the resource. Typically, Date will be associated with
     * the creation or availability of the resource. Recommended best practice for encoding the date
     * value is defined in a profile of ISO 8601 [W3CDTF] and includes (among others) dates of the
     * form YYYY-MM-DD.
     */
    String ELEMENT_DATE = "date";

    /**
     * The nature or genre of the content of the resource. Type includes terms describing general
     * categories, functions, genres, or aggregation levels for content. Recommended best practice
     * is to select a value from a controlled vocabulary (for example, the DCMI Type Vocabulary
     * [DCT1]). To describe the physical or digital manifestation of the resource, use the FORMAT
     * element.
     */
    String ELEMENT_TYPE = "type";

    /**
     * The physical or digital manifestation of the resource. Typically, Format may include the
     * media-type or dimensions of the resource. Format may be used to identify the software,
     * hardware, or other equipment needed to display or operate the resource. Examples of
     * dimensions include size and duration. Recommended best practice is to select a value from a
     * controlled vocabulary (for example, the list of Internet Media Types [MIME] defining computer
     * media formats).
     */
    String ELEMENT_FORMAT = "format";

    /**
     * An unambiguous reference to the resource within a given context. Recommended best practice is
     * to identify the resource by means of a string or number conforming to a formal identification
     * system. Formal identification systems include but are not limited to the Uniform Resource
     * Identifier (URI) (including the Uniform Resource Locator (URL)), the Digital Object
     * Identifier (DOI) and the International Standard Book Number (ISBN).
     */
    String ELEMENT_IDENTIFIER = "identifier";

    /**
     * A Reference to a resource from which the present resource is derived. The present resource
     * may be derived from the Source resource in whole or in part. Recommended best practice is to
     * identify the referenced resource by means of a string or number conforming to a formal
     * identification system.
     */
    String ELEMENT_SOURCE = "source";

    /**
     * A language of the intellectual content of the resource. Recommended best practice is to use
     * RFC 3066 [RFC3066] which, in conjunction with ISO639 [ISO639]), defines two- and three-letter
     * primary language tags with optional subtags. Examples include "en" or "eng" for English,
     * "akk" for Akkadian", and "en-GB" for English used in the United Kingdom.
     */
    String ELEMENT_LANGUAGE = "language";

    /**
     * A reference to a related resource. Recommended best practice is to identify the referenced
     * resource by means of a string or number conforming to a formal identification system.
     */
    String ELEMENT_RELATION = "relation";

    /**
     * The extent or scope of the content of the resource. Typically, Coverage will include spatial
     * location (a place name or geographic coordinates), temporal period (a period label, date, or
     * date range) or jurisdiction (such as a named administrative entity). Recommended best
     * practice is to select a value from a controlled vocabulary (for example, the Thesaurus of
     * Geographic Names [TGN]) and to use, where appropriate, named places or time periods in
     * preference to numeric identifiers such as sets of coordinates or date ranges.
     */
    String ELEMENT_COVERAGE = "coverage";

    /**
     * Information about rights held in and over the resource. Typically, Rights will contain a
     * rights management statement for the resource, or reference a service providing such
     * information. Rights information often encompasses Intellectual Property Rights (IPR),
     * Copyright, and various Property Rights. If the Rights element is absent, no assumptions may
     * be made about any rights held in or over the resource.
     */
    String ELEMENT_RIGHTS = "rights";

    /**
     * A summary of the content of the resource.
     */
    String TERM_ABSTRACT = "abstract";

    /**
     * Information about who can access the resource or an indication of its security status. Access
     * Rights may include information regarding access or restrictions based on privacy, security or
     * other regulations.
     */
    String TERM_ACCESSRIGHTS = "accessRights";

    /**
     * Any form of the title used as a substitute or alternative to the formal title of the
     * resource. This qualifier can include Title abbreviations as well as translations.
     */
    String TERM_ALTERNATIVE = "alternative";

    /**
     * A class of entity for whom the resource is intended or useful. A class of entity may be
     * determined by the creator or the publisher or by a third party.
     */
    String TERM_AUDIENCE = "audience";

    /**
     * Date (often a range) that the resource will become or did become available.
     */
    String TERM_AVAILABLE = "available";

    /**
     * A bibliographic reference for the resource. Recommended practice is to include sufficient
     * bibliographic detail to identify the resource as unambiguously as possible, whether or not
     * the citation is in a standard form.
     */
    String TERM_BIBLIOGRAPHICCITATION = "bibliographicCitation";

    /**
     * A reference to an established standard to which the resource conforms.
     */
    String TERM_CONFORMSTO = "conformsTo";

    /**
     * Date of creation of the resource.
     */
    String TERM_CREATED = "created";

    /**
     * Date of acceptance of the resource (e.g. of thesis by university department, of article by
     * journal, etc.).
     */
    String TERM_DATEACCEPTED = "dateAccepted";

    /**
     * Date of a statement of copyright.
     */
    String TERM_DATECOPYRIGHTED = "dateCopyrighted";

    /**
     * Date of submission of the resource (e.g. thesis, articles, etc.).
     */
    String TERM_DATESUBMITTED = "dateSubmitted";

    /**
     * A general statement describing the education or training context. Alternatively, a more
     * specific statement of the location of the audience in terms of its progression through an
     * education or training context.
     */
    String TERM_EDUCATIONLEVEL = "educationLevel";

    /**
     * The size or duration of the resource.
     */
    String TERM_EXTENT = "extent";

    /**
     * The described resource pre-existed the referenced resource, which is essentially the same
     * intellectual content presented in another format.
     */
    String TERM_HASFORMAT = "hasFormat";

    /**
     * The described resource includes the referenced resource either physically or logically.
     */
    String TERM_HASPART = "hasPart";

    /**
     * The described resource has a version, edition, or adaptation, namely, the referenced
     * resource.
     */
    String TERM_HASVERSION = "hasVersion";

    /**
     * The described resource is the same intellectual content of the referenced resource, but
     * presented in another format.
     */
    String TERM_ISFORMATOF = "isFormatOf";

    /**
     * The described resource is a physical or logical part of the referenced resource.
     */
    String TERM_ISPARTOF = "isPartOf";

    /**
     * The described resource is referenced, cited, or otherwise pointed to by the referenced
     * resource.
     */
    String TERM_ISREFERENCEDBY = "isReferencedBy";

    /**
     * The described resource is supplanted, displaced, or superseded by the referenced resource.
     */
    String TERM_ISREPLACEDBY = "isReplacedBy";

    /**
     * The described resource is required by the referenced resource, either physically or
     * logically.
     */
    String TERM_ISREQUIREDBY = "isRequiredBy";

    /**
     * Date of formal issuance (e.g., publication) of the resource.
     */
    String TERM_ISSUED = "issued";

    /**
     * The described resource is a version, edition, or adaptation of the referenced resource.
     * Changes in version imply substantive changes in content rather than differences in format.
     */
    String TERM_ISVERSIONOF = "isVersionOf";

    /**
     * A legal document giving official permission to do something with the resource. Recommended
     * best practice is to identify the license using a URI. Examples of such licenses can be found
     * at http://creativecommons.org/licenses/.
     */
    String TERM_LICENSE = "license";

    /**
     * A class of entity that mediates access to the resource and for whom the resource is intended
     * or useful. The audiences for a resource are of two basic classes: (1) an ultimate beneficiary
     * of the resource, and (2) frequently, an entity that mediates access to the resource. The
     * mediator element refinement represents the second of these two classes.
     */
    String TERM_MEDIATOR = "mediator";

    /**
     * The material or physical carrier of the resource.
     */
    String TERM_MEDIUM = "medium";

    /**
     * Date on which the resource was changed.
     */
    String TERM_MODIFIED = "modified";

    /**
     * The described resource references, cites, or otherwise points to the referenced resource.
     */
    String TERM_REFERENCES = "references";

    /**
     * The described resource supplants, displaces, or supersedes the referenced resource.
     */
    String TERM_REPLACES = "replaces";

    /**
     * The described resource requires the referenced resource to support its function, delivery, or
     * coherence of content.
     */
    String TERM_REQUIRES = "requires";

    /**
     * A person or organization owning or managing rights over the resource. Recommended best
     * practice is to use the URI or name of the Rights Holder to indicate the entity.
     */
    String TERM_RIGHTSHOLDER = "rightsHolder";

    /**
     * Spatial characteristics of the intellectual content of the resource.
     */
    String TERM_SPATIAL = "spatial";

    /**
     * A list of subunits of the content of the resource.
     */
    String TERM_TABLEOFCONTENTS = "tableOfContents";

    /**
     * Temporal characteristics of the intellectual content of the resource.
     */
    String TERM_TEMPORAL = "temporal";

    /**
     * Date (often a range) of validity of a resource.
     */
    String TERM_VALID = "valid";

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
     * Replace the contents of the current dublin core by the contents of other.
     * @param other The other dublin core object.
     * @throws DocumentException if an error occurs.
     */
    void replaceBy(DublinCore other) throws DocumentException;

}