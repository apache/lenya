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

/* $Id: Item.java,v 1.3 2004/03/03 12:56:31 gregor Exp $  */

package org.apache.lenya.ac;

import java.io.File;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;


/**
 * An item can be initialized from a configuration.
 */
public interface Item {
    
    /**
     * Returns the ID.
     * @return A string.
     */
    String getId();
    
    /**
     * Returns the name.
     * @return A string.
     */
    String getName();
    
    /**
     * Sets the name.
     * @param name A string.
     */
    void setName(String name);
    
    /**
     * Returns the description.
     * @return A string.
     */
    String getDescription();

    /**
     * Sets the description.
     * @param description A string.
     */
    void setDescription(String description);
    
    /**
     * Sets the configuration directory of this item.
     * @param configurationDirectory The configuration directory.
     */
    void setConfigurationDirectory(File configurationDirectory);

    /**
     * Configures this item.
     * @param configuration The configuration.
     * @throws ConfigurationException when something went wrong.
     */
    void configure(Configuration configuration) throws ConfigurationException;
    
}
