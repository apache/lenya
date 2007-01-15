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
package org.apache.lenya.cms.usecase.gui.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.lenya.cms.usecase.gui.Tab;

/**
 * Tab implementation.
 */
public class TabImpl implements Tab {

    /**
     * Ctor.
     * @param group The name of the usecase group.
     * @param name The name of the tab.
     * @param usecase The usecase to be displayed.
     * @param label The label to be displayed on the tab.
     */
    public TabImpl(String group, String name, String usecase, String label) {
        this.name = name;
        this.group = group;
        this.usecase = usecase;
        this.label = label;
    }
    
    private Map parameters = new HashMap();

    private String name;

    void setParameter(String name, String value) {
        this.parameters.put(name, value);
    }
    
    public String getName() {
        return this.name;
    }

    private String usecase;

    private String label;

    public String getLabel() {
        return label;
    }

    public String getUsecase() {
        return usecase;
    }

    private String group;

    public String getGroup() {
        return this.group;
    }
    
    public String[] getParameterNames() {
        Set keys = this.parameters.keySet();
        return (String[]) keys.toArray(new String[keys.size()]);
    }
    
    public String getParameter(String key) {
        return (String) this.parameters.get(key);
    }
    
}
