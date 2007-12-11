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
package org.apache.lenya.workflow;

import java.util.Date;

/**
 * A version of the workflow history.
 * 
 * @version $Id$
 */
public interface Version {
    
    /**
     * Returns the event.
     * @return An event.
     */
    String getEvent();

    /**
     * Returns the state.
     * @return A state.
     */
    String getState();
    

    /**
     * Returns the date.
     * @return A string.
     */
    Date getDate();

    /**
     * Sets the date.
     * @param _date A date.
     */
    void setDate(Date _date);

    /**
     * Returns the user ID.
     * @return A string.
     */
    public String getUserId();

    /**
     * Sets the user ID.
     * @param _userId A user ID.
     */
    public void setUserId(String _userId);

    /**
     * Returns the ip address.
     * @return A string.
     */
    public String getIPAddress();

    /**
     * Sets the ip address.
     * @param _ipaddress A ip address.
     */
    public void setIPAddress(String _ipaddress);
    
    /**
     * Returns the value of a variable.
     * @param variableName The variable name.
     * @return A boolean value.
     */
    boolean getValue(String variableName);

    /**
     * Sets a variable value.
     * @param variableName The variable name.
     * @param value The value.
     */
    void setValue(String variableName, boolean value);
    
}