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
package org.apache.lenya.cms.usecase.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Tab in a tabbed multiple-usecase environment.
 */
public class Tab {

    /**
     * Ctor.
     * @param group The name of the usecase group.
     * @param name The name of the tab.
     * @param usecase The usecase to be displayed.
     * @param label The label to be displayed on the tab.
     */
    public Tab(String group, String name, String usecase, String label) {
        this.name = name;
        this.group = group;
        this.usecase = usecase;
        this.label = label;
    }
    
    private Map parameters = new HashMap();

    private String name;

    /**
     * @return The name of the tab.
     */
    public String getName() {
        return this.name;
    }

    private String usecase;

    private String label;

    /**
     * @return The label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return The usecase which is displayed on the tab.
     */
    public String getUsecase() {
        return usecase;
    }

    private String group;

    /**
     * @return The group this tab belongs to.
     */
    public String getGroup() {
        return this.group;
    }
    
    void setParameter(String name, String value) {
        this.parameters.put(name, value);
    }
    
    String[] getParameterNames() {
        Set keys = this.parameters.keySet();
        return (String[]) keys.toArray(new String[keys.size()]);
    }
    
    String getParameter(String key) {
        return (String) this.parameters.get(key);
    }
    
}
