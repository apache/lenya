/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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
package org.apache.lenya.cms.repo.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lenya.cms.metadata.dublincore.DublinCore;
import org.apache.lenya.cms.repo.metadata.Element;
import org.apache.lenya.cms.repo.metadata.impl.ElementImpl;

public class DublinCoreElements {

    public static String ELEMENT_SET = DublinCoreElements.class.getName();

    /**
     * The dublin core elements.
     */
    private static String[] ELEMENTS = { DublinCore.ELEMENT_TITLE, DublinCore.ELEMENT_CREATOR,
            DublinCore.ELEMENT_SUBJECT, DublinCore.ELEMENT_DESCRIPTION,
            DublinCore.ELEMENT_PUBLISHER, DublinCore.ELEMENT_CONTRIBUTOR, DublinCore.ELEMENT_DATE,
            DublinCore.ELEMENT_TYPE, DublinCore.ELEMENT_FORMAT, DublinCore.ELEMENT_IDENTIFIER,
            DublinCore.ELEMENT_SOURCE, DublinCore.ELEMENT_LANGUAGE, DublinCore.ELEMENT_RELATION,
            DublinCore.ELEMENT_COVERAGE, DublinCore.ELEMENT_RIGHTS };

    /**
     * The dublin core terms.
     */
    private static String[] TERMS = { DublinCore.TERM_AUDIENCE, DublinCore.TERM_ALTERNATIVE,
            DublinCore.TERM_TABLEOFCONTENTS, DublinCore.TERM_ABSTRACT, DublinCore.TERM_CREATED,
            DublinCore.TERM_VALID, DublinCore.TERM_EXTENT, DublinCore.TERM_AVAILABLE,
            DublinCore.TERM_ISSUED, DublinCore.TERM_MODIFIED, DublinCore.TERM_LICENSE,
            DublinCore.TERM_MEDIUM, DublinCore.TERM_ISVERSIONOF, DublinCore.TERM_HASVERSION,
            DublinCore.TERM_ISREPLACEDBY, DublinCore.TERM_REPLACES, DublinCore.TERM_ISREQUIREDBY,
            DublinCore.TERM_REQUIRES, DublinCore.TERM_ISPARTOF, DublinCore.TERM_HASPART,
            DublinCore.TERM_ISREFERENCEDBY, DublinCore.TERM_REFERENCES,
            DublinCore.TERM_RIGHTSHOLDER, DublinCore.TERM_ISFORMATOF, DublinCore.TERM_HASFORMAT,
            DublinCore.TERM_CONFORMSTO, DublinCore.TERM_SPATIAL, DublinCore.TERM_TEMPORAL,
            DublinCore.TERM_MEDIATOR, DublinCore.TERM_DATEACCEPTED,
            DublinCore.TERM_DATECOPYRIGHTED, DublinCore.TERM_DATESUBMITTED,
            DublinCore.TERM_EDUCATIONLEVEL, DublinCore.TERM_ACCESSRIGHTS,
            DublinCore.TERM_BIBLIOGRAPHICCITATION };

    /**
     * Returns a list of all allowed attribute names in this metadata.
     * @return the list of attribute names
     */
    public static Element[] getElements() {
        List names = new ArrayList();
        names.addAll(Arrays.asList(ELEMENTS));
        names.addAll(Arrays.asList(TERMS));
        Element[] elements = new Element[names.size()];
        for (int i = 0; i < elements.length; i++) {
            elements[i] = new ElementImpl((String) names.get(i), true);
        }
        return elements;
    }
}
