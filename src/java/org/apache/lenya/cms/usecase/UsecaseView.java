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
 */
public class UsecaseView implements Configurable, Serviceable {

    protected static final String ATTRIBUTE_TEMPLATE_URI = "template";
    protected static final String ATTRIBUTE_SHOW_MENU = "menu";
    protected static final String ELEMENT_PARAMETER = "parameter";
    protected static final String ATTRIBUTE_NAME = "name";
    protected static final String ATTRIBUTE_VALUE = "value";
    
    protected static final String ATTRIBUTE_TYPE = "type";
    protected static final String VIEW_CFORM = "cforms";
    
    protected static final String ELEMENT_CFORM_DEFINITION = "definition";
    protected static final String ELEMENT_CFORM_BINDING = "binding";
    
    protected static final String ATTRIBUTE_URI = "uri";
    protected static final String ATTRIBUTE_GROUP = "group";
    protected static final String ELEMENT_TAB = "tab";

    private Map parameters = new HashMap();

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration config) throws ConfigurationException {
        this.templateUri = config.getAttribute(ATTRIBUTE_TEMPLATE_URI, null);
        this.viewUri = config.getAttribute(ATTRIBUTE_URI, null);
        
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

        if (this.viewUri == null && this.templateUri == null) {
            throw new ConfigurationException("Either uri or template attribute must be declared!");
        }

        this.showMenu = config.getAttributeAsBoolean(ATTRIBUTE_SHOW_MENU, false);
        
        this.viewType = config.getAttribute(ATTRIBUTE_TYPE, null);
        
        if (this.viewType!=null && this.viewType.equals(VIEW_CFORM)){
            Configuration cformDefinitionConfig = config.getChild(ELEMENT_CFORM_DEFINITION, false);
            if (cformDefinitionConfig!=null){
                this.cformDefinition = cformDefinitionConfig.getAttribute(ATTRIBUTE_TEMPLATE_URI, null);
            }
            Configuration cformBindingConfig = config.getChild(ELEMENT_CFORM_BINDING, false);
            if (cformBindingConfig!=null){
                this.cformBinding = cformBindingConfig.getAttribute(ATTRIBUTE_TEMPLATE_URI, null);
            }
        }

        Configuration[] parameterConfigs = config.getChildren(ELEMENT_PARAMETER);
        for (int i = 0; i < parameterConfigs.length; i++) {
            String name = parameterConfigs[i].getAttribute(ATTRIBUTE_NAME);
            String value = parameterConfigs[i].getAttribute(ATTRIBUTE_VALUE);
            this.parameters.put(name, value);
        }
    }

    private String templateUri;

    /**
     * @return The URI of the JX template;
     */
    public String getTemplateURI() {
        return this.templateUri;
    }

    private String viewUri;

    /**
     * @return The URI of the JX template;
     */
    public String getViewURI() {
        return this.viewUri;
    }

    private boolean showMenu;

    /**
     * @return If the menubar should be visible on usecase screens.
     */
    public boolean showMenu() {
        return this.showMenu;
    }

    /**
     * @param name The parameter name.
     * @return The parameter value.
     */
    public String getParameter(String name) {
        return (String) this.parameters.get(name);
    }

    private Tab tab;

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

    private ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    private String cformDefinition =null;

	/**
	 * @return Returns the cformDefinition.
	 */
	public String getCformDefinition() {
		return cformDefinition;
	}

	/**
	 * @param cformDefinition The cformDefinition to set.
	 */
	public void setCformDefinition(String cformDefinition) {
		this.cformDefinition = cformDefinition;
	}
	
    
	private String cformBinding =null;
    

    /**
	 * @return Returns the cformBinding.
	 */
	public String getCformBinding() {
		return cformBinding;
	}

	/**
	 * @param cformBinding The cformBinding to set.
	 */
	public void setCformBinding(String cformBinding) {
		this.cformBinding = cformBinding;
	}
	
    private String viewType;
    
    /**
     * @return Returns the viewType.
     */
    public String getViewType() {
        return viewType;
    }

    /**
     * @param viewType The viewType to set.
     */
    public void setViewType(String viewType) {
        this.viewType = viewType;
    }

}