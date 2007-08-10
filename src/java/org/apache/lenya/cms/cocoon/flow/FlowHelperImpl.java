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

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.components.flow.javascript.fom.FOM_Cocoon;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.DocumentUtil;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeException;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.publication.util.DocumentHelper;
import org.apache.lenya.cms.rc.FileReservedCheckInException;
import org.apache.lenya.cms.repository.Node;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.workflow.WorkflowUtil;
import org.apache.lenya.workflow.WorkflowException;

/**
 * Flowscript utility class. The FOM_Cocoon object is not passed in the constructor to avoid errors.
 * This way, not the initial, but the current FOM_Cocoon object is used by the methods.
 */
public class FlowHelperImpl extends AbstractLogEnabled implements FlowHelper, Serviceable {

    /**
     * Ctor.
     */
    public FlowHelperImpl() {
        // do nothing
    }

    /**
     * @see org.apache.lenya.cms.cocoon.flow.FlowHelper#getPageEnvelope(org.apache.cocoon.components.flow.javascript.fom.FOM_Cocoon)
     */
    public PageEnvelope getPageEnvelope(FOM_Cocoon cocoon) throws PageEnvelopeException {
        Request request = getRequest(cocoon);
        try {
            Session session = RepositoryUtil.getSession(this.manager, request);
            DocumentFactory map = DocumentUtil.createDocumentFactory(this.manager, session);
            PageEnvelopeFactory factory = PageEnvelopeFactory.getInstance();
            Publication publication = PublicationUtil.getPublication(this.manager, request);
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
    public Request getRequest(FOM_Cocoon cocoon) {
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
        return new DocumentHelper(this.manager, cocoon.getObjectModel());
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
        Request request = getRequest(cocoon);
        try {
            Session session = RepositoryUtil.getSession(this.manager, request);
            WorkflowUtil.invoke(this.manager, session, getLogger(), document, event);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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

    private ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }
}