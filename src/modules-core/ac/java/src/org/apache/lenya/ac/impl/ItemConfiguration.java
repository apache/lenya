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

/* $Id$  */

package org.apache.lenya.ac.impl;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.lenya.ac.AccessControlException;

/**
 * Use this class to create configurations from {@link AbstractItem}s or
 * to build {@link AbstractItem}s from configurations.
 */
public class ItemConfiguration {
    
    /**
     * Ctor.
     */
    public ItemConfiguration() {
	    // do nothing
    }
    
    /**
     * Saves the ID, name and description of the Manageable to the configuration.
     * @param manageable A manageable.
     * @param configuration A configuration.
     */
    public void save(AbstractItem manageable, DefaultConfiguration configuration) {
        configuration.setAttribute(ATTRIBUTE_CLASS, manageable.getClass().getName());
        configuration.setAttribute(ATTRIBUTE_ID, manageable.getId());

        DefaultConfiguration child = null;

        // add name node
        child = new DefaultConfiguration(ELEMENT_NAME);
        child.setValue(manageable.getName());
        configuration.addChild(child);

        // add description node
        child = new DefaultConfiguration(ELEMENT_DESCRIPTION);
        child.setValue(manageable.getDescription());
        configuration.addChild(child);

    }

    protected static final String ELEMENT_NAME = "name";
    protected static final String ELEMENT_DESCRIPTION = "description";
    protected static final String ATTRIBUTE_ID = "id";
    protected static final String ATTRIBUTE_CLASS = "class";

    /**
     * Configures a Manageable.
     * @param manageable The manageable.
     * @param configuration The configuration.
     * @throws ConfigurationException when something went wrong.
     */    
    public void configure(AbstractItem manageable, Configuration configuration) throws ConfigurationException {
        manageable.setId(configuration.getAttribute(ATTRIBUTE_ID));
        manageable.setName(configuration.getChild(ELEMENT_NAME).getValue(""));
        manageable.setDescription(configuration.getChild(ELEMENT_DESCRIPTION).getValue(""));
    }

    /**
     * Returns the class name of an item.
     * @param config The item configuration.
     * @return The class name.
     * @throws AccessControlException when something went wrong.
     */
    public static String getItemClass(Configuration config) throws AccessControlException {
        String klass = null;

        try {
            klass = config.getAttribute(ItemConfiguration.ATTRIBUTE_CLASS);
        } catch (ConfigurationException e) {
            String errorMsg =
                "Exception when extracting class name from identity file: "
                    + klass
                    + config.getAttributeNames();
            throw new AccessControlException(errorMsg, e);
        }
        return klass;
    }

}
