/*
$Id: Notifier.java,v 1.2 2003/08/25 15:40:55 andreas Exp $
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

import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.lenya.util.NamespaceMap;
import org.apache.log4j.Category;

/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class Notifier extends ParameterWrapper {

    private static Category log = Category.getInstance(Notifier.class);

    public static final String NOTIFICATION_PREFIX = "notification";
    public static final String MAIL_TARGET = "mail";
    public static final String MAIL_PREFIX = "mail";

    public static final String PARAMETER_TO = "tolist";

    private TaskManager taskManager;

    /**
     * Ctor.
     * @param taskManager The task manager.
     * @param parameters The task wrapper parameters.
     */
    public Notifier(TaskManager taskManager, Map parameters) {
        super(parameters);
        this.taskManager = taskManager;
    }

    /**
     * Sends the notification message.
     * @param taskParameters The task parameters.
     * @throws ExecutionException when something went wrong.
     */
    public void sendNotification(TaskParameters taskParameters) throws ExecutionException {

        if (getMap().isEmpty()) {
            log.info("Not sending notification: no parameters provided.");
        } else if ("".equals(get(PARAMETER_TO).trim())) {
            log.info("Not sending notification: empty notification.tolist parameter.");
        }
        else {
            log.info("Sending notification");
            
            Task task = taskManager.getTask(TaskManager.ANT_TASK);

            Parameters params = new Parameters();

            params.setParameter(AntTask.TARGET, MAIL_TARGET);

            String[] keys =
                {
                    Task.PARAMETER_PUBLICATION_ID,
                    Task.PARAMETER_CONTEXT_PREFIX,
                    Task.PARAMETER_SERVER_PORT,
                    Task.PARAMETER_SERVER_URI,
                    Task.PARAMETER_SERVLET_CONTEXT };

            for (int i = 0; i < keys.length; i++) {
                params.setParameter(keys[i], (String) taskParameters.get(keys[i]));
            }

            NamespaceMap mailMap = new NamespaceMap(MAIL_PREFIX);
            mailMap.putAll(getMap());
            NamespaceMap propertiesMap = new NamespaceMap(AntTask.PROPERTIES_PREFIX);
            propertiesMap.putAll(mailMap.getPrefixedMap());

            Map prefixMap = propertiesMap.getPrefixedMap();
            for (Iterator i = prefixMap.keySet().iterator(); i.hasNext();) {
                String key = (String) i.next();
                params.setParameter(key, (String) prefixMap.get(key));
            }

            try {
                task.parameterize(params);
            } catch (ParameterException e) {
                throw new ExecutionException(e);
            }
            log.info("    Executing notification target ...");
            task.execute((String) taskParameters.get(Task.PARAMETER_SERVLET_CONTEXT));
            log.info("    Notification target executed.");
        }
    }

    /**
     * Returns the task manager.
     * @return A task manager.
     */
    protected TaskManager getTaskManager() {
        return taskManager;
    }

    /**
     * @see org.apache.lenya.cms.task.ParameterWrapper#getPrefix()
     */
    public String getPrefix() {
        return NOTIFICATION_PREFIX;
    }

    /**
     * @see org.apache.lenya.cms.task.ParameterWrapper#getRequiredKeys()
     */
    protected String[] getRequiredKeys() {
        String[] requiredKeys = { };
        return requiredKeys;
    }

}
