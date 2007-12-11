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
package org.apache.lenya.ac.file;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.AccreditableManagerFactory;
import org.apache.lenya.ac.UserType;

/**
 * Factory for file-based accreditable managers.
 */
public class FileAccreditableManagerFactory extends AbstractLogEnabled implements
        AccreditableManagerFactory, ThreadSafe, Serviceable {

    private Map id2manager = new HashMap();

    protected static final String U_M_CHILD_TAG = "user-manager";
    protected static final String U_T_CHILD_TAG = "user-type";
    protected static final String U_T_CLASS_ATTRIBUTE = "class";
    protected static final String U_T_CREATE_ATTRIBUTE = "create-use-case";

    public AccreditableManager getAccreditableManager(Configuration config)
            throws ConfigurationException {

        try {
            String configUri = null;
            Configuration[] paramConfigs = config.getChildren("parameter");
            for (int i = 0; i < paramConfigs.length; i++) {
                if (paramConfigs[i].getAttribute("name").equals("directory")) {
                    configUri = paramConfigs[i].getAttribute("value");
                }
            }

            if (configUri == null) {
                throw new RuntimeException("No <parameter name=\"directory\"> element found!");
            }

            if (this.id2manager.containsKey(configUri)) {
                return (AccreditableManager) this.id2manager.get(configUri);
            } else {
                Set userTypes = new HashSet();
                Configuration umConf = config.getChild(U_M_CHILD_TAG, false);
                if (umConf != null) {
                    Configuration[] typeConfs = umConf.getChildren();
                    for (int i = 0; i < typeConfs.length; i++) {
                        userTypes.add(new UserType(typeConfs[i].getValue(), typeConfs[i]
                                .getAttribute(U_T_CLASS_ATTRIBUTE), typeConfs[i]
                                .getAttribute(U_T_CREATE_ATTRIBUTE)));
                    }
                } else {
                    getLogger().debug(
                            "FileAccreditableManager: using default configuration for user types");
                    // no "user-manager" block in access control: provide
                    // a default for backward compatibility
                    userTypes.add(FileAccreditableManager.getDefaultUserType());
                }
                UserType[] types = (UserType[]) userTypes.toArray(new UserType[userTypes.size()]);
                AccreditableManager mgr = new FileAccreditableManager(this.manager, getLogger(),
                        configUri, types);
                this.id2manager.put(configUri, mgr);
                return mgr;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected ServiceManager manager;

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}
