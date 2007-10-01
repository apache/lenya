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
package org.apache.lenya.ac.saml;

import java.util.Map;

/**
 * The attribute translator translates user attributes into a simpler form. The multi-value
 * attributes can either be translated into comma-separated values, or into string arrays.
 */
public interface AttributeTranslator {

    /**
     * The service role.
     */
    String ROLE = AttributeTranslator.class.getName();

    /**
     * This is the same as <code>translateAttributes(map, true)</code>.
     * @param attributes The original attributes, the values are {@link org.opensaml.SAMLAttribute}
     *        objects.
     * @return AttributesMap containing all values of a given key as a comma-separated String.
     */
    Map translateAttributes(Map attributes);

    /**
     * @param attributes The original attributes, the values are {@link org.opensaml.SAMLAttribute}
     *        objects.
     * @param joinValues If the values should be joined to a comma-separated string.
     * @return AttributesMap containing all values of a given key as a String array or
     *         comma-separated String, depending on the <code>joinValues</code> parameter.
     */
    Map translateAttributes(Map attributes, boolean joinValues);

    /**
     * @return All possible result names.
     */
    String[] getSupportedResultNames();

}
