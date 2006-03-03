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
 * Parameter
 */
public class Parameter {

    private String name;
    private String defaultValue;
    private String localValue;

    /**
     *
     */
    public void setName(String name) { 
        this.name = name;
    }

    /**
     *
     */
    public String getName() {
        return name;
    }

    /**
     *
     */
    public void setDefaultValue(String value) { 
        this.defaultValue = value;
    }

    /**
     *
     */
    public String getDefaultValue() { 
        return defaultValue;
    }

    /**
     *
     */
    public void setLocalValue(String value) { 
        this.localValue = value;
    }

    /**
     *
     */
    public String getLocalValue() { 
        return localValue;
    }

    /**
     *
     */
    public boolean test(String value) {
        // No tests are being executed!
        return true;
    }

    /**
     *
     */
    public String getAvailableValues() {
        return null;
    }

    /**
     *
     */
    public String toString() { 
        return name + ":::" + defaultValue + ":::" + localValue;
    }
}
