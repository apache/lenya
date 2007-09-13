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

import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.shibboleth.util.UniqueIdentifierMapper;

/**
 * Implementation of a unique identifier mapper.
 */
public class UniqueIdentifierMapperImpl extends AbstractLogEnabled implements UniqueIdentifierMapper, Configurable,
        ThreadSafe {

    private static final String CONF_UNIQUEIDENTIFIERS_DEFAULT = "Default";
    private static final String CONF_UNIQUEIDENTIFIERS_SITENAME = "siteName";
    private static final String CONF_UNIQUEIDENTIFIERS_ORIGINSITE = "OriginSite";
    private static final String CONF_UNIQUEIDENTIFIERS_UIDATTRIBUTE = "uidAttribute";

    private static String defaultUniqueIdentifier;
    private static Map uniqueIdentifierMap;

    public void configure(Configuration uidConfig) throws ConfigurationException {
        uniqueIdentifierMap = new HashMap();
        Configuration defaultUidConfig = uidConfig.getChild(CONF_UNIQUEIDENTIFIERS_DEFAULT);
        if (defaultUidConfig == null)
            throw new ConfigurationException("Missing default unique identifier. Please fix!");
        defaultUniqueIdentifier = defaultUidConfig
                .getAttribute(CONF_UNIQUEIDENTIFIERS_UIDATTRIBUTE);
        Configuration[] originUidConfigs = uidConfig.getChildren(CONF_UNIQUEIDENTIFIERS_ORIGINSITE);
        for (int i = 0; i < originUidConfigs.length; i++) {
            String siteName = originUidConfigs[i].getAttribute(CONF_UNIQUEIDENTIFIERS_SITENAME);
            String uid = originUidConfigs[i].getAttribute(CONF_UNIQUEIDENTIFIERS_UIDATTRIBUTE);
            uniqueIdentifierMap.put(siteName, uid);
        }
    }

    public String resolveUIDAttribute(String originSiteName) {
        if (originSiteName == null)
            return defaultUniqueIdentifier;
        else {
            String uidAttr = (String) uniqueIdentifierMap.get(originSiteName);
            return uidAttr == null ? defaultUniqueIdentifier : uidAttr;
        }
    }

}
