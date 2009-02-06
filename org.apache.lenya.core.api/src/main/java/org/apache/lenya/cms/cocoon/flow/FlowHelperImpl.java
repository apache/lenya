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

package org.apache.lenya.cms.cocoon.flow;

import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.cocoon.components.flow.javascript.fom.FOM_Cocoon;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.util.AbstractLogEnabled;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentFactoryBuilder;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeException;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.publication.util.DocumentHelper;
import org.apache.lenya.cms.rc.FileReservedCheckInException;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.repository.RepositoryManager;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.workflow.WorkflowUtil;
import org.apache.lenya.util.ServletHelper;
import org.apache.lenya.workflow.WorkflowException;

/**
 * Flowscript utility class. The FOM_Cocoon object is not passed in the constructor to avoid errors.
 * This way, not the initial, but the current FOM_Cocoon object is used by the methods.
 */
public class FlowHelperImpl extends AbstractLogEnabled implements FlowHelper {
    
    private RepositoryManager repositoryManager;
    private DocumentFactoryBuilder documentFactoryBuilder;

    public RepositoryManager getRepositoryManager() {
        return repositoryManager;
    }

    public void setRepositoryManager(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    public DocumentFactoryBuilder getDocumentFactoryBuilder() {
        return documentFactoryBuilder;
    }

    public void setDocumentFactoryBuilder(DocumentFactoryBuilder documentFactoryBuilder) {
        this.documentFactoryBuilder = documentFactoryBuilder;
    }

    /**
     * @see org.apache.lenya.cms.cocoon.flow.FlowHelper#getPageEnvelope(org.apache.cocoon.components.flow.javascript.fom.FOM_Cocoon)
     */
    public PageEnvelope getPageEnvelope(FOM_Cocoon cocoon) throws PageEnvelopeException {
        HttpServletRequest request = getRequest(cocoon);
        try {
            Session session = RepositoryUtil.getSession(getRepositoryManager(), request);
            DocumentFactory map = getDocumentFactoryBuilder().createDocumentFactory(session);
            PageEnvelopeFactory factory = PageEnvelopeFactory.getInstance();
            URLInformation info = new URLInformation(ServletHelper.getWebappURI(request));
            Publication publication = map.getPublication(info.getPublicationId());
            return factory.getPageEnvelope(map, cocoon.getObjectModel(), publication);
        } catch (Exception e) {
            throw new PageEnvelopeException(e);
        }
    }

    /**
     * @see org.apache.lenya.cms.cocoon.flow.FlowHelper#getRequestURI(org.apache.cocoon.components.flow.javascript.fom.FOM_Cocoon)
     */
    public String getRequestURI(FOM_Cocoon cocoon) {
        return cocoon.getRequest().getRequestURI();
    }

    /**
     * @see org.apache.lenya.cms.cocoon.flow.FlowHelper#getRequest(org.apache.cocoon.components.flow.javascript.fom.FOM_Cocoon)
     */
    public HttpServletRequest getRequest(FOM_Cocoon cocoon) {
        return cocoon.getRequest();
    }

    /**
     * @see org.apache.lenya.cms.cocoon.flow.FlowHelper#getObjectModel(org.apache.cocoon.components.flow.javascript.fom.FOM_Cocoon)
     */
    public Map getObjectModel(FOM_Cocoon cocoon) {
        return cocoon.getObjectModel();
    }

    /**
     * @see org.apache.lenya.cms.cocoon.flow.FlowHelper#getDocumentHelper(org.apache.cocoon.components.flow.javascript.fom.FOM_Cocoon)
     */
    public DocumentHelper getDocumentHelper(FOM_Cocoon cocoon) {
        return new DocumentHelper(cocoon.getObjectModel());
    }

    /**
     * <code>SEPARATOR</code> The separator
     */
    public static final String SEPARATOR = ":";

    /**
     * @see org.apache.lenya.cms.cocoon.flow.FlowHelper#getImageParameterValue(org.apache.cocoon.components.flow.javascript.fom.FOM_Cocoon,
     *      java.lang.String)
     */
    public String getImageParameterValue(FOM_Cocoon cocoon, String parameterName) {

        getLogger().debug("Resolving parameter value for name [" + parameterName + "]");

        Request request = cocoon.getRequest();
        String value = request.getParameter(parameterName);

        if (value == null) {
            String prefix = parameterName + SEPARATOR;
            Enumeration e = request.getParameterNames();
            while (e.hasMoreElements() && value == null) {
                String name = (String) e.nextElement();
                if (name.startsWith(prefix)) {
                    getLogger().debug("Complete parameter name: [" + name + "]");
                    value = name.substring(prefix.length(), name.length() - 2);
                    getLogger().debug("Resolved value: [" + value + "]");
                }
            }
        }

        return value;
    }

    /**
     * @see org.apache.lenya.cms.cocoon.flow.FlowHelper#triggerWorkflow(org.apache.cocoon.components.flow.javascript.fom.FOM_Cocoon,
     *      java.lang.String)
     */
    public void triggerWorkflow(FOM_Cocoon cocoon, String event) throws WorkflowException,
            PageEnvelopeException, AccessControlException {
        Document document = getPageEnvelope(cocoon).getDocument();
        WorkflowUtil.invoke(getLogger(), document, event);
    }

    /**
     * @see org.apache.lenya.cms.cocoon.flow.FlowHelper#reservedCheckIn(org.apache.cocoon.components.flow.javascript.fom.FOM_Cocoon,
     *      boolean)
     */
    public void reservedCheckIn(FOM_Cocoon cocoon, boolean backup)
            throws FileReservedCheckInException, Exception {
        final PageEnvelope pageEnvelope = getPageEnvelope(cocoon);
        Node node = pageEnvelope.getDocument().getRepositoryNode();
        node.checkin();
    }

}