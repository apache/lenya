/*
 * $Id: TaskAction.java,v 1.6 2003/02/07 12:14:08 ah Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 wyona. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by wyona (http://www.wyona.org)"
 *
 * 4. The name "wyona" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@wyona.org
 *
 * 5. Products derived from this software may not be called "wyona" nor may "wyona"
 *    appear in their names without prior written permission of wyona.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by wyona (http://www.wyona.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY wyona "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. wyona WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL wyona BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF wyona HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. wyona WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Wyona includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
/*
 * TaskAction.java
 *
 * Created on November 6, 2002, 6:22 PM
 */
package org.wyona.cms.cocoon.acting;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;

import org.apache.cocoon.acting.AbstractComplementaryConfigurableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;

import org.apache.log4j.Category;

import org.wyona.cms.publishing.PublishingEnvironment;
import org.wyona.cms.task.*;

import java.util.Enumeration;
import java.util.HashMap;


/**
 * An action that executes a task.
 *
 * @author <a href="mailto:ah@wyona.org">Andreas Hartmann</a>
 */
public class TaskAction extends AbstractComplementaryConfigurableAction {
    static Category log = Category.getInstance(TaskAction.class);
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
            log.debug("CONFIGURATION:\ntask id = " + taskId);
        } catch (ConfigurationException e) {
            log.debug("CONFIGURATION:\nNo task id provided");
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
    public java.util.Map act(org.apache.cocoon.environment.Redirector redirector,
        org.apache.cocoon.environment.SourceResolver sourceResolver, java.util.Map objectModel,
        String str, Parameters parameters) throws java.lang.Exception {
        // Get Source
        org.apache.cocoon.environment.Source inputSource = sourceResolver.resolve("");
        String publicationPath = inputSource.getSystemId();

        // Remove "file:" protocol
        publicationPath = publicationPath.substring(5);

        log.info("######### " + publicationPath);

        if (publicationPath.endsWith("/")) {
            publicationPath = publicationPath.substring(0, publicationPath.length() - 1);
        }

        log.info("######### " + publicationPath);

        int lastSlashIndex = publicationPath.lastIndexOf("/");
        String publicationId = publicationPath.substring(lastSlashIndex + 1);

        log.info("#######id " + publicationId);

        publicationPath = publicationPath.substring(0, lastSlashIndex + 1);

        log.info("######### " + publicationPath);

        String publicationPrefix = PublishingEnvironment.PUBLICATION_PREFIX;

        //        String contextPath = publicationPath.replaceAll(publicationPrefix, "");
        String contextPath = publicationPath.substring(0,
                publicationPath.length() - publicationPrefix.length());

        log.info("######### " + contextPath);

        publicationPath += publicationId;

        // Get request object
        Request request = ObjectModelHelper.getRequest(objectModel);

        if (request == null) {
            getLogger().error("No request object");

            return null;
        }

        taskId = parameters.getParameter("task-id", taskId);

        if (taskId == null) {
            log.error("No task id provided! ", new IllegalStateException());
        }

        //------------------------------------------------------------
        // prepare default parameters
        //------------------------------------------------------------
        Parameters taskParameters = new Parameters();

        taskParameters.setParameter(Task.PARAMETER_SERVLET_CONTEXT, contextPath);
        taskParameters.setParameter(Task.PARAMETER_CONTEXT_PREFIX, request.getContextPath() + "/");
        taskParameters.setParameter(Task.PARAMETER_SERVER_PORT,
            Integer.toString(request.getServerPort()));
        taskParameters.setParameter(Task.PARAMETER_SERVER_URI, "http://" + request.getServerName());
        taskParameters.setParameter(Task.PARAMETER_PUBLICATION_ID, publicationId);

        // set parameters using the request parameters
        for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
            String name = (String) e.nextElement();
            taskParameters.setParameter(name, request.getParameter(name));
        }

        //------------------------------------------------------------
        // execute task
        //------------------------------------------------------------
        log.debug("\n-------------------------------------------------" + "\n- Executing task '" +
            getTaskId() + "'" + "\n-------------------------------------------------");

        TaskManager manager = new TaskManager(publicationPath);
        Task task = manager.getTask(getTaskId());

        task.parameterize(taskParameters);
        task.execute(contextPath);

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
                "org.wyona.cms.cocoon.acting.TaskAction.parent_uri");
        HashMap actionMap = new HashMap();
        actionMap.put("parent_uri", parent_uri);
        session.removeAttribute("org.wyona.cms.cocoon.acting.TaskAction.parent_uri");

        return actionMap;
    }
}
