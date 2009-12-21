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
package org.apache.lenya.cms.usecase.cocoon;

import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.components.modules.input.AbstractInputModule;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.cms.usecase.Usecase;
import org.apache.lenya.cms.usecase.UsecaseResolver;
import org.apache.lenya.cms.usecase.gui.Tab;
import org.apache.lenya.util.ServletHelper;

/**
 * Input module to obtain information about usecases.
 */
public class UsecaseModule extends AbstractInputModule implements Serviceable {

    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
            throws ConfigurationException {
        
        Object value = "";
        
        String prefix = "tabGroup:";
        if (name.startsWith(prefix) && name.length() > prefix.length()) {
            String[] steps = name.split(":");
            String usecaseName = steps[1];
            
            Request request = ObjectModelHelper.getRequest(objectModel);
            String webappUrl = ServletHelper.getWebappURI(request);
            
            UsecaseResolver resolver = null;
            Usecase usecase = null;
            try {
                resolver = (UsecaseResolver) this.manager.lookup(UsecaseResolver.ROLE);
                usecase = resolver.resolve(webappUrl, usecaseName);
                if (usecase.getView() != null) {
                    Tab tab = usecase.getView().getTab();
                    if (tab != null) {
                        value = tab.getGroup();
                    }
                }
            } catch (ServiceException e) {
                throw new ConfigurationException("Error: ", e);
            }
            finally {
                if (resolver != null) {
                    if (usecase != null) {
                        try {
                            resolver.release(usecase);
                        } catch (ServiceException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    this.manager.release(resolver);
                }
            }
        }
        
        return value;

    }
    
    private ServiceManager manager;

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}
