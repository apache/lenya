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

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

import org.apache.cocoon.components.flow.javascript.fom.FOM_Cocoon;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.Machine;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.impl.PolicyAuthorizer;
import org.apache.lenya.cms.publication.DocumentHelper;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeException;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.rc.FileReservedCheckInException;
import org.apache.lenya.cms.rc.RCEnvironment;
import org.apache.lenya.cms.rc.RevisionController;
import org.apache.lenya.cms.workflow.WorkflowDocument;
import org.apache.lenya.cms.workflow.WorkflowFactory;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.log4j.Logger;

/**
 * Flowscript utility class.
 * The FOM_Cocoon object is not passed in the constructor to avoid
 * errors. This way, not the initial, but the current FOM_Cocoon
 * object is used by the methods.
 */
public class FlowHelper {

    private static final Logger log = Logger.getLogger(FlowHelper.class);

    /**
     * Ctor.
     */
    public FlowHelper() {
    }

    /**
     * Returns the current workflow situation.
     * @param cocoon The FOM_Cocoon object.
     * @return A situation.
     * @throws AccessControlException when something went wrong.
     */
    public Situation getSituation(FOM_Cocoon cocoon) throws AccessControlException {
        Request request = ObjectModelHelper.getRequest(cocoon.getObjectModel());
        Session session = request.getSession();
        Identity identity = (Identity) session.getAttribute(Identity.class.getName());

        String userId = "";
        String ipAddress = "";

        User user = identity.getUser();
        if (user != null) {
            userId = user.getId();
        }

        Machine machine = identity.getMachine();
        if (machine != null) {
            ipAddress = machine.getIp();
        }

        Role[] roles = PolicyAuthorizer.getRoles(request);
        String[] roleIds = new String[roles.length];
        for (int i = 0; i < roles.length; i++) {
            roleIds[i] = roles[i].getId();
        }

        WorkflowFactory factory = WorkflowFactory.newInstance();
        Situation situation = factory.buildSituation(roleIds, userId, ipAddress);
        return situation;
    }

    /**
     * Returns the current page envelope.
     * @param cocoon The FOM_Cocoon object.
     * @return A page envelope.
     * @throws PageEnvelopeException when something went wrong.
     */
    public PageEnvelope getPageEnvelope(FOM_Cocoon cocoon) throws PageEnvelopeException {
        PageEnvelopeFactory factory = PageEnvelopeFactory.getInstance();
        return factory.getPageEnvelope(cocoon.getObjectModel());
    }

    /**
     * Returns the request URI of the current request.
     * @param cocoon The FOM_Cocoon object.
     * @return A string.
     */
    public String getRequestURI(FOM_Cocoon cocoon) {
        return cocoon.getRequest().getRequestURI();
    }
    
    /**
     * Returns the request object of the current request.
     * @param cocoon The FOM_Cocoon object.
     * @return A request object.
     */
    public Request getRequest(FOM_Cocoon cocoon) {
        return cocoon.getRequest();
    }

    /**
     * Returns the Cocoon Object Model
     * @param cocoon The Flow Object Model of Cocoon
     * @return The object model
     */
    public Map getObjectModel(FOM_Cocoon cocoon) {
        return cocoon.getObjectModel();
    }
    
    /**
     * Returns a DocumentHelper instance.  
     * @param cocoon The Flow Object Model of Cocoon 
     * @return The document helper
     * @see DocumentHelper
     */
    public DocumentHelper getDocumentHelper(FOM_Cocoon cocoon) {
        return new DocumentHelper(cocoon.getObjectModel());
    }
    
    public static final String SEPARATOR = ":";

    /**
     * Resolves the request parameter value for a specific name.
     * The parameter names are encoded as <code>{name}:{value}.{axis}</code>.
     * This is a workaround for the &lt;input type="image"/&gt;
     * bug in Internet Explorer.
     * @param cocoon The FOM_Cocoon object.
     * @param parameterName The request parameter name.
     * @return A string.
     */
    public String getImageParameterValue(FOM_Cocoon cocoon, String parameterName) {

        log.debug("Resolving parameter value for name [" + parameterName + "]");

        Request request = cocoon.getRequest();
        String value = request.getParameter(parameterName);

        if (value == null) {
            String prefix = parameterName + SEPARATOR;
            Enumeration e = request.getParameterNames();
            while (e.hasMoreElements() && value == null) {
                String name = (String) e.nextElement();
                if (name.startsWith(prefix)) {
                    log.debug("Complete parameter name: [" + name + "]");
                    value = name.substring(prefix.length(), name.length() - 2);
                    log.debug("Resolved value: [" + value + "]");
                }
            }
        }

        return value;
    }
    
    /**
     * Trigger a workflow event for the document associated with the current PageEnvelope.
     * @param cocoon The Cocoon Flow Object Model
     * @param event The name of the workflow event to trigger.
     * @throws WorkflowException If an workflow error occurs
     * @throws PageEnvelopeException Page envelope can not operate properly.
     * @throws AccessControlException If an access control violation occurs.
     */
    public void triggerWorkflow(FOM_Cocoon cocoon, String event) 
    throws WorkflowException, PageEnvelopeException, AccessControlException {
        final WorkflowDocument wf = (WorkflowDocument)WorkflowFactory.newInstance().buildInstance(getPageEnvelope(cocoon).getDocument());
        wf.invoke(getSituation(cocoon), event);
    }
    
    /**
     * Get a RevisionController instance.
     * @param cocoon The Cocoon Flow Object Model
     * @throws PageEnvelopeException Page envelope can not operate properly.
     * @throws IOException If an IOException occurs.
     * @see PageEnvelope
     * @see RevisionController
     */
    public RevisionController getRevisionController(FOM_Cocoon cocoon) 
    throws PageEnvelopeException, IOException {        
        final Publication publication = getPageEnvelope(cocoon).getPublication();
        final String publicationPath = publication.getDirectory().getAbsolutePath();
        final RCEnvironment rcEnvironment = RCEnvironment.getInstance(publication.getServletContext().getAbsolutePath());
        String rcmlDirectory = rcEnvironment.getRCMLDirectory();
        rcmlDirectory = publicationPath + File.separator + rcmlDirectory;
        String backupDirectory = rcEnvironment.getBackupDirectory();
        backupDirectory = publicationPath + File.separator + backupDirectory;

        return new RevisionController(rcmlDirectory, backupDirectory, publicationPath);    	
    }
    
    /**
     * Checkis in the current document from the PageEnvelope context.
     * @param cocoon The Cocoon Flow Object Model
     * @param backup Wether a new revision should be created.
     * @throws FileReservedCheckInException 
     * @throws Exception
     * @see RevisionController#reservedCheckIn(String, String, boolean)
     */
    public void reservedCheckIn(FOM_Cocoon cocoon, boolean backup) 
    throws FileReservedCheckInException, Exception
    {
        final Identity identity = (Identity) ObjectModelHelper.getRequest(cocoon.getObjectModel()).getSession().getAttribute(Identity.class.getName());
        final PageEnvelope pageEnvelope = getPageEnvelope(cocoon);
        final Publication publication = getPageEnvelope(cocoon).getPublication();
        final String filename = pageEnvelope.getDocument().getFile().getAbsolutePath().substring(publication.getDirectory().getAbsolutePath().length());   
        getRevisionController(cocoon).reservedCheckIn(filename, identity.getUser().getId(), backup);
    }
}
