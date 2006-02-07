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
package org.apache.lenya.cms.url;

import org.apache.lenya.cms.repo.RepositoryException;
import org.apache.lenya.cms.repo.metadata.Element;
import org.apache.lenya.cms.repo.metadata.ElementSet;
import org.apache.lenya.cms.repo.metadata.impl.ElementImpl;

/**
 * Element set to store the URL mapper attribute.
 */
public class URLMapperElements implements ElementSet {
    
    /**
     * The URL mapper element set name.
     */
    public static final String ELEMENT_SET = URLMapperElements.class.getName();

    /**
     * The URL mapper attribute.
     */
    public static final String URL_MAPPER = "urlMapper";

    protected static final Element[] ELEMENTS = { new ElementImpl(URL_MAPPER, false) };

    public Element[] getElements() {
        return ELEMENTS;
    }

    public Element getElement(String name) throws RepositoryException {
        if (!name.equals(URL_MAPPER)) {
            throw new RepositoryException("The name [" + name + "] is not contained!");
        }
        return ELEMENTS[0];
    }

}
