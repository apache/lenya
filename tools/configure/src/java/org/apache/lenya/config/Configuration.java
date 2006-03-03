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

package org.apache.lenya.config;

/**
 * Configuration
 */
abstract public class Configuration {

    protected Parameter[] params;

    /**
     *
     */
    abstract public Parameter[] getParameters();

    /**
     *
     */
    abstract public Parameter[] getConfigurableParameters();

    /**
     *
     */
    public void setParameter(Parameter param) {
        for (int i = 0; i < params.length; i++) {
            if(param.getName().equals(params[i].getName())) {
                params[i] = param;
            }
        }
    }

    /**
     *
     */
    public Parameter getParameter(String name) {
        for (int i = 0; i < params.length; i++) {
            if(name.equals(params[i].getName())) {
                return params[i];
            }
        }
        return null;
    }

    /**
     *
     */
    abstract public void readDefault();

    /**
     *
     */
    abstract public void readLocal();

    /**
     * Read default and local and combine the two of them
     */
    abstract public void read();

    /**
     *
     */
    abstract public String getVersionDefault();

    /**
     *
     */
    abstract public String getVersionLocal();

    /**
     *
     */
    abstract public void writeLocal();

    /**
     *
     */
    abstract public boolean localConfigExists();
}
