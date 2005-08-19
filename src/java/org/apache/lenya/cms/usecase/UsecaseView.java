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

/**
 * Information about a usecase view.
 * 
 * @version $Id$
 */
public class UsecaseView implements Configurable {
    
    protected static final String ATTRIBUTE_TEMPLATE_URI = "template";
    protected static final String ATTRIBUTE_SHOW_MENU = "menu";
    protected static final String ELEMENT_PARAMETER = "parameter";
    protected static final String ATTRIBUTE_NAME = "name";
    protected static final String ATTRIBUTE_VALUE = "value";
    protected static final String ATTRIBUTE_URI = "uri";
    
    private Map parameters = new HashMap();

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration config) throws ConfigurationException {
        this.templateUri = config.getAttribute(ATTRIBUTE_TEMPLATE_URI, null);
        this.viewUri = config.getAttribute(ATTRIBUTE_URI, null);
        
        if (this.viewUri == null && this.templateUri == null) {
            throw new ConfigurationException("Either uri or template attribute must be declared!");
        }
        
        this.showMenu = config.getAttributeAsBoolean(ATTRIBUTE_SHOW_MENU, false);
        
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
    
}