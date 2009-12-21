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

package org.apache.lenya.workflow.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.lenya.workflow.Version;

/**
 * A version of the workflow history.
 */
public class VersionImpl implements Version {

    private Date date;
    private String ipAddress;
    private String event;
    private String state;
    private String userId;
    private Map variableValues = new HashMap();
 
    /**
     * @see org.apache.lenya.workflow.Version#getEvent()
     */
    public String getEvent() {
        return this.event;
    }

    /**
     * @see org.apache.lenya.workflow.Version#getState()
     */
    public String getState() {
        return this.state;
    }
    
    /**
     * Returns the date.
     * @return A string.
     */
    public Date getDate() {
        return (Date)this.date.clone();
    }

    /**
     * Sets the date.
     * @param _date A date.
     */
    public void setDate(Date _date) {
        this.date = (Date)_date.clone();
    }

    /**
     * Returns the user ID.
     * @return A string.
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     * Sets the user ID.
     * @param _userId A user ID.
     */
    public void setUserId(String _userId) {
        this.userId = _userId;
    }

    /**
     * Returns the ip address.
     * @return A string.
     */
    public String getIPAddress() {
    	return this.ipAddress;
    }

    /**
     * Sets the ip address.
     * @param _ipaddress A ip address.
     */
    public void setIPAddress(String _ipaddress){
    	this.ipAddress = _ipaddress;
    }
    
    /**
     * Ctor.
     * @param _event The event that caused the version change.
     * @param _state The destination state.
     */
    public VersionImpl(String _event, String _state) {
        this.event = _event;
        this.state = _state;
    }

    /**
     * @see org.apache.lenya.workflow.Version#getValue(java.lang.String)
     */
    public boolean getValue(String variableName) {
        Boolean value = (Boolean) this.variableValues.get(variableName);
        if (value == null) {
            throw new RuntimeException("No value set for variable [" + variableName + "]");
        }
        return value.booleanValue();
    }

    /**
     * @see org.apache.lenya.workflow.Version#setValue(java.lang.String, boolean)
     */
    public void setValue(String variableName, boolean value) {
        this.variableValues.put(variableName, Boolean.valueOf(value));
    }

}
