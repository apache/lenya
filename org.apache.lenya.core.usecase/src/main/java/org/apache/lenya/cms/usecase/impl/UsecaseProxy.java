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
package org.apache.lenya.cms.usecase.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.usecase.Usecase;
import org.apache.lenya.cms.usecase.UsecaseView;

/**
 * Proxy which holds the parameters of a usecase. It is used to restore the usecase after the
 * flowscript is re-entered and to pass the usecase parameters to a JX template.
 * 
 * @version $Id$
 */
public class UsecaseProxy {

    private Map parameters = new HashMap();
    private String name;
    private String sourceUrl;
    private UsecaseView view;

    /**
     * Ctor.
     * @param usecase The usecase to extract the parameters from.
     */
    public UsecaseProxy(Usecase usecase) {
        this.name = usecase.getName();

        String[] names = usecase.getParameterNames();
        for (int i = 0; i < names.length; i++) {
            this.parameters.put(names[i], usecase.getParameter(names[i]));
        }

        this.errorMessages = usecase.getErrorMessages();
        this.infoMessages = usecase.getInfoMessages();
        this.sourceUrl = usecase.getSourceURL();
        this.view = usecase.getView();
    }

    /**
     * Initializes a usecase from this proxy.
     * @param usecase The usecase.
     */
    public void setup(Usecase usecase) {
        usecase.setName(this.name);
        usecase.setSourceURL(this.sourceUrl);
        usecase.setView(this.view);

        String[] names = getParameterNames();
        for (int i = 0; i < names.length; i++) {
            usecase.setParameter(names[i], this.parameters.get(names[i]));
        }
    }

    /**
     * Returns the current value of a parameter.
     * @param name The parameter name.
     * @return An object.
     */
    public Object getParameter(String name) {
        return this.parameters.get(name);
    }

    /**
     * Returns the current value of a parameter.
     * @param name The parameter name.
     * @param defaultValue The default value to use when the parameter is not set.
     * @return An object.
     */
    public Object getParameter(String name, Object defaultValue) {
        Object value = this.parameters.get(name);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    /**
     * Returns the current value of a parameter as a string.
     * @param name The parameter name.
     * @return A string or <code>null</code> if the parameter was not set.
     */
    public String getParameterAsString(String name) {
        String valueString = null;
        Object value = getParameter(name);
        if (value != null) {
            valueString = value.toString();
        }
        return valueString;
    }

    /**
     * @return The parameter names.
     */
    public String[] getParameterNames() {
        Set keys = this.parameters.keySet();
        return (String[]) keys.toArray(new String[keys.size()]);
    }

    private List errorMessages;
    private List infoMessages;

    /**
     * Returns the error messages from the previous operation. Error messages prevent the operation
     * from being executed.
     * @return A list of strings.
     */
    public List getErrorMessages() {
        return this.errorMessages;
    }

    /**
     * Returns the info messages from the previous operation. Info messages do not prevent the
     * operation from being executed.
     * @return A list of strings.
     */
    public List getInfoMessages() {
        return this.infoMessages;
    }

    /**
     * Determine if the usecase has error messages. Provides a way of checking for errors without
     * actually retrieving them.
     * @return true if the usecase resulted in error messages.
     */
    public boolean hasErrors() {
        boolean ret = false;
        if (this.errorMessages != null)
            ret = !this.errorMessages.isEmpty();
        return ret;
    }

    /**
     * Determine if the usecase has info messages. Provides a way of checking for info messages
     * without actually retrieving them.
     * @return true if the usecase resulted in info messages being generated.
     */
    public boolean hasInfoMessages() {
        boolean ret = false;
        if (this.infoMessages != null)
            ret = !this.infoMessages.isEmpty();
        return ret;
    }

    /**
     * @return The name of this usecase.
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return The view of the usecase.
     */
    public UsecaseView getView() {
        return this.view;
    }
    
    /**
     * @return The session of the usecase.
     */
    public Session getSession() {
        return (Session) getParameter("private.session");
    }

    /**
     * Returns one of the strings "true" or "false" depending on whether the
     * corresponding checkbox was checked.
     * @param name The parameter name.
     * @return A string.
     */
    public String getBooleanCheckboxParameter(String name) {
        String value = "false";
        String paramValue = getParameterAsString(name);
        if (paramValue != null && (paramValue.equals("on") || paramValue.equals("true"))) {
            value = "true";
        }
        return value;
    }
    
    /**
     * @return The source URL of the usecase.
     */
    public String getSourceURL() {
        return this.sourceUrl;
    }

    /**
     * Returns a parameter as integer. If the parameter does not exist, a default value is returned.
     * @param name The parameter name.
     * @param defaultValue The default value.
     * @return An integer.
     */
    public int getParameterAsInteger(String name, int defaultValue) {
        int valueInt = defaultValue;
        Object value = getParameter(name);
        if (value != null) {
            valueInt = Integer.valueOf(value.toString()).intValue();
        }
        return valueInt;
    }

}
