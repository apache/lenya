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

/* $Id: RollbackAction.java,v 1.18 2004/03/01 16:18:21 gregor Exp $  */

package org.apache.lenya.cms.cocoon.acting;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;


public class RollbackAction extends RevisionControllerAction {
    /**
     * DOCUMENT ME!
     *
     * @param redirector DOCUMENT ME!
     * @param resolver DOCUMENT ME!
     * @param objectModel DOCUMENT ME!
     * @param src DOCUMENT ME!
     * @param parameters DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public Map act(Redirector redirector, SourceResolver resolver, Map objectModel, String src,
        Parameters parameters) throws Exception {
        super.act(redirector, resolver, objectModel, src, parameters);

        HashMap actionMap = new HashMap();

        // Get request object
        Request request = ObjectModelHelper.getRequest(objectModel);

        if (request == null) {
            getLogger().error("No request object");

            return null;
        }

        // Get parameters                                                                                                                       
        String rollbackTime = request.getParameter("rollbackTime");

        // Do the rollback to an earlier version
        long newtime = 0;

        try {
            newtime = getRc().rollback(getFilename(), getUsername(), true, new Long(rollbackTime).longValue());
        } catch (FileNotFoundException e) {
            getLogger().error("Unable to roll back!" + e);

            return null;
        } catch (Exception e) {
            getLogger().error("Unable to roll back!" + e);

            return null;
        }

        getLogger().debug("rollback complete, old (and now current) time was " + rollbackTime +
            " backup time is " + newtime);

        String location = request.getHeader("Referer");

        getLogger().debug("redirect to " + location);
        actionMap.put("location", location);

        return actionMap;
    }
}
