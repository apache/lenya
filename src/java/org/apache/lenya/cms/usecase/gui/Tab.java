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
package org.apache.lenya.cms.usecase.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Tab {

    public Tab(String group, String name, String usecase, String label) {
        this.name = name;
        this.group = group;
        this.usecase = usecase;
        this.label = label;
    }
    
    private Map parameters = new HashMap();

    private String name;

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
