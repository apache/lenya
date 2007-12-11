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

/*
  This file is deprecated. You should not base new code on it. 
  It's still used by Kupu and BXE,  that's why it remains for now. 
  In the future, generic editor functionality and document handling 
  helpers should be provided by the editors module only.
*/

importClass(Packages.org.apache.lenya.cms.cocoon.flow.FlowHelper);
importClass(Packages.org.apache.excalibur.source.SourceResolver);
importClass(Packages.org.apache.lenya.cms.cocoon.source.SourceUtil);

/**
 * Provides general flow functions for document editing.
 * @version $Id$
 */

function checkoutDocument() {
}

function checkinDocument() {
}

/**
 * Saves a Source to the document in the current PageEnvelope.
 * TODO: Use nobby's new usecase fw in 2.0 branch. Exception handling e.g. display appropriate error pages.
 * @param sourceUri An URI of a Source providing the edited document data
 * @param useBuffer If "true", the source Source is read into a buffer before it is written to its final destination.
 * @param workflowEvent Name of the workflow event to trigger. Default is "edit".
 * @param noWorkflow If true, no workflow event will be triggered. Default is false.
 * @param noCheckin If true, the current document is not checked in. Default is false.
 * @param backup If true, a new revision is created on checkin. Default is true.
 * @param status Default int value is 204. Used to set the response status.
 * @param noStatus If true, then no response status will be set.     
 */
function editDocument() {
    var usecaseName = cocoon.parameters["lenya.usecase"];
    
    var usecaseResolver;
    var usecase;
    
    try {
        var flowHelper = cocoon.getComponent("org.apache.lenya.cms.cocoon.flow.FlowHelper");
        var request = flowHelper.getRequest(cocoon);
        var sourceUrl = Packages.org.apache.lenya.util.ServletHelper.getWebappURI(request);

        usecaseResolver = cocoon.getComponent("org.apache.lenya.cms.usecase.UsecaseResolver");
        usecase = usecaseResolver.resolve(sourceUrl, usecaseName);

        if (cocoon.log.isDebugEnabled()) {
            cocoon.log.debug("edit-document.js::editDocument() calling usecase methods, usecaseName [" + usecaseName + "], sourceUrl [" + sourceUrl + "]");    
        }

        usecase.setSourceURL(sourceUrl);
        usecase.setName(usecaseName);

        usecase.checkPreconditions();
        usecase.lockInvolvedObjects();
        if (!usecase.getErrorMessages().isEmpty()) {
            throw new Exception("Checking pre-conditions or locking failed!");
        }
        else {
            usecase.execute();
            if (!usecase.getErrorMessages().isEmpty()) {
                throw new Exception("Saving failed!");
            }
        }

        if (cocoon.log.isDebugEnabled()) {
            cocoon.log.debug("edit-document.js::editDocument() after usecase.execute(), hasErrors ? " + usecase.hasErrors());
        }

        // FIXME: handle error messages
        //if (usecase.hasErrors()) {
        //}

        if(_getParameter("noStatus", "false") == "false")
            cocoon.sendStatus(_getParameter("status", 204));
        else
            cocoon.redirectTo(_getParameter("redirectUrl", "FIXME"));

    } catch (exception) {

	/* FIXME: This is unclean because the flow will not return a value
	   if there is an exception */
        cocoon.log.error("Can not edit document.", exception);
        
    }
    finally {
        /* done with usecase component, tell usecaseResolver to release it */
        if (usecaseResolver != null) {
            if (usecase != null) {
                usecaseResolver.release(usecase);
                usecase = undefined;
            }
            cocoon.releaseComponent(usecaseResolver);
        }
    }
        
}

function _getParameter(name, defaultValue) {
    if(cocoon.parameters[name])
        return cocoon.parameters[name];
    else
        return defaultValue;
}

