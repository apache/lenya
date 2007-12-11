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
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.workflow.WorkflowUtil;

/**
 * Action to invoke a workflow transition independently from the request document URL. Parameters:
 * <ul>
 * <li><strong>area: </strong> The area.</li>
 * <li><strong>document-uuid: </strong> The document uuid.</li>
 * <li><strong>language: </strong> The language.</li>
 * <li><strong>event: </strong> The event to invoke.</li>
 * </ul>
 */
public class WorkflowInvokerAction extends ServiceableAction {

    /**
     * <code>AREA</code> The area
     */
    public static final String AREA = "area";
    /**
     * <code>DOCUMENT_UUID</code> The document id
     */
    public static final String DOCUMENT_UUID = "document-uuid";
    /**
     * <code>LANGUAGE</code> The language
     */
    public static final String LANGUAGE = "language";
    /**
     * <code>EVENT</code> The event
     */
    public static final String EVENT = "event";

    /**
     * @see org.apache.cocoon.acting.Action#act(org.apache.cocoon.environment.Redirector,
     *      org.apache.cocoon.environment.SourceResolver, java.util.Map, java.lang.String,
     *      org.apache.avalon.framework.parameters.Parameters)
     */
    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String source,
            Parameters parameters) throws Exception {

        String area = parameters.getParameter(AREA);
        String documentId = parameters.getParameter(DOCUMENT_UUID);
        String language = parameters.getParameter(LANGUAGE);
        String eventName = parameters.getParameter(EVENT);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(getClass().getName() + " invoked.");
            getLogger().debug("    Area:        [" + area + "]");
            getLogger().debug("    Document ID: [" + documentId + "]");
            getLogger().debug("    Language:    [" + language + "]");
            getLogger().debug("    Event:       [" + eventName + "]");
        }

        Publication pub;
        Request request = ObjectModelHelper.getRequest(objectModel);

        try {
            pub = PublicationUtil.getPublication(this.manager, request);
        } catch (Exception e) {
            throw new AccessControlException(e);
        }
        Session session = RepositoryUtil.getSession(this.manager, request);
        DocumentFactory map = DocumentUtil.createDocumentFactory(this.manager, session);
        Document document = map.get(pub, area, documentId, language);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("    Invoking workflow event");
        }
        WorkflowUtil.invoke(this.manager, session, getLogger(), document, eventName);

        return Collections.EMPTY_MAP;
    }

}
