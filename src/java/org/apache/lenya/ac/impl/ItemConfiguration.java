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

/* $Id: ItemConfiguration.java,v 1.2 2004/03/03 12:56:33 gregor Exp $  */

package org.apache.lenya.ac.impl;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;

/**
 * Use this class to create configurations from {@link AbstractItem}s or
 * to build {@link AbstractItem}s from configurations.
 */
public class ItemConfiguration {
    
    /**
     * Ctor.
     */
    public ItemConfiguration() {
    }
    
    /**
     * Saves the ID, name and description of the Manageable to the configuration.
     * @param manageable A manageable.
     * @param configuration A configuration.
     */
    public void save(AbstractItem manageable, DefaultConfiguration configuration) {
        configuration.setAttribute(CLASS_ATTRIBUTE, manageable.getClass().getName());
        configuration.setAttribute(ID_ATTRIBUTE, manageable.getId());

        DefaultConfiguration child = null;

        // add name node
        child = new DefaultConfiguration(NAME);
        child.setValue(manageable.getName());
        configuration.addChild(child);

        // add description node
        child = new DefaultConfiguration(DESCRIPTION);
        child.setValue(manageable.getDescription());
        configuration.addChild(child);

    }

    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String ID_ATTRIBUTE = "id";
    public static final String CLASS_ATTRIBUTE = "class";

    /**
     * Configures a Manageable.
     * @param manageable The manageable.
     * @param configuration The configuration.
     * @throws ConfigurationException when something went wrong.
     */    
    public void configure(AbstractItem manageable, Configuration configuration) throws ConfigurationException {
        manageable.setId(configuration.getAttribute(ID_ATTRIBUTE));
        manageable.setName(configuration.getChild(NAME).getValue(""));
        manageable.setDescription(configuration.getChild(DESCRIPTION).getValue(""));
    }

}
