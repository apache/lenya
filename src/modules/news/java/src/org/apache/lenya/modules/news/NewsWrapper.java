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
package org.apache.lenya.modules.news;

import javax.xml.transform.TransformerException;

import org.apache.avalon.framework.logger.Logger;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.modules.collection.CollectionWrapper;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Element;

/**
 * Wrapper for a news document.
 */
public class NewsWrapper extends CollectionWrapper {

    protected static final int DEFAULT_INCLUDE_ITEMS = 3;
    protected static final String ATTRIBUTE_INCLUDE_ITEMS = "includeItems";

    private short includeItemNumber = DEFAULT_INCLUDE_ITEMS;

    /**
     * @param doc The document to wrap.
     * @param logger The logger.
     */
    public NewsWrapper(Document doc, Logger logger) {
        super(doc, logger);
    }

    protected void loadXml(NamespaceHelper helper) {
        super.loadXml(helper);
        Element docElement = helper.getDocument().getDocumentElement();
        if (docElement.hasAttribute(ATTRIBUTE_INCLUDE_ITEMS)) {
            String number = docElement.getAttribute(ATTRIBUTE_INCLUDE_ITEMS);
            this.includeItemNumber = Short.parseShort(number);
        }
    }

    protected void saveXml(NamespaceHelper helper) throws TransformerException, DocumentException {
        super.saveXml(helper);
        Element docElement = helper.getDocument().getDocumentElement();
        docElement.setAttribute(ATTRIBUTE_INCLUDE_ITEMS, Short.toString(this.includeItemNumber));
    }

    /**
     * @return The number of items to appear on overviews which are generated using the "include"
     *         format.
     */
    public short getIncludeItemNumber() {
        load();
        return this.includeItemNumber;
    }

    /**
     * @param number The number of items to appear on overviews which are generated using the
     *            "include" format.
     */
    public void setIncludeItemNumber(short number) {
        load();
        this.includeItemNumber = number;
    }
    
}
