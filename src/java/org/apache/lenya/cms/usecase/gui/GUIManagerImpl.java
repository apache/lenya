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
package org.apache.lenya.cms.usecase.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccessController;
import org.apache.lenya.ac.AccessControllerResolver;
import org.apache.lenya.ac.Authorizer;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.impl.DefaultAccessController;
import org.apache.lenya.ac.impl.PolicyAuthorizer;
import org.apache.lenya.cms.ac.usecase.UsecaseAuthorizer;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.cms.usecase.Usecase;
import org.apache.lenya.cms.usecase.UsecaseMessage;
import org.apache.lenya.cms.usecase.UsecaseResolver;
import org.apache.lenya.util.ServletHelper;
import org.xml.sax.SAXException;

/**
 * GUI manager implementation.
 */
public class GUIManagerImpl extends AbstractLogEnabled implements GUIManager, Configurable,
        Serviceable, Contextualizable {

    protected static final String ELEMENT_PARAMETER = "parameter";
    protected static final String ELEMENT_TAB_GROUP = "tab-group";
    protected static final String ELEMENT_TAB = "tab";
    protected static final String ATTRIBUTE_NAME = "name";
    protected static final String ATTRIBUTE_VALUE = "value";
    protected static final String ATTRIBUTE_LABEL = "label";
    protected static final String ATTRIBUTE_USECASE = "usecase";

    private Map name2group = new HashMap();

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration config) throws ConfigurationException {
        Configuration[] tabGroupConfigs = config.getChildren(ELEMENT_TAB_GROUP);
        for (int i = 0; i < tabGroupConfigs.length; i++) {
            String groupName = tabGroupConfigs[i].getAttribute(ATTRIBUTE_NAME);
            List group = new ArrayList();
            Configuration[] tabConfigs = tabGroupConfigs[i].getChildren(ELEMENT_TAB);
            for (int j = 0; j < tabConfigs.length; j++) {
                String name = tabConfigs[j].getAttribute(ATTRIBUTE_NAME);
                String label = tabConfigs[j].getAttribute(ATTRIBUTE_LABEL);
                String usecase = tabConfigs[j].getAttribute(ATTRIBUTE_USECASE);
                Tab tab = new Tab(groupName, name, usecase, label);
                group.add(tab);

                Configuration[] paramConfigs = tabConfigs[j].getChildren(ELEMENT_PARAMETER);
                for (int p = 0; p < paramConfigs.length; p++) {
                    String paramName = paramConfigs[p].getAttribute(ATTRIBUTE_NAME);
                    String paramValue = paramConfigs[p].getAttribute(ATTRIBUTE_VALUE);
                    tab.setParameter(paramName, paramValue);
                }
            }
            name2group.put(groupName, group.toArray(new Tab[group.size()]));
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.gui.GUIManager#getActiveTabs(java.lang.String)
     */
    public Tab[] getActiveTabs(String group) {
        Tab[] tabs = getTabs(group);
        List activeTabs = new ArrayList();
        for (int i = 0; i < tabs.length; i++) {
            if (getErrorMessages(tabs[i]).length == 0) {
                activeTabs.add(tabs[i]);
            }
        }
        return (Tab[]) activeTabs.toArray(new Tab[activeTabs.size()]);
    }

    protected Tab[] getTabs(String group) {
        Tab[] tabs = (Tab[]) name2group.get(group);
        return tabs;
    }

    /**
     * @see org.apache.lenya.cms.usecase.gui.GUIManager#getTab(java.lang.String, java.lang.String)
     */
    public Tab getTab(String group, String name) {
        Tab[] tabs = getTabs(group);
        for (int i = 0; i < tabs.length; i++) {
            if (name.equals(tabs[i].getName())) {
                return tabs[i];
            }
        }
        throw new IllegalArgumentException("No tab [" + name + "] in group [" + group + "]");
    }

    /**
     * Checks if a tab's usecase can be executed.
     * @param tab The tab.
     * @return An array of error messages.
     */
    protected UsecaseMessage[] getErrorMessages(Tab tab) {

        UsecaseMessage[] messages;

        ServiceSelector selector = null;
        AccessControllerResolver acResolver = null;
        AccessController accessController = null;
        UsecaseResolver usecaseResolver = null;
        UsecaseAuthorizer authorizer = null;
        try {

            selector = (ServiceSelector) this.manager.lookup(AccessControllerResolver.ROLE
                    + "Selector");
            acResolver = (AccessControllerResolver) selector
                    .select(AccessControllerResolver.DEFAULT_RESOLVER);
            accessController = acResolver.resolveAccessController(this.webappUrl);

            if (accessController instanceof DefaultAccessController) {
                DefaultAccessController defaultAccessController = (DefaultAccessController) accessController;
                Authorizer[] authorizers = defaultAccessController.getAuthorizers();
                for (int i = 0; i < authorizers.length; i++) {
                    if (authorizers[i] instanceof UsecaseAuthorizer) {
                        authorizer = (UsecaseAuthorizer) authorizers[i];
                    }
                }
            }

            usecaseResolver = (UsecaseResolver) this.manager.lookup(UsecaseResolver.ROLE);

            // filter item if usecase not allowed
            if (tab.getUsecase() != null) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Found usecase [" + tab.getUsecase() + "]");
                }
                PublicationFactory factory = PublicationFactory.getInstance(getLogger());
                Publication pub = factory.getPublication(this.manager, this.webappUrl);
                if (!authorizer.authorizeUsecase(tab.getUsecase(), this.roles, pub)) {
                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug("Usecase not authorized");
                    }
                    messages = new UsecaseMessage[1];
                    messages[0] = new UsecaseMessage("Access denied", null);
                }
            }

            if (usecaseResolver.isRegistered(tab.getUsecase())) {
                Usecase usecase = null;
                try {
                    usecase = usecaseResolver.resolve(tab.getUsecase());
                    usecase.setSourceURL(this.webappUrl);
                    usecase.setName(tab.getUsecase());
                    String[] keys = tab.getParameterNames();
                    for (int i = 0; i < keys.length; i++) {
                        usecase.setParameter(keys[i], tab.getParameter(keys[i]));
                    }
                    usecase.checkPreconditions();
                    if (usecase.hasErrors()) {
                        if (getLogger().isDebugEnabled()) {
                            getLogger().debug("Usecase preconditions not complied");
                        }

                        List msgs = usecase.getErrorMessages();
                        messages = (UsecaseMessage[]) msgs.toArray(new UsecaseMessage[msgs.size()]);
                    } else {
                        messages = new UsecaseMessage[0];
                    }
                } finally {
                    if (usecase != null) {
                        usecaseResolver.release(usecase);
                    }
                }
            } else {
                messages = new UsecaseMessage[1];
                messages[0] = new UsecaseMessage(
                        "Usecase [" + tab.getUsecase() + "] is not registered!", null);
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (usecaseResolver != null) {
                this.manager.release(usecaseResolver);
            }
            if (selector != null) {
                if (acResolver != null) {
                    if (accessController != null) {
                        acResolver.release(accessController);
                    }
                    selector.release(acResolver);
                }
                this.manager.release(selector);
            }
        }

        return messages;
    }

    private ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    private String webappUrl;
    private Role[] roles;

    /**
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    public void contextualize(Context context) throws ContextException {
        Request request = ContextHelper.getRequest(context);
        try {
            this.roles = PolicyAuthorizer.getRoles(request);
        } catch (AccessControlException e) {
            throw new ContextException("Obtaining roles failed: ", e);
        }
        this.webappUrl = ServletHelper.getWebappURI(request);
    }

}
