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

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.cocoon.components.validation.Validator;
import org.apache.cocoon.spring.configurator.WebAppContextUtils;
import org.apache.cocoon.xml.dom.DOMStreamer;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;

/**
 * Validation utility.
 */
public class ValidationUtil {

    /**
     * @param document The document to validate.
     * @param handler The SAX error handler.
     * @throws Exception if an error occurs.
     */
	/* never used
    public static void validate(Document document, ErrorHandler handler) throws Exception {
        org.w3c.dom.Document xmlDoc = DocumentHelper.readDocument(document.getInputStream());
        validate(xmlDoc, document.getResourceType().getSchema(), handler);

    }
    */

    /**
     * @param xmlDoc The XML document.
     * @param schema The schema to use.
     * @param handler The SAX error handler.
     * @throws Exception if an error occurs.
     */
    public static void validate(org.w3c.dom.Document xmlDoc, Schema schema, ErrorHandler handler)
            throws Exception {
        Validator validator = getValidator();
        ContentHandler validatorHandler = validator.getValidationHandler(schema.getURI(), handler);
        new DOMStreamer(validatorHandler).stream(xmlDoc);
    }

    protected static Validator getValidator() {
        Validator validator = (Validator) WebAppContextUtils.getCurrentWebApplicationContext()
                .getBean(Validator.ROLE);
        return validator;
    }

    /**
     * @param source The source to validate.
     * @param schema The schema to use.
     * @param handler The SAX error handler.
     * @throws Exception if an error occurs.
     */
    public static void validate(Source source, Schema schema, ErrorHandler handler)
            throws Exception {
        Validator validator = getValidator();
        ContentHandler validatorHandler = validator.getValidationHandler(schema.getURI(), handler);

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        SAXResult result = new SAXResult(validatorHandler);
        transformer.transform(source, result);
    }

}
