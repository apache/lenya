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
package org.apache.lenya.cms.site.usecases;

import java.util.Arrays;

import org.apache.lenya.cms.usecase.UsecaseResolver;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Element;

/**
 * <p>
 * Create a usecase document. The document content looks like this:
 * </p>
 * 
 * <pre>
 *   &lt;usecase name=&quot;...&quot;/&gt;
 * </pre>
 */
public class CreateUsecaseDocument extends CreateDocument {

    protected static final String USECASE = "usecase";
    protected static final String RESOURCE_TYPE_USECASE = "usecase";

    /**
     * The namespace for usecase document content.
     */
    public static final String NAMESPACE = "http://apache.org/lenya/usecase/1.0";

    /**
     * The local name of the usecase XML element.
     */
    public static final String ELEMENT_USECASE = "usecase";

    /**
     * The local name of the name attribute.
     */
    public static final String ATTRIBUTE_NAME = "name";
    
    protected void initParameters() {
        super.initParameters();
        setParameter(DOCUMENT_TYPE, RESOURCE_TYPE_USECASE);
        setParameter(RESOURCE_TYPES, Arrays.asList(new String[0]));
    }

    protected void doCheckExecutionConditions() throws Exception {
        super.doCheckExecutionConditions();

        String usecaseName = getParameterAsString(USECASE);
        if (usecaseName.equals("")) {
            addErrorMessage("Please enter a usecase name.");
        } else {
            UsecaseResolver resolver = null;
            try {
                resolver = (UsecaseResolver) this.manager.lookup(UsecaseResolver.ROLE);
                if (!resolver.isRegistered(getSourceURL(), usecaseName)) {
                    addErrorMessage("The usecase '" + usecaseName + "' is not registered.");
                }
            } finally {
                if (resolver != null) {
                    this.manager.release(resolver);
                }
            }
        }
    }

    protected void doExecute() throws Exception {
        super.doExecute();

        NamespaceHelper helper = new NamespaceHelper(NAMESPACE, "", ELEMENT_USECASE);
        Element usecaseElement = helper.getDocument().getDocumentElement();
        usecaseElement.setAttribute(ATTRIBUTE_NAME, getParameterAsString(USECASE));
        DocumentHelper.writeDocument(helper.getDocument(), getNewDocument().getOutputStream());

    }

}
