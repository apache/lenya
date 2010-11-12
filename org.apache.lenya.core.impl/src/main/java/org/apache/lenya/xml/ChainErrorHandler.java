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
package org.apache.lenya.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ChainErrorHandler implements ErrorHandler {
    
    private List handlers = new ArrayList();
    
    public void add(ErrorHandler handler) {
        Validate.notNull(handler);
        this.handlers.add(handler);
    }

    public void error(SAXParseException e) throws SAXException {
        for (Iterator i = this.handlers.iterator(); i.hasNext(); ) {
            ((ErrorHandler) i.next()).error(e);
        }
    }

    public void fatalError(SAXParseException e) throws SAXException {
        for (Iterator i = this.handlers.iterator(); i.hasNext(); ) {
            ((ErrorHandler) i.next()).fatalError(e);
        }
    }

    public void warning(SAXParseException e) throws SAXException {
        for (Iterator i = this.handlers.iterator(); i.hasNext(); ) {
            ((ErrorHandler) i.next()).warning(e);
        }
    }

}
