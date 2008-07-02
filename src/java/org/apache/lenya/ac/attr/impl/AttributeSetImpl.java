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
package org.apache.lenya.ac.attr.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.lenya.ac.attr.Attribute;
import org.apache.lenya.ac.attr.AttributeSet;
import org.apache.lenya.util.Assert;

/**
 * SAML attribute translator implementation.
 */
public class AttributeSetImpl extends AbstractLogEnabled implements AttributeSet, Configurable {

    private static final String CONF_ATTRIBUTE = "Attribute";
    private static final String CONF_ATTRIBUTE_NAME = "name";
    private static final String CONF_ATTRIBUTE_ALIAS = "alias";
    private static final String CONF_ATTRIBUTE_DESCRIPTION = "description";

    /**
     * Separator for multiple values.
     */
    public static final String MULTIVALUE_SEPARATOR = ";";

    private Map name2attr;
    private String name;

    public void configure(Configuration attrTransConfig) throws ConfigurationException {
        this.name2attr = new HashMap();
        this.name = attrTransConfig.getAttribute(CONF_ATTRIBUTE_NAME);
        Configuration[] transList = attrTransConfig.getChildren();
        for (int i = 0; i < transList.length; i++) {
            if (!transList[i].getName().equals(CONF_ATTRIBUTE))
                throw new ConfigurationException("Invalid child config element: "
                        + transList[i].getName());
            String name = transList[i].getAttribute(CONF_ATTRIBUTE_NAME);
            String alias = transList[i].getAttribute(CONF_ATTRIBUTE_ALIAS, name);
            String descr = transList[i].getAttribute(CONF_ATTRIBUTE_DESCRIPTION, "");
            Attribute attr = new AttributeImpl(name, alias, descr);
            this.name2attr.put(name, attr);
        }
    }

    public Attribute getAttribute(String name) {
        Assert.isTrue("Attribute '" + name + "' exists", this.name2attr.containsKey(name));
        return (Attribute) this.name2attr.get(name);
    }

    public String[] getAttributeNames() {
        Set names = this.name2attr.keySet();
        return (String[]) names.toArray(new String[names.size()]);
    }

    public String getName() {
        return this.name;
    }

}
