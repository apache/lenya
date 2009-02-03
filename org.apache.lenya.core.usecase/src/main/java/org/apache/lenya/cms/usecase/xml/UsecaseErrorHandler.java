/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.lenya.cms.usecase.xml;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.lenya.cms.usecase.AbstractUsecase;
import org.apache.xml.utils.SAXSourceLocator;
import org.apache.xml.utils.WrappedRuntimeException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Error handler which outputs its errors to usecase messages.
 */
public class UsecaseErrorHandler implements ErrorHandler {

    protected static final String MSG_ERROR = "usecase.validationError";
    protected static final String MSG_WARNING = "usecase.validationWarning";
    protected static final String MSG_FATAL = "usecase.validationFatal";

    private AbstractUsecase usecase;

    /**
     * Ctor.
     * @param usecase The usecase.
     */
    public UsecaseErrorHandler(AbstractUsecase usecase) {
        this.usecase = usecase;
    }

    protected void addErrorMessage(SAXParseException e, String message) {
        String[] params = new String[3];
        params[0] = e.getMessage();
        params[1] = Integer.toString(e.getLineNumber());
        params[2] = Integer.toString(e.getColumnNumber());
        this.usecase.addErrorMessage(message, params);
    }

    public void error(SAXParseException e) throws SAXException {
        addErrorMessage(e, MSG_ERROR);
    }

    public void fatalError(SAXParseException e) throws SAXException {
        addErrorMessage(e, MSG_FATAL);
    }

    public void warning(SAXParseException e) throws SAXException {
        addErrorMessage(e, MSG_WARNING);
    }

}
