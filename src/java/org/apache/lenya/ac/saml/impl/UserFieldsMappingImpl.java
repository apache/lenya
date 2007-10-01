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
package org.apache.lenya.ac.saml.impl;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.lenya.ac.saml.UserFieldsMapping;

/**
 * User fields mapping implementation.
 */
public class UserFieldsMappingImpl extends AbstractLogEnabled implements UserFieldsMapping,
        Configurable, ThreadSafe {
    
    private static final String CONF_FIRSTNAME = "FirstName";
    private static final String CONF_LASTNAME = "LastName";
    private static final String CONF_EMAIL = "EMail";

    // shibboleth registration mappings
    private String firstName;
    private String lastName;
    private String eMail;

    public void configure(Configuration userfieldMappingConfig) throws ConfigurationException {
        if (userfieldMappingConfig.getChildren().length > 0) { // optional
            firstName = userfieldMappingConfig.getChild(CONF_FIRSTNAME).getValue();
            lastName = userfieldMappingConfig.getChild(CONF_LASTNAME).getValue();
            eMail = userfieldMappingConfig.getChild(CONF_EMAIL).getValue();
        }
    }

    public String getFirstNameAttribute() {
        return this.firstName;
    }

    public String getLastNameAttribute() {
        return this.lastName;
    }

    public String getEMailAttribute() {
        return this.eMail;
    }

}
