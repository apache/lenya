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
package org.apache.lenya.ac.saml;

import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.opensaml.SAMLAttribute;

/**
 * User fields mapper.
 */
public class UserFieldsMapper {

    private Map samlAttributes;

    private UserFieldsMapping mapping;

    private ServiceManager manager;

    protected UserFieldsMapping getMapping() {
        if (this.mapping == null) {
            try {
                this.mapping = (UserFieldsMapping) this.manager.lookup(UserFieldsMapping.ROLE);
            } catch (ServiceException e) {
                throw new RuntimeException(e);
            }
        }
        return this.mapping;
    }

    /**
     * @param manager The service manager.
     * @param samlAttributes The mapping.
     */
    public UserFieldsMapper(ServiceManager manager, Map samlAttributes) {
        this.manager = manager;
        this.samlAttributes = samlAttributes;
    }

    /**
     * @return First Name value from shibboleth attributes.
     */
    public String getFirstName() {
        return getAttributeValueFromMap(getMapping().getFirstNameAttribute());
    }

    /**
     * @return Last Name value from shibboleth attributes.
     */
    public String getLastName() {
        return getAttributeValueFromMap(getMapping().getLastNameAttribute());
    }

    /**
     * @return EMail value from shibboleth attributes.
     */
    public String getEMail() {
        return getAttributeValueFromMap(getMapping().getEMailAttribute());
    }

    private String getAttributeValueFromMap(String attributeName) {
        if (attributeName == null) {
            throw new IllegalArgumentException("The attribute name must not be null!");
        }
        SAMLAttribute samlAttr = (SAMLAttribute) this.samlAttributes.get(attributeName);
        if (samlAttr == null)
            return null;
        Iterator valuesIter = samlAttr.getValues();
        if (!valuesIter.hasNext())
            return null;
        return (String) valuesIter.next();
    }

}
