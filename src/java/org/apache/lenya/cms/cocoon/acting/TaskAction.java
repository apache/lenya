/*
$Id
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.cms.cocoon.acting;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;

import org.apache.cocoon.acting.AbstractComplementaryConfigurableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.SourceResolver;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.cms.task.*;
import org.apache.lenya.cms.workflow.WorkflowFactory;
import org.apache.lenya.workflow.Event;
import org.apache.lenya.workflow.Situation;
import org.apache.lenya.workflow.WorkflowInstance;

import java.io.File;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


/**
 * An action that executes a task.
 *
 * @author <a href="mailto:ah@lenya.org">Andreas Hartmann</a>
 */
public class TaskAction extends AbstractComplementaryConfigurableAction {
    private String taskId = null;

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param configuration DOCUMENT ME!
     *
     * @throws ConfigurationException DOCUMENT ME!
     */
    public void configure(Configuration configuration)
        throws ConfigurationException {
        super.configure(configuration);

        try {
            taskId = configuration.getChild("task").getAttribute(TaskManager.TASK_ID_ATTRIBUTE);
            getLogger().debug("CONFIGURATION:\ntask id = " + taskId);
        } catch (ConfigurationException e) {
            getLogger().debug("CONFIGURATION:\nNo task id provided");
        }
    }

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
    public java.util.Map act(Redirector redirector, SourceResolver sourceResolver, Map objectModel,
        String str, Parameters parameters) throws java.lang.Exception {
        Publication publication = PublicationFactory.getPublication(objectModel);
        File publicationDirectory = publication.getDirectory();

        // Get request object
        Request request = ObjectModelHelper.getRequest(objectModel);

        if (request == null) {
            getLogger().error("No request object");

            return null;
        }

        taskId = parameters.getParameter("task-id", taskId);

        if (taskId == null) {
            throw new IllegalStateException("No task id provided!");
        }

        //------------------------------------------------------------
        // prepare default parameters
        //------------------------------------------------------------
        Parameters taskParameters = new Parameters();

        taskParameters.setParameter(Task.PARAMETER_SERVLET_CONTEXT,
            publication.getServletContext().getCanonicalPath());
        taskParameters.setParameter(Task.PARAMETER_CONTEXT_PREFIX, request.getContextPath() + "/");
        taskParameters.setParameter(Task.PARAMETER_SERVER_PORT,
            Integer.toString(request.getServerPort()));
        taskParameters.setParameter(Task.PARAMETER_SERVER_URI, "http://" + request.getServerName());
        taskParameters.setParameter(Task.PARAMETER_PUBLICATION_ID, publication.getId());

        // set parameters using the request parameters
        for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
            String name = (String) e.nextElement();
            taskParameters.setParameter(name, request.getParameter(name));
        }

        String[] parameterNames = parameters.getNames();
		// set parameters using the request parameters
		for (int i = 0; i < parameterNames.length; i++) {
			taskParameters.setParameter(parameterNames[i], parameters.getParameter(parameterNames[i]));
		}

        //------------------------------------------------------------
        // execute task
        //------------------------------------------------------------
        getLogger().debug("\n-------------------------------------------------" +
            "\n- Executing task '" + getTaskId() + "'" +
            "\n-------------------------------------------------");
            
        String eventName = request.getParameter("lenya.event");
        boolean hasWorkflow = false;
        WorkflowFactory factory = null;
        Document document = null;
        
        if (eventName != null) {
            // check for workflow instance first (task can initialize the workflow history)
            factory = WorkflowFactory.newInstance();
            PageEnvelope envelope = PageEnvelopeFactory.getInstance().getPageEnvelope(objectModel);
            document = envelope.getDocument();
            hasWorkflow = factory.hasWorkflow(document);
        }

        TaskManager manager = new TaskManager(publication.getDirectory().getCanonicalPath());
        Task task = manager.getTask(getTaskId());

        task.parameterize(taskParameters);
        task.execute(publication.getServletContext().getCanonicalPath());

        if (eventName != null && hasWorkflow) {
            WorkflowInstance instance = factory.buildInstance(document);
            Situation situation = factory.buildSituation(objectModel);

            Event event = null;
            Event[] events = instance.getExecutableEvents(situation);

            for (int i = 0; i < events.length; i++) {
                if (events[i].getName().equals(eventName)) {
                    event = events[i];
                }
            }

            assert event != null;
            instance.invoke(situation, event);
        }

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
        String parent_uri = (String) session.getAttribute(
                "org.apache.lenya.cms.cocoon.acting.TaskAction.parent_uri");
        HashMap actionMap = new HashMap();
        actionMap.put("parent_uri", parent_uri);
        session.removeAttribute("org.apache.lenya.cms.cocoon.acting.TaskAction.parent_uri");

        return actionMap;
    }
}
