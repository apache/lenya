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

/* $Id: TaskAction.java,v 1.27 2004/03/01 16:18:21 gregor Exp $  */

package org.apache.lenya.cms.cocoon.acting;

import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.AbstractAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.lenya.cms.cocoon.task.CocoonTaskWrapper;
import org.apache.lenya.cms.task.TaskWrapper;

/**
 * An action that executes a task.
 */
public class TaskAction extends AbstractAction {
    
    /**
     * DOCUMENT ME!
     *
     * @param redirector DOCUMENT ME!
     * @param sourceResolver DOCUMENT ME!
     * @param objectModel DOCUMENT ME!
     * @param str DOCUMENT ME!
     * @param parameters DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws java.lang.Exception DOCUMENT ME!
     */
    public java.util.Map act(
        Redirector redirector,
        SourceResolver sourceResolver,
        Map objectModel,
        String str,
        Parameters parameters)
        throws java.lang.Exception {

        TaskWrapper wrapper = new CocoonTaskWrapper(objectModel, parameters);
        wrapper.execute();

        Request request = ObjectModelHelper.getRequest(objectModel);

        //------------------------------------------------------------
        // get session
        //------------------------------------------------------------
        Session session = request.getSession(true);

        if (session == null) {
            getLogger().error("No session object");

            return null;
        }

        //------------------------------------------------------------
        // Return referer
        //------------------------------------------------------------
        String parent_uri =
            (String) session.getAttribute(
                "org.apache.lenya.cms.cocoon.acting.TaskAction.parent_uri");
        HashMap actionMap = new HashMap();
        actionMap.put("parent_uri", parent_uri);
        session.removeAttribute("org.apache.lenya.cms.cocoon.acting.TaskAction.parent_uri");

        return actionMap;
    }
}
