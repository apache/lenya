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

/* $Id: ParameterWrapper.java,v 1.2 2004/03/01 16:18:20 gregor Exp $  */

package org.apache.lenya.cms.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.lenya.util.NamespaceMap;
import org.apache.log4j.Category;

public abstract class ParameterWrapper {
    
    private static Category log = Category.getInstance(ParameterWrapper.class);
    private NamespaceMap parameters;
    
    /**
     * Returns the un-prefixed parameters.
     * @return A map.
     */
    public Map getMap() {
        return parameters.getMap();
    }

    /**
     * Ctor.
     * @param prefixedParameters The prefixed parameters to wrap.
     */
    public ParameterWrapper(Map prefixedParameters) {
        parameters = new NamespaceMap(prefixedParameters, getPrefix());
    }
    
    /**
     * Returns the namespace prefix.
     * @return A string.
     */
    public abstract String getPrefix();
    
    /**
     * Adds a key-value pair. If the value is null, no pair is added.
     * @param key The key.
     * @param value The value.
     */
    public void put(String key, String value) {
        if (value != null) {
            log.debug("Setting parameter: [" + key + "] = [" + value + "]");
            parameters.put(key, value);
        }
        else {
            log.debug("Not setting parameter: [" + key + "] = [" + value + "]");
        }
    }
    
    /**
     * Returns the value for a key.
     * @param key The key.
     * @return The value.
     */
    public String get(String key) {
        return (String) parameters.get(key);
    }
    
    /**
     * Returns the required keys.
     * @return A string array.
     */
    protected abstract String[] getRequiredKeys();
    
    /**
     * Checks if this parameters object contains all necessary parameters.
     * @return A boolean value.
     */
    public boolean isComplete() {
        boolean complete = true;
        Map parameterMap = getMap();
        String[] requiredKeys = getRequiredKeys();
        int i = 0;
        while (complete && i < requiredKeys.length) {
            log.debug("Checking parameter: [" + requiredKeys[i] + "]");
            complete = complete && parameterMap.containsKey(requiredKeys[i]);
            log.debug("OK: [" + complete + "]");
            i++;
        }
        return complete;
    }

    /**
     * Returns the missing parameters parameters.
     * @return A string array.
     */
    public String[] getMissingKeys() {
        String[] requiredKeys = getRequiredKeys();
        Map parameterMap = getMap();
        List keyList = new ArrayList();
        for (int i = 0; i < requiredKeys.length; i++) {
            if (!parameterMap.containsKey(requiredKeys[i])) {
                keyList.add(requiredKeys[i]);
            }
        }
        return (String[]) keyList.toArray(new String[keyList.size()]);
    }
    
    /**
     * Parameterizes this wrapper with un-prefixed parameters.
     * @param parameters A parameters object.
     */
    public void parameterize(Parameters parameters) {
        String[] keys = parameters.getNames();
        for (int i = 0; i < keys.length; i++) {
            put(keys[i], parameters.getParameter(keys[i], null));
        }
    }
    
}
