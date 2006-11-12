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
package org.apache.lenya.xml;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.cocoon.components.validation.Validator;
import org.apache.cocoon.xml.dom.DOMStreamer;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.publication.Document;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;

/**
 * Validation utility.
 */
public class ValidationUtil {

    /**
     * @param manager The service manager.
     * @param document The document to validate.
     * @param handler The SAX error handler.
     * @throws Exception if an error occurs.
     */
    public static void validate(ServiceManager manager, Document document, ErrorHandler handler)
            throws Exception {

        org.w3c.dom.Document xmlDoc = SourceUtil.readDOM(document.getSourceURI(), manager);
        validate(manager, xmlDoc, document.getResourceType().getSchema(), handler);

    }

    /**
     * @param manager The service manager.
     * @param xmlDoc The XML document.
     * @param schema The schema to use.
     * @param handler The SAX error handler.
     * @throws Exception if an error occurs.
     */
    public static void validate(ServiceManager manager, org.w3c.dom.Document xmlDoc, Schema schema,
            ErrorHandler handler) throws Exception {

        Validator validator = null;
        try {
            validator = (Validator) manager.lookup(Validator.ROLE);
            ContentHandler validatorHandler = validator.getValidationHandler(schema.getURI());

            DOMStreamer streamer = new DOMStreamer(validatorHandler);
            streamer.stream(xmlDoc);

        } finally {
            if (validator != null) {
                manager.release(validator);
            }
        }
    }

}
