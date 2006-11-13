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
package org.apache.lenya.cms.site.usecases;

import java.io.FileNotFoundException;
import java.util.Map;

import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.lenya.ac.Identity;
import org.apache.lenya.ac.User;
import org.apache.lenya.cms.metadata.dublincore.DublinCoreHelper;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.rc.RevisionController;
import org.apache.lenya.cms.usecase.DocumentUsecase;
import org.apache.lenya.cms.usecase.UsecaseException;
import org.apache.lenya.cms.workflow.WorkflowUtil;

/**
 * Rollback.
 */
public class Rollback extends DocumentUsecase {
	
    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getNodesToLock()
     */
    protected org.apache.lenya.cms.repository.Node[] getNodesToLock() throws UsecaseException {
        org.apache.lenya.cms.repository.Node[] objects = { getSourceDocument().getRepositoryNode() };
        return objects;
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckPreconditions()
     */
    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();
        if (!WorkflowUtil.canInvoke(this.manager,
                getSession(),
                getLogger(),
                getSourceDocument(),
                getEvent())) {
            addErrorMessage("error-workflow-document", new String[] { getEvent(),
                    DublinCoreHelper.getTitle(getSourceDocument()) });
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();

        // Get parameters                                                                                                                       
        String rollbackTime = getParameterAsString("rollbackTime");
            
        // Do the rollback to an earlier version
        long newtime = 0;
        
        Document document = getSourceDocument();
        
        // Initialize Revision Controller
        RevisionController rc = new RevisionController();
        
        Map objectModel = ContextHelper.getObjectModel(getContext());
        Request request = ObjectModelHelper.getRequest(objectModel);
        Session session = request.getSession(false);
        Identity identity = (Identity) session.getAttribute(Identity.class.getName());
        User user = identity.getUser();
        
        try {
            newtime = rc.rollback(document.getRepositoryNode(), user.getId(), true, new Long(rollbackTime).longValue());
            WorkflowUtil.invoke(this.manager,
                getSession(),
                getLogger(),
                getSourceDocument(),
                getEvent());
        } catch (FileNotFoundException e) {
            addErrorMessage("Unable to roll back!" + e);
        } catch (Exception e) {
            addErrorMessage("Unable to roll back!" + e);
        }
            
        getLogger().debug("rollback complete, old (and now current) time was " + rollbackTime +
              " backup time is " + newtime);
                
    }

    protected String getEvent() {
        return "edit";
    }

}
