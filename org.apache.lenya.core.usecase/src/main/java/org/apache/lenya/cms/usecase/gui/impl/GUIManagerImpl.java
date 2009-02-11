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
package org.apache.lenya.cms.usecase.gui.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.cocoon.processing.ProcessInfoProvider;
import org.apache.cocoon.spring.configurator.WebAppContextUtils;
import org.apache.cocoon.util.AbstractLogEnabled;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccessController;
import org.apache.lenya.ac.AccessControllerResolver;
import org.apache.lenya.ac.Authorizer;
import org.apache.lenya.ac.Role;
import org.apache.lenya.cms.ac.PolicyUtil;
import org.apache.lenya.cms.ac.usecase.UsecaseAuthorizer;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.Repository;
import org.apache.lenya.cms.publication.Session;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.usecase.Usecase;
import org.apache.lenya.cms.usecase.UsecaseMessage;
import org.apache.lenya.cms.usecase.UsecaseResolver;
import org.apache.lenya.cms.usecase.gui.GUIManager;
import org.apache.lenya.cms.usecase.gui.Tab;
import org.apache.lenya.util.ServletHelper;

/**
 * GUI manager implementation.
 */
public class GUIManagerImpl extends AbstractLogEnabled implements GUIManager {

    protected static final String ELEMENT_PARAMETER = "parameter";
    protected static final String ELEMENT_TAB_GROUP = "tab-group";
    protected static final String ELEMENT_TAB = "tab";
    protected static final String ATTRIBUTE_NAME = "name";
    protected static final String ATTRIBUTE_VALUE = "value";
    protected static final String ATTRIBUTE_LABEL = "label";
    protected static final String ATTRIBUTE_USECASE = "usecase";

    private Map name2group = new HashMap();

    private UsecaseResolver usecaseResolver;
    private Repository repository;

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

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
                TabImpl tab = new TabImpl(groupName, name, usecase, label);
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
        UsecaseAuthorizer authorizer = null;

        String usecaseName = tab.getUsecase();
        if (usecaseName == null) {
            throw new IllegalArgumentException("The usecase name of the tab [" + tab.getName()
                    + "] is null.");
        }

        try {

            AccessControllerResolver acResolver = (AccessControllerResolver) WebAppContextUtils
                    .getCurrentWebApplicationContext().getBean(AccessControllerResolver.ROLE);
            AccessController accessController = acResolver.resolveAccessController(this.webappUrl);

            Authorizer[] authorizers = accessController.getAuthorizers();
            for (int i = 0; i < authorizers.length; i++) {
                if (authorizers[i] instanceof UsecaseAuthorizer) {
                    authorizer = (UsecaseAuthorizer) authorizers[i];
                }
            }

            // filter item if usecase not allowed
            if (tab.getUsecase() != null) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Found usecase [" + tab.getUsecase() + "]");
                }

                HttpServletRequest request = getRequest();
                Session session = this.repository.getSession(request);
                String pubId = new URLInformation(this.webappUrl).getPublicationId();
                Publication pub = session.getPublication(pubId);
                if (!authorizer.authorizeUsecase(usecaseName, getRoles(), pub)) {
                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug("Usecase not authorized");
                    }
                    messages = new UsecaseMessage[1];
                    messages[0] = new UsecaseMessage("Access denied");
                }
            }

            if (usecaseResolver.isRegistered(this.webappUrl, usecaseName)) {
                Usecase usecase = null;
                try {
                    usecase = usecaseResolver.resolve(this.webappUrl, usecaseName);
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
                String[] params = {};
                messages[0] = new UsecaseMessage(
                        "Usecase [" + usecaseName + "] is not registered!", params);
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

        return messages;
    }

    protected HttpServletRequest getRequest() {
        ProcessInfoProvider process = (ProcessInfoProvider) WebAppContextUtils
                .getCurrentWebApplicationContext().getBean(ProcessInfoProvider.ROLE);
        HttpServletRequest request = process.getRequest();
        return request;
    }

    private String webappUrl;
    private Role[] roles;

    protected Role[] getRoles() throws AccessControlException {
        if (this.roles == null) {
            HttpServletRequest request = getRequest();
            this.roles = PolicyUtil.getRoles(getRequest());
            this.webappUrl = ServletHelper.getWebappURI(request);
        }
        return this.roles;
    }

    public void setUsecaseResolver(UsecaseResolver usecaseResolver) {
        this.usecaseResolver = usecaseResolver;
    }

    public UsecaseResolver getUsecaseResolver() {
        return usecaseResolver;
    }

}
