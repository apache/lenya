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
package org.apache.shibboleth.util.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.shibboleth.impl.ShibbolethModuleImpl;
import org.apache.shibboleth.util.AttributeTranslator;
import org.opensaml.SAMLAttribute;

/**
 * SAML attribute translator implementation.
 */
public class AttributeTranslatorImpl extends AbstractLogEnabled implements AttributeTranslator,
        Configurable {

    private static final String CONF_ATTRIBUTE = "Attribute";
    private static final String CONF_ATTRIBUTE_OUTNAME = "outName";
    private static final String CONF_ATTRIBUTE_INNAME = "inName";

    private Map attributeTranslations;

    public void configure(Configuration attrTransConfig) throws ConfigurationException {
        // fetch attribute translation map
        attributeTranslations = new HashMap();
        if (attrTransConfig != null) {
            Configuration[] transList = attrTransConfig.getChildren();
            for (int i = 0; i < transList.length; i++) {
                if (!transList[i].getName().equals(CONF_ATTRIBUTE))
                    throw new ConfigurationException("Invalid child config element: "
                            + transList[i].getName());
                String in = transList[i].getAttribute(CONF_ATTRIBUTE_INNAME);
                String out = transList[i].getAttribute(CONF_ATTRIBUTE_OUTNAME);
                attributeTranslations.put(in, out);
            }
        }
    }

    public Map translateSamlAttributes(Map samlAttributesMap) {
        return translateSamlAttributes(samlAttributesMap, true);
    }

    /**
     * Translate Shibboleth Attributes according to configured attribute
     * translations
     * @param inName
     * @return Translated attribute name.
     */
    protected String translateAttribute(String inName) {
        String outName = (String) attributeTranslations.get(inName);
        return outName != null ? outName : inName;
    }

    public Map translateSamlAttributes(Map samlAttributesMap, boolean joinValues) {
        Map convertedMap = new HashMap(samlAttributesMap.size());
        Iterator keys = samlAttributesMap.keySet().iterator();
        while (keys.hasNext()) {
            SAMLAttribute attribute = (SAMLAttribute) samlAttributesMap.get(keys.next());
            String translatedKey = translateAttribute(attribute.getName());
            Object values;
            if (joinValues) {
                StringBuffer buffer = new StringBuffer();
                Iterator iter = attribute.getValues();
                if (iter.hasNext()) {
                    buffer.append((String) iter.next());
                }
                while (iter.hasNext()) {
                    buffer.append(ShibbolethModuleImpl.MULTIVALUE_SEPARATOR);
                    buffer.append((String) iter.next());
                }
                values = buffer.toString();
            }
            else {
                List valueList = new ArrayList();
                for (Iterator i = attribute.getValues(); i.hasNext(); ) {
                    String value = (String) i.next();
                    valueList.add(value);
                }
                values = valueList.toArray(new String[valueList.size()]);
            }
            convertedMap.put(translatedKey, values);
        }
        return convertedMap;
    }

    public String[] getSupportedResultNames() {
        Collection values = this.attributeTranslations.values();
        return (String[]) values.toArray(new String[values.size()]);
    }

}
