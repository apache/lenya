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
package org.apache.lenya.cms.usecase;

import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.lenya.cms.usecase.gui.GUIManager;
import org.apache.lenya.cms.usecase.gui.Tab;

/**
 * Information about a usecase view.
 * 
 * @version $Id$
 * <p>
 * Example configuration:
 * <code><pre>&lt;view uri="/modules/foo/usecases/foo-mogrify.jx"
 *     customFlow="/modules/foo/flow/myflow.js"
 *     menu="false|true"
 *     createContinuation="false|true"
 * &gt;
 *   &lt;tab group="foo" name="bar"/&gt;      // optional
 *   &lt;parameter name="foo" value="bar/&gt; // optional
 * &lt;/view&gt;</pre></code>
 * </p>
 * <p>
 * <code>uri</code> is the relative URL of the page to be sent back to the client. If the URI
 * starts with a slash, it is resolved starting at the root sitemap, otherwise it
 * is resolved relative to the current sitemap. The URI should not contain a
 * scheme (such as cocoon:).
 * </p>
 * <p>
 * <code>menu</code> is a boolean that governs whether the Lenya GUI menu is displayed while
 * the usecase is running. The displaying of the menu is handled by the usecase.xmap sitemap;
 * hence this option is only functional if <code>uri</code> does <em>not</em> start with a slash
 * (or if you implement it yourself based on the <code>showMenu()</code> method of this object).<br>
 * Default is <em>false</em>.
 * </p>
 * <p>
 * <code>customFlow</code> is a javascript file where you can provide custom methods that will override
 * those in the default usecase handler (<code>modules-core/usecase/usecases.js</code>).
 * Currently, it provides support for "customLoopFlow" and "customSubmitFlow". Refer to the default handler
 * for function prototypes and more information.
 * NB: the "menu" and "createContinuation" attributes will have no effect when you use custom flow code, unless
 * you check for them and implement the respective functions yourself.
 * </p>
 * <p>
 * <code>createContinuation</code> can be set to false, in which case the generic flowscript
 * uses "sendPage" instead of "sendPageAndWait" and terminates after the view has been sent.
 * When <code>createContinuation</code> is false, you must not specify <code>submitFlow</code> 
 * or <code>loopFlow</code>.<br>
 * Default is <em>true</em>.
 * </p>
 * <p>
 * For tabbed usecases, you can optionally specify a tab group and name. Additional custom
 * configuration can be passed via the generic "parameter" element. 
 * </p>
 * <p>
 * For backwards compatibility with existing usecases, the constructor looks for a <code>template</code>
 * attribute if no <code>uri</code> is present. It is mapped to the same field, viewUri, internally.
 * </p>
 */
public class UsecaseView implements Configurable, Serviceable {

    protected static final String ATTRIBUTE_URI = "uri";
    protected static final String ATTRIBUTE_TEMPLATE = "template"; // backwards compatibility, mapped to "uri"

    protected static final String ATTRIBUTE_CUSTOM_FLOW = "customFlow";
    protected static final String ATTRIBUTE_SHOW_MENU = "menu";
    protected static final String ATTRIBUTE_CREATE_CONT = "createContinuation";

    // additional parameters:
    protected static final String ELEMENT_PARAMETER = "parameter";
    protected static final String ATTRIBUTE_NAME = "name";
    protected static final String ATTRIBUTE_VALUE = "value";

    // tabbed usecases:
    protected static final String ATTRIBUTE_GROUP = "group"; 
    protected static final String ELEMENT_TAB = "tab";


    private Map parameters = new HashMap();
    private ServiceManager manager;

    private String viewUri;
    private String customFlow;
    
    private boolean showMenu;
    private boolean createContinuation;
    private Tab tab;


    
    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration config) throws ConfigurationException {
        // get <view> attributes:
        this.viewUri = config.getAttribute(ATTRIBUTE_URI, "");
        if (this.viewUri == "") {
           // fall back to "template" attribute for backwards compatibility (rip out eventually).
           this.viewUri = config.getAttribute(ATTRIBUTE_TEMPLATE, "");
        }
        this.showMenu = config.getAttributeAsBoolean(ATTRIBUTE_SHOW_MENU, false);
        this.customFlow = config.getAttribute(ATTRIBUTE_CUSTOM_FLOW, "");
        this.createContinuation = config.getAttributeAsBoolean(ATTRIBUTE_CREATE_CONT, true);


        // get <tab/> configuration:
        Configuration tabConfig = config.getChild(ELEMENT_TAB, false);
        if (tabConfig != null) {
            String tabName = tabConfig.getAttribute(ATTRIBUTE_NAME);
            String tabGroup = tabConfig.getAttribute(ATTRIBUTE_GROUP);
            GUIManager guiMgr = null;
            try {
                guiMgr = (GUIManager) this.manager.lookup(GUIManager.ROLE);
                this.tab = guiMgr.getTab(tabGroup, tabName);
            } catch (ServiceException e) {
                throw new RuntimeException(e);
            } finally {
                if (guiMgr != null) {
                    this.manager.release(guiMgr);
                }
            }
        }

        // get <parameter/> configuration
        Configuration[] parameterConfigs = config.getChildren(ELEMENT_PARAMETER);
        for (int i = 0; i < parameterConfigs.length; i++) {
            String name = parameterConfigs[i].getAttribute(ATTRIBUTE_NAME);
            String value = parameterConfigs[i].getAttribute(ATTRIBUTE_VALUE);
            this.parameters.put(name, value);
        }

        checkConfig();

    }

    /**
     * @return The URI of the JX template;
     */
    public String getViewURI() {
        return this.viewUri;
    }

    /**
     * @return whether the menubar should be visible on usecase screens.
     */
    public boolean showMenu() {
        return this.showMenu;
    }

    /**
     * @return whether a continuation should be created.
     */
    public boolean createContinuation() {
        return this.createContinuation;
    }

    /**
     * @return the Flowscript snippet to be executed during the usecase view loop.
     */
    public String getCustomFlow() {
        return this.customFlow;
    }

    /**
     * @param name The parameter name.
     * @return The parameter value.
     */
    public String getParameter(String name) {
        return (String) this.parameters.get(name);
    }

    /**
     * @return The tab the usecase belongs to or <code>null</code>.
     */
    public Tab getTab() {
        return this.tab;
    }

    /**
     * @return All tabs in the same group.
     */
    public Tab[] getTabsInGroup() {
        if (getTab() == null) {
            return null;
        } else {
            GUIManager guiMgr = null;
            try {
                guiMgr = (GUIManager) this.manager.lookup(GUIManager.ROLE);
                return guiMgr.getActiveTabs(getTab().getGroup());
            } catch (ServiceException e) {
                throw new RuntimeException(e);
            } finally {
                if (guiMgr != null) {
                    this.manager.release(guiMgr);
                }
            }
        }
    }

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    private void checkConfig() throws ConfigurationException {
        if (this.tab != null && this.viewUri == "") {
            throw new ConfigurationException("When you specify a <tab/>, you must specify a <view uri=\"..\"/> as well!");
        }
    }
}
