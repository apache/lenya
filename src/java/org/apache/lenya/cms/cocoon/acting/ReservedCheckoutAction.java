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

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.lenya.ac.AccessControllerResolver;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.UserManager;
import org.apache.lenya.ac.impl.DefaultAccessController;
import org.apache.lenya.cms.rc.FileReservedCheckOutException;
import org.apache.lenya.util.ServletHelper;
import org.apache.log4j.Logger;

/**
 * Action doing reserved checkout
 */
public class ReservedCheckoutAction extends RevisionControllerAction  {
    Logger log = Logger.getLogger(ReservedCheckoutAction.class);

    public Map act(
        Redirector redirector,
        SourceResolver resolver,
        Map objectModel,
        String src,
        Parameters parameters)
        throws Exception {
        super.act(redirector, resolver, objectModel, src, parameters);

        HashMap actionMap = new HashMap();

        //check out
        try {
            getLogger().debug(".act(): Filename: " + getFilename());
            getLogger().debug(".act(): Username: " + getUsername());

            if (getFilename() == null) {
                throw new Exception("Filename is null");
            }

            if (getUsername() == null) {
                throw new Exception("Username is null");
            }

            getRc().reservedCheckOut(getFilename(), getUsername());
        } catch (FileReservedCheckOutException e) {
            actionMap.put("exception", "fileReservedCheckOutException");
            actionMap.put("filename", getFilename());
            
            String userId = e.getCheckOutUsername();
            if (userId != null && !userId.equals("")) {
                User user = getUser(objectModel, userId);
                if (user != null) {
                    actionMap.put("userFullName", user.getName());
                }
                actionMap.put("user", userId);
            }
            
            actionMap.put("date", e.getCheckOutDate());
            getLogger().warn(
                "Document "
                    + getFilename()
                    + " already checked-out by "
                    + e.getCheckOutUsername()
                    + " since "
                    + e.getCheckOutDate());

            return actionMap;
        } catch (Exception e) {
            actionMap.put("exception", "genericException");
            actionMap.put("filename", getFilename());
            actionMap.put("message", "" + e.getMessage());
            log.error("The document " + getFilename() + " couldn't be checked out: ", e);

            return actionMap;
        }

        return null;
    }
    
}
