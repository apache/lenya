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

/**
 * Tab in a tabbed multiple-usecase environment.
 */
public interface Tab {

    /**
     * @return The name of the tab.
     */
    String getName();

    /**
     * @return The label.
     */
    public String getLabel();

    /**
     * @return The usecase which is displayed on the tab.
     */
    public String getUsecase();

    /**
     * @return The group this tab belongs to.
     */
    public String getGroup();
    
    /**
     * @return The names of the parameters to pass to the usecase upon invocation.
     */
    public String[] getParameterNames();
    
    /**
     * Returns the value of a certain parameter to pass to the usecase upon invocation. 
     * @param key The value.
     * @return A string.
     */
    public String getParameter(String key);
    
}
