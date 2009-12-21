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
package org.apache.lenya.cms.cocoon.flow;

import java.util.Map;

import org.apache.cocoon.components.flow.javascript.fom.FOM_Cocoon;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeException;
import org.apache.lenya.cms.publication.util.DocumentHelper;
import org.apache.lenya.cms.rc.FileReservedCheckInException;
import org.apache.lenya.workflow.WorkflowException;

/**
 * Flowscript utility class.
 * @version $Id$
 */
public interface FlowHelper {
	
    /**
     * The Avalon Role.
     */
    String ROLE = FlowHelper.class.getName();

    /**
     * Returns the current page envelope.
     * @param cocoon The FOM_Cocoon object.
     * @return A page envelope.
     * @throws PageEnvelopeException when something went wrong.
     */
    PageEnvelope getPageEnvelope(FOM_Cocoon cocoon) throws PageEnvelopeException;

    /**
     * Returns the request URI of the current request.
     * @param cocoon The FOM_Cocoon object.
     * @return A string.
     */
    String getRequestURI(FOM_Cocoon cocoon);

    /**
     * Returns the request object of the current request.
     * @param cocoon The FOM_Cocoon object.
     * @return A request object.
     */
    Request getRequest(FOM_Cocoon cocoon);

    /**
     * Returns the Cocoon Object Model
     * @param cocoon The Flow Object Model of Cocoon
     * @return The object model
     */
    Map getObjectModel(FOM_Cocoon cocoon);

    /**
     * Returns a DocumentHelper instance.
     * @param cocoon The Flow Object Model of Cocoon
     * @return The document helper
     * @see DocumentHelper
     */
    DocumentHelper getDocumentHelper(FOM_Cocoon cocoon);

    /**
     * Resolves the request parameter value for a specific name. The parameter
     * names are encoded as <code>{name}:{value}.{axis}</code>. This is a
     * workaround for the &lt;input type="image"/&gt; bug in Internet Explorer.
     * @param cocoon The FOM_Cocoon object.
     * @param parameterName The request parameter name.
     * @return A string.
     */
    String getImageParameterValue(FOM_Cocoon cocoon, String parameterName);

    /**
     * Trigger a workflow event for the document associated with the current
     * PageEnvelope.
     * @param cocoon The Cocoon Flow Object Model
     * @param event The name of the workflow event to trigger.
     * @throws WorkflowException If an workflow error occurs
     * @throws PageEnvelopeException Page envelope can not operate properly.
     * @throws AccessControlException If an access control violation occurs.
     */
    void triggerWorkflow(FOM_Cocoon cocoon, String event) throws WorkflowException,
            PageEnvelopeException, AccessControlException;

    /**
     * Checkis in the current document from the PageEnvelope context.
     * @param cocoon The Cocoon Flow Object Model
     * @param backup Wether a new revision should be created.
     * @throws FileReservedCheckInException
     * @throws Exception
     */
    void reservedCheckIn(FOM_Cocoon cocoon, boolean backup) throws FileReservedCheckInException,
            Exception;
}
