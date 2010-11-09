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
import java.util.Properties;

import org.apache.lenya.cms.usecase.gui.GUIManager;
import org.apache.lenya.cms.usecase.gui.Tab;

/**
 * Information about a usecase view.
 * 
 * @version $Id$
 *          <p>
 *          Example configuration: <code><pre>&lt;view uri="/modules/foo/usecases/foo-mogrify.jx"
 *     customFlow="/modules/foo/flow/myflow.js"
 *     menu="false|true"
 *     createContinuation="false|true"
 * &gt;
 *   &lt;tab group="foo" name="bar"/&gt;      // optional
 *   &lt;parameter name="foo" value="bar/&gt; // optional
 * &lt;/view&gt;</pre></code>
 *          </p>
 *          <p>
 *          <code>uri</code> is the relative URL of the page to be sent back to the client. If the
 *          URI starts with a slash, it is resolved starting at the root sitemap, otherwise it is
 *          resolved relative to the current sitemap. The URI should not contain a scheme (such as
 *          cocoon:).
 *          </p>
 *          <p>
 *          <code>menu</code> is a boolean that governs whether the Lenya GUI menu is displayed
 *          while the usecase is running. The displaying of the menu is handled by the usecase.xmap
 *          sitemap; hence this option is only functional if <code>uri</code> does <em>not</em>
 *          start with a slash (or if you implement it yourself based on the <code>showMenu()</code>
 *          method of this object).<br>
 *          Default is <em>false</em>.
 *          </p>
 *          <p>
 *          <code>customFlow</code> is a javascript file where you can provide custom methods that
 *          will override those in the default usecase handler (
 *          <code>modules-core/usecase/usecases.js</code>). Currently, it provides support for
 *          "customLoopFlow" and "customSubmitFlow". Refer to the default handler for function
 *          prototypes and more information. NB: the "menu" and "createContinuation" attributes will
 *          have no effect when you use custom flow code, unless you check for them and implement
 *          the respective functions yourself.
 *          </p>
 *          <p>
 *          <code>createContinuation</code> can be set to false, in which case the generic
 *          flowscript uses "sendPage" instead of "sendPageAndWait" and terminates after the view
 *          has been sent. When <code>createContinuation</code> is false, you must not specify
 *          <code>submitFlow</code> or <code>loopFlow</code>.<br>
 *          Default is <em>true</em>.
 *          </p>
 *          <p>
 *          For tabbed usecases, you can optionally specify a tab group and name. Additional custom
 *          configuration can be passed via the generic "parameter" element.
 *          </p>
 *          <p>
 *          For backwards compatibility with existing usecases, the constructor looks for a
 *          <code>template</code> attribute if no <code>uri</code> is present. It is mapped to the
 *          same field, viewUri, internally.
 *          </p>
 */
public class UsecaseViewImpl implements UsecaseView {

    protected static final String ATTRIBUTE_URI = "uri";
    protected static final String ATTRIBUTE_TEMPLATE = "template"; // backwards compatibility,
    // mapped to "uri"

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

    private String viewUri;
    private String customFlow;

    private boolean showMenu;
    private boolean createContinuation;
    private Tab tab;
    private GUIManager guiManager;
    private String tabName;
    private String tabGroup;

    /* (non-Javadoc)
		 * @see org.apache.lenya.cms.usecase.UsecaseView#getViewURI()
		 */
    public String getViewURI() {
        return this.viewUri;
    }

    /* (non-Javadoc)
		 * @see org.apache.lenya.cms.usecase.UsecaseView#showMenu()
		 */
    public boolean showMenu() {
        return this.showMenu;
    }

    /* (non-Javadoc)
		 * @see org.apache.lenya.cms.usecase.UsecaseView#createContinuation()
		 */
    public boolean createContinuation() {
        return this.createContinuation;
    }

    /* (non-Javadoc)
		 * @see org.apache.lenya.cms.usecase.UsecaseView#getCustomFlow()
		 */
    public String getCustomFlow() {
        return this.customFlow;
    }

    /* (non-Javadoc)
		 * @see org.apache.lenya.cms.usecase.UsecaseView#getParameter(java.lang.String)
		 */
    public String getParameter(String name) {
        return (String) this.parameters.get(name);
    }

    /* (non-Javadoc)
		 * @see org.apache.lenya.cms.usecase.UsecaseView#getTab()
		 */
    public Tab getTab() {
        return this.tab;
    }

    /* (non-Javadoc)
		 * @see org.apache.lenya.cms.usecase.UsecaseView#getTabsInGroup()
		 */
    public Tab[] getTabsInGroup() {
        if (getTab() == null) {
            return null;
        } else {
            return getGuiManager().getActiveTabs(getTab().getGroup());
        }
    }

    /* (non-Javadoc)
		 * @see org.apache.lenya.cms.usecase.UsecaseView#initialize()
		 */
    public void initialize() {
        if (this.tab != null && this.viewUri == "") {
            throw new RuntimeException("When you specify a <tab/>, you must specify a <view uri=\"..\"/> as well!");
        }
        this.tab = getGuiManager().getTab(tabGroup, tabName);
    }

    /* (non-Javadoc)
		 * @see org.apache.lenya.cms.usecase.UsecaseView#getGuiManager()
		 */
    public GUIManager getGuiManager() {
        return guiManager;
    }

    /* (non-Javadoc)
		 * @see org.apache.lenya.cms.usecase.UsecaseView#setGuiManager(org.apache.lenya.cms.usecase.gui.GUIManager)
		 */
    public void setGuiManager(GUIManager guiManager) {
        this.guiManager = guiManager;
    }
    
    /* (non-Javadoc)
		 * @see org.apache.lenya.cms.usecase.UsecaseView#setUri(java.lang.String)
		 */
    public void setViewUri(String uri) {
        this.viewUri = uri;
    }

    /* (non-Javadoc)
		 * @see org.apache.lenya.cms.usecase.UsecaseView#setShowMenu(boolean)
		 */
    public void setShowMenu(boolean showMenu) {
        this.showMenu = showMenu;
    }

    /* (non-Javadoc)
		 * @see org.apache.lenya.cms.usecase.UsecaseView#setCreateContinuation(boolean)
		 */
    public void setCreateContinuation(boolean createContinuation) {
        this.createContinuation = createContinuation;
    }

    /* (non-Javadoc)
		 * @see org.apache.lenya.cms.usecase.UsecaseView#setTabName(java.lang.String)
		 */
    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    /* (non-Javadoc)
		 * @see org.apache.lenya.cms.usecase.UsecaseView#setTabGroup(java.lang.String)
		 */
    public void setTabGroup(String tabGroup) {
        this.tabGroup = tabGroup;
    }
    
    /* (non-Javadoc)
		 * @see org.apache.lenya.cms.usecase.UsecaseView#setCustomFlow(java.lang.String)
		 */
    public void setCustomFlow(String customFlow) {
        this.customFlow = customFlow;
    }
    
    /* (non-Javadoc)
		 * @see org.apache.lenya.cms.usecase.UsecaseView#setParameters(java.util.Properties)
		 */
    public void setParameters(Properties params) {
        this.parameters = params;
    }

}
