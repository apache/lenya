/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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

package org.apache.lenya.cms.ac;

import java.util.Enumeration;
import java.util.List;

import org.apache.cocoon.environment.Request;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Role;

/**
 * Policy utility class.
 */
public class PolicyUtil {

    /**
     * Fetches the stored roles from the request.
     * @param request The request.
     * @return A role array.
     * @throws AccessControlException If the request does not contain the roles list.
     */
    public static Role[] getRoles(Request request) throws AccessControlException {
        List roleList = (List) request.getAttribute(Role.class.getName());

        if (roleList == null) {
            StringBuffer buf = new StringBuffer();
            buf.append("    URI: [" + request.getRequestURI() + "]\n");
            for (Enumeration e = request.getParameterNames(); e.hasMoreElements(); ) {
                String key = (String) e.nextElement();
                buf.append("    Parameter: [" + key + "] = [" + request.getParameter(key) + "]\n");
            }
            
            throw new AccessControlException("Request [" + request + "] does not contain roles: \n" + buf.toString());
        }
        
        Role[] roles = (Role[]) roleList.toArray(new Role[roleList.size()]);
        return roles;
    }

}
