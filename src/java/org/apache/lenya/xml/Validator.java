/*
 * Copyright 1999-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.lenya.xml;

import org.apache.excalibur.xml.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

/**
 * Validator.
 */
public interface Validator extends EntityResolver {

    /**
     * The Avalon role.
     */
    String ROLE = Validator.class.getName();

    /**
     * Validate a source against a schema.
     * @param xml The xml source.
     * @param schema The schema source.
     * @param handler The error handler.
     */    void validate(InputSource xml, InputSource schema, ErrorHandler handler);

}
