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

import java.util.HashMap;
import java.util.Map;
import java.util.Date;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.environment.Session;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.cms.cocoon.acting.RevisionControllerAction;
import org.apache.lenya.cms.rc.CheckOutEntry;
import org.apache.lenya.cms.rc.CheckInEntry;
import org.apache.lenya.cms.rc.RCML;

/**
 * Checkin a document
 */

public class ForceCheckInAction extends RevisionControllerAction {

    /**
     * Creates a new instance of ForceCheckInAction
     */
    public ForceCheckInAction() {
    }

    public Map act(Redirector redirector, SourceResolver resolver,
            Map objectModel, String src, Parameters parameters)
            throws Exception {

        super.act(redirector, resolver, objectModel, src, parameters);
        
        HashMap actionMap = new HashMap();
        
        // Get request object
        Request request = ObjectModelHelper.getRequest(objectModel);

        // Get the parameters in order to check some preconditions (e.g. 
        // is the document relly checked out).
        String step = parameters.getParameter("step");
                
        Session session = request.getSession(false);
        RCML rcml = getRc().getRCML(getFilename());
        Identity identity = (Identity) session.getAttribute(Identity.class
                .getName());

        if (step.equals("checkit")) {
            if (rcml.getLatestEntry().getType() != RCML.ci) {
                CheckOutEntry coe = rcml.getLatestCheckOutEntry();
                actionMap.put("user", coe.getIdentity());
                Date checkOutDate = new Date(coe.getTime());
                actionMap.put("date", checkOutDate.toString());
                actionMap.put("message", "lenya.rc.checkedoutalready");
                actionMap.put("state", "co");
            } else {
                CheckInEntry cie = rcml.getLatestCheckInEntry();
                actionMap.put("user", cie.getIdentity());
                Date checkInDate = new Date(cie.getTime());
                actionMap.put("date", checkInDate.toString());
                actionMap.put("message", "The resource has already been checked in by");    
                actionMap.put("state", "ci");
            }
            return actionMap;
        }

        rcml.checkOutIn(RCML.ci, identity.getUser().getId(), new Date().getTime(),
                false);

        return null;
    }
}
