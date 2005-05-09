/*
 * Copyright  1999-2005 The Apache Software Foundation
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.logger.Logger;
import org.apache.lenya.cms.metadata.MetaDataImpl;
import org.apache.lenya.cms.publication.DocumentException;

/**
 * Access dublin core meta data in documents. This class uses the dublin core specification from
 * 2003-03-04.
 * @version $Id$
 */
public class DublinCoreImpl extends MetaDataImpl {

    private static final String LOCAL_META = "dc";

    private static final String DC_NAMESPACE = "http://purl.org/dc/elements/1.1/";
    private static final String DC_PREFIX = "dc";

    /**
     * The dublin core elements.
     */
    static final String[] ELEMENTS = { 
            DublinCore.ELEMENT_TITLE, 
            DublinCore.ELEMENT_CREATOR,
            DublinCore.ELEMENT_SUBJECT, 
            DublinCore.ELEMENT_DESCRIPTION,
            DublinCore.ELEMENT_PUBLISHER, 
            DublinCore.ELEMENT_CONTRIBUTOR, 
            DublinCore.ELEMENT_DATE,
            DublinCore.ELEMENT_TYPE, 
            DublinCore.ELEMENT_FORMAT, 
            DublinCore.ELEMENT_IDENTIFIER,
            DublinCore.ELEMENT_SOURCE, 
            DublinCore.ELEMENT_LANGUAGE, 
            DublinCore.ELEMENT_RELATION,
            DublinCore.ELEMENT_COVERAGE, 
            DublinCore.ELEMENT_RIGHTS };

    private static final String DCTERMS_NAMESPACE = "http://purl.org/dc/terms/";
    private static final String DCTERMS_PREFIX = "dcterms";

    /**
     * The dublin core terms.
     */
    private static final String[] TERMS = { 
            DublinCore.TERM_AUDIENCE, 
            DublinCore.TERM_ALTERNATIVE,
            DublinCore.TERM_TABLEOFCONTENTS, 
            DublinCore.TERM_ABSTRACT, 
            DublinCore.TERM_CREATED,
            DublinCore.TERM_VALID, 
            DublinCore.TERM_EXTENT, 
            DublinCore.TERM_AVAILABLE,
            DublinCore.TERM_ISSUED, 
            DublinCore.TERM_MODIFIED, 
            DublinCore.TERM_EXTENT,
            DublinCore.TERM_LICENSE, 
            DublinCore.TERM_MEDIUM, 
            DublinCore.TERM_ISVERSIONOF,
            DublinCore.TERM_HASVERSION, 
            DublinCore.TERM_ISREPLACEDBY, 
            DublinCore.TERM_REPLACES,
            DublinCore.TERM_ISREQUIREDBY, 
            DublinCore.TERM_REQUIRES, 
            DublinCore.TERM_ISPARTOF,
            DublinCore.TERM_HASPART, 
            DublinCore.TERM_ISREFERENCEDBY, 
            DublinCore.TERM_REFERENCES,
            DublinCore.TERM_RIGHTSHOLDER, 
            DublinCore.TERM_ISFORMATOF, 
            DublinCore.TERM_HASFORMAT,
            DublinCore.TERM_CONFORMSTO, 
            DublinCore.TERM_SPATIAL, 
            DublinCore.TERM_TEMPORAL,
            DublinCore.TERM_MEDIATOR, 
            DublinCore.TERM_DATEACCEPTED,
            DublinCore.TERM_DATECOPYRIGHTED, 
            DublinCore.TERM_DATESUBMITTED,
            DublinCore.TERM_EDUCATIONLEVEL, 
            DublinCore.TERM_ACCESSRIGHTS,
            DublinCore.TERM_BIBLIOGRAPHICCITATION };

    /**
     * @see org.apache.lenya.cms.metadata.MetaDataImpl#MetaDataImpl(java.lang.String, org.apache.avalon.framework.service.ServiceManager, org.apache.avalon.framework.logger.Logger)
     */
    public DublinCoreImpl(String sourceUri, ServiceManager manager, Logger _logger) throws DocumentException {
        super(sourceUri, manager, _logger);
    }

    /**
     * @see org.apache.lenya.cms.metadata.MetaDataImpl#getNamespaces()
     */
    protected String[] getNamespaces() {
        return new String[] { DC_NAMESPACE, DCTERMS_NAMESPACE };
    }

    /**
     * @see org.apache.lenya.cms.metadata.MetaDataImpl#getPrefixes()
     */
    protected String[] getPrefixes() {
        return new String[] { DC_PREFIX, DCTERMS_PREFIX };
    }

    /**
     * @see org.apache.lenya.cms.metadata.MetaDataImpl#getElements()
     */
    protected String[] getElements() {
        return ELEMENTS;
    }

    /**
     * @see org.apache.lenya.cms.metadata.MetaDataImpl#getTerms()
     */
    protected String[] getTerms() {
        return TERMS;
    }

    /**
     * @see org.apache.lenya.cms.metadata.MetaDataImpl#getLocalElementName()
     */
    protected String getLocalElementName() {
        return LOCAL_META;
    }

    /**
     * Returns a list of all allowed attribute names in this
     * metadata.
     * @return the list of attribute names
     */
    public static List getAttributeNames() {
        List names = new ArrayList();
        names.addAll(Arrays.asList(ELEMENTS));
        names.addAll(Arrays.asList(TERMS));
        return names;
    }
}
