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

/* $Id$  */

package org.apache.lenya.cms.cocoon.acting;

import java.util.Collections;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.util.ServletHelper;

/**
 * Action that checks if the current URL represents an existing document.
 */
public class LanguageExistsAction extends ServiceableAction {

    /**
     * Check if the current URL represents an existing document.
     * @return an empty <code>Map</code> if there is a version of this document for the current
     *         language, <code>null</code> otherwise.
     * @throws Exception if an error occurs
     */
    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String source,
            Parameters parameters) throws Exception {

        Request request = ObjectModelHelper.getRequest(objectModel);
        DocumentFactory factory = DocumentUtil.getDocumentFactory(this.manager, request);

        String url = ServletHelper.getWebappURI(request);
        if (factory.isDocument(url)) {
            return Collections.unmodifiableMap(Collections.EMPTY_MAP);
        }
        else {
            return null;
        }
    }
}