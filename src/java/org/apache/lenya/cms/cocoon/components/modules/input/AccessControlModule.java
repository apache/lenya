/*
$Id: AccessControlModule.java,v 1.8 2003/09/01 17:02:11 andreas Exp $
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

package org.apache.lenya.cms.cocoon.components.modules.input;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.components.modules.input.AbstractInputModule;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.lenya.cms.ac.ItemManager;
import org.apache.lenya.cms.ac.Machine;
import org.apache.lenya.cms.ac.User;
import org.apache.lenya.cms.ac2.AccessController;
import org.apache.lenya.cms.ac2.AccessControllerResolver;
import org.apache.lenya.cms.ac2.AccreditableManager;
import org.apache.lenya.cms.ac2.DefaultAccessController;
import org.apache.lenya.cms.ac2.Identity;

/**
 * 
 * @author egli
 * 
 */
public class AccessControlModule extends AbstractInputModule implements Serviceable {

    public static final String USER_ID = "user-id";
    public static final String USER_NAME = "user-name";
    public static final String USER_EMAIL = "user-email";
    public static final String IP_ADDRESS = "ip-address";

    public static final String USER_MANAGER = "user-manager";
    public static final String GROUP_MANAGER = "group-manager";
    public static final String ROLE_MANAGER = "role-manager";
    public static final String IP_RANGE_MANAGER = "iprange-manager";

    /**
      * The names of the AccessControlModule parameters.
      */
    public static final String[] PARAMETER_NAMES =
        {
            IP_ADDRESS,
            USER_ID,
            USER_NAME,
            USER_EMAIL,
            USER_MANAGER,
            GROUP_MANAGER,
            ROLE_MANAGER,
            IP_RANGE_MANAGER };

    /**
     *
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttribute(java.lang.String, org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
        throws ConfigurationException {

        Request request = ObjectModelHelper.getRequest(objectModel);
        Session session = request.getSession();
        Object value = null;

        if (session != null) {
            Identity identity = (Identity) session.getAttribute(Identity.class.getName());
            if (identity != null) {
                if (name.equals(USER_ID)) {
                    User user = identity.getUser();
                    if (user != null) {
                        value = user.getId();
                    }
                } else if (name.equals(USER_NAME)) {
                    User user = identity.getUser();
                    if (user != null) {
                        value = user.getName();
                    }
                } else if (name.equals(USER_EMAIL)) {
                    User user = identity.getUser();
                    if (user != null) {
                        value = user.getEmail();
                    }
                } else if (name.equals(IP_ADDRESS)) {
                    Machine machine = identity.getMachine();
                    if (machine != null) {
                        value = machine.getIp();
                    }
                }
            }
        }

        if (name.equals(USER_MANAGER)
            || name.equals(GROUP_MANAGER)
            || name.equals(ROLE_MANAGER)
            || name.equals(IP_RANGE_MANAGER)) {
            value = getItemManager(request, name);
        }

        return value;
    }

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeNames(org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Iterator getAttributeNames(Configuration modeConf, Map objectModel)
        throws ConfigurationException {
        return Arrays.asList(PARAMETER_NAMES).iterator();
    }

    /**
     * @see org.apache.cocoon.components.modules.input.InputModule#getAttributeValues(java.lang.String, org.apache.avalon.framework.configuration.Configuration, java.util.Map)
     */
    public Object[] getAttributeValues(String name, Configuration modeConf, Map objectModel)
        throws ConfigurationException {
        Object[] objects = { getAttribute(name, modeConf, objectModel)};

        return objects;
    }

    /**
     * Returns the item manager for a certain name.
     * @param request The request.
     * @param name The name of the manager ({@link #USER_MANAGER},
     * {@link #ROLE_MANAGER}, {@link #GROUP_MANAGER}, or {@link IP_RANGE_MANAGER}
     * @return An item manager.
     * @throws ConfigurationException when something went wrong.
     */
    protected ItemManager getItemManager(Request request, String name)
        throws ConfigurationException {
        AccessController accessController = null;
        ServiceSelector selector = null;
        AccessControllerResolver resolver = null;
        ItemManager itemManager = null;

        try {
            selector = (ServiceSelector) manager.lookup(AccessControllerResolver.ROLE + "Selector");
            resolver =
                (AccessControllerResolver) selector.select(
                    AccessControllerResolver.DEFAULT_RESOLVER);

            String requestURI = request.getRequestURI();
            String context = request.getContextPath();
            if (context == null) {
                context = "";
            }
            String url = requestURI.substring(context.length());
            accessController = resolver.resolveAccessController(url);

            AccreditableManager accreditableManager =
                ((DefaultAccessController) accessController).getAccreditableManager();

            if (name.equals(USER_MANAGER)) {
                itemManager = accreditableManager.getUserManager();
            } else if (name.equals(GROUP_MANAGER)) {
                itemManager = accreditableManager.getGroupManager();
            } else if (name.equals(ROLE_MANAGER)) {
                itemManager = accreditableManager.getRoleManager();
            } else if (name.equals(IP_RANGE_MANAGER)) {
                itemManager = accreditableManager.getIPRangeManager();
            }

        } catch (Exception e) {
            throw new ConfigurationException("Obtaining item manager failed: ", e);
        } finally {
            if (selector != null) {
                if (resolver != null) {
                    if (accessController != null) {
                        resolver.release(accessController);
                    }
                    selector.release(resolver);
                }
                manager.release(selector);
            }
        }

        return itemManager;
    }

    private ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}
