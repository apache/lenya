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
package org.apache.lenya.cms.workflow;

import java.util.Map;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.Machine;
import org.apache.lenya.ac.User;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.impl.WorkflowManagerImpl;

/**
 * Lenya-specific workflow manager.
 * 
 * @version $Id:$
 */
public class LenyaWorkflowManager extends WorkflowManagerImpl implements Contextualizable {

    /**
     * @see org.apache.lenya.workflow.WorkflowManager#getSituation()
     */
    public Situation getSituation() {
        Request request = ObjectModelHelper.getRequest(this.objectModel);
        Session session = request.getSession(false);

        Situation situation = null;
        if (session != null) {
            Identity identity = (Identity) session.getAttribute(Identity.class.getName());

            User user = identity.getUser();
            String userId = null;
            if (user != null) {
                userId = user.getId();
            }

            Machine machine = identity.getMachine();
            String machineIp = null;
            if (machine != null) {
                machineIp = machine.getIp();
            }

            situation = new LenyaSituation(identity, this.manager, getLogger());
        } else {
            situation = new LenyaSituation(null, this.manager, getLogger());
        }
        return situation;
    }

    private Map objectModel;

    /**
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize(Context context) throws ContextException {
        this.objectModel = ContextHelper.getObjectModel(context);
    }

}