/*
$Id: TaskWrapperParameters.java,v 1.1 2003/08/25 09:52:40 andreas Exp $
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
package org.apache.lenya.cms.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.lenya.cms.ac.Machine;
import org.apache.lenya.cms.ac.Role;
import org.apache.lenya.cms.ac.User;
import org.apache.lenya.cms.ac2.Identity;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.util.NamespaceMap;
import org.apache.log4j.Category;

/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class TaskWrapperParameters {

    private static Category log = Category.getInstance(TaskWrapperParameters.class);

    public static final String TASK_ID = "task-id";
    public static final String ROLES = "roles";
    public static final String USER_ID = "user-id";
    public static final String MACHINE = "machine";
    public static final String EVENT = "event";
    public static final String WEBAPP_URL = "webapp-url";

    protected static final String[] KEYS = { TASK_ID, ROLES, USER_ID, MACHINE, EVENT, WEBAPP_URL };

    public static final String TASK_PREFIX = "task";

    private Map parameters = new HashMap();
    private NamespaceMap taskParameters = new NamespaceMap(parameters, TASK_PREFIX);
    private List keys = new ArrayList();

    /**
     * Ctor.
     */
    public TaskWrapperParameters() {
        keys.addAll(Arrays.asList(KEYS));

        for (int i = 0; i < Task.PARAMETERS.length; i++) {
            keys.add(
                NamespaceMap.getFullName(TaskWrapperParameters.TASK_PREFIX, Task.PARAMETERS[i]));
        }
        for (int i = 0; i < keys.size(); i++) {
            log.debug("Adding required key: [" + keys.get(i) + "]");
        }
    }

    /**
     * Sets a wrapper parameter value if this key is a required key.
     * @param key The key.
     * @param value The value.
     */
    protected void put(String key, String value) {
        if (keys.contains(key)) {
            putForced(key, value);
        }
    }

    /**
     * Sets a wrapper parameter value.
     * @param key The key.
     * @param value The value.
     */
    protected void putForced(String key, String value) {
        if (value == null) {
            value = "";
        }
        log.debug("Setting parameter: [" + key + "] = [" + value + "]");
        parameters.put(key, value);
    }

    /**
     * Returns a parameter value.
     * @param key The key.
     * @return The value.
     */
    protected String get(String key) {
        return (String) parameters.get(key);
    }

    /**
     * Checks if this parameters object contains all necessary parameters.
     * @return A boolean value.
     */
    public boolean isComplete() {
        boolean complete = true;
        int i = 0;
        while (complete && i < keys.size()) {
            log.debug("Checking parameter: [" + keys.get(i) + "]");
            complete = complete && parameters.containsKey(keys.get(i));
            log.debug("OK: [" + complete + "]");
            i++;
        }
        return complete;
    }

    /**
     * Returns the missing parameters parameters.
     * @return A string array.
     */
    public String[] getMissingKeys() {
        List keyList = new ArrayList();
        for (int i = 0; i < keys.size(); i++) {
            if (!parameters.containsKey(keys.get(i))) {
                keyList.add(keys.get(i));
            }
        }
        return (String[]) keyList.toArray(new String[keyList.size()]);
    }

    /**
     * Returns the required keys.
     * @return A string array.
     */
    public String[] getRequiredKeys() {
        return (String[]) keys.toArray(new String[keys.size()]);
    }

    /**
     * Returns the role names.
     * @return A string array.
     */
    protected String[] getRoleIDs() {
        String[] roleIDs = get(ROLES).split(",");
        return roleIDs;
    }

    /**
     * Sets the roles.
     * @param roles A role array.
     */
    public void setRoles(Role[] roles) {

        String roleString = "";
        for (int i = 0; i < roles.length; i++) {
            if (i > 0) {
                roleString += ",";
            }
            roleString += roles[i].getId();
        }
        put(ROLES, roleString);
    }

    /**
     * Sets the identity.
     * @param identity An identity.
     */
    public void setIdentity(Identity identity) {

        String userId = "";
        User user = identity.getUser();
        if (user != null) {
            userId = user.getId();
        }
        put(USER_ID, userId);

        String machineIp = "";
        Machine machine = identity.getMachine();
        if (machine != null) {
            machineIp = machine.getIp();
        }
        put(MACHINE, machineIp);
    }

    /**
     * Returns the workflow event name.
     * @return A string.
     */
    public String getEventName() {
        return (String) get(EVENT);
    }

    /**
     * Returns the publication.
     * @return A publication.
     * @throws ExecutionException when something went wrong.
     */
    public Publication getPublication() throws ExecutionException {
        Publication publication;
        try {
            publication =
                PublicationFactory.getPublication(
                    (String) taskParameters.get(Task.PARAMETER_PUBLICATION_ID),
                    (String) taskParameters.get(Task.PARAMETER_SERVLET_CONTEXT));
        } catch (PublicationException e) {
            throw new ExecutionException(e);
        }
        return publication;
    }

    /**
     * Returns the task ID.
     * @return A string.
     */
    public String getTaskId() {
        return get(TASK_ID);
    }

    /**
     * Returns the webapp URL.
     * @return A string.
     */
    public String getWebappUrl() {
        return get(WEBAPP_URL);
    }

    /**
     * Returns the task parameters.
     * @return A parameters object.
     */
    public Parameters getTaskParameters() {
        Properties properties = new Properties();
        properties.putAll(taskParameters.getMap());
        Parameters parameters = Parameters.fromProperties(properties);
        return parameters;
    }

    /**
     * Sets the publication.
     * @param publication A publication.
     */
    public void setPublication(Publication publication) {
        taskParameters.put(Task.PARAMETER_PUBLICATION_ID, publication.getId());
        taskParameters.put(
            Task.PARAMETER_SERVLET_CONTEXT,
            publication.getServletContext().getAbsolutePath());
    }

    /**
     * Sets the webapp URL.
     * @param url A url.
     */
    public void setWebappUrl(String url) {
        put(WEBAPP_URL, url);
    }

    /**
     * Sets the task ID.
     * @param taskId A string.
     */
    public void setTaskId(String taskId) {
        put(TASK_ID, taskId);
    }

    /**
     * Sets the task parameters.
     * @param parameters A parameters object.
     */
    public void setTaskParameters(Parameters parameters) {
        String[] names = parameters.getNames();
        for (int i = 0; i < names.length; i++) {
            String value = parameters.getParameter(names[i], "");
            log.debug("Setting task parameter: [" + names[i] + "] = [" + value + "]");
            taskParameters.put(names[i], value);
        }
    }

    /**
     * Returns the parameter keys.
     * @return A string array.
     */
    public String[] getKeys() {
        return (String[]) parameters.keySet().toArray(new String[parameters.keySet().size()]);
    }

    /**
     * Returns the wrapper parameters.
     * @return A parameters object.
     */
    protected Parameters getParameters() {
        Properties properties = new Properties();
        properties.putAll(parameters);
        Parameters parametersObject = Parameters.fromProperties(properties);
        return parametersObject;
    }
    
    /**
     * Returns the user ID.
     * @return A string.
     */
    public String getUserId() {
        return (String) parameters.get(USER_ID);
    }
    
    /**
     * Returns the machine IP address.
     * @return A string.
     */
    public String getMachineIp() {
        return (String) parameters.get(MACHINE);
    }
    
    /**
     * Adds all key-value pairs in map to the parameters.
     * @param map A map.
     */
    protected void putAll(Map map) {
        for (Iterator i = map.keySet().iterator(); i.hasNext(); ) {
            String key = (String) i.next();
            putForced(key, (String) map.get(key));
        }
    }

}
