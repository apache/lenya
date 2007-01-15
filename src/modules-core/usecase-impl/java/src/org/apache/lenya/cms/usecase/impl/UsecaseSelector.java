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

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.components.ExtendedComponentSelector;
import org.apache.lenya.cms.usecase.UsecaseResolver;

/**
 * Usecase selector.
 */
public class UsecaseSelector extends ExtendedComponentSelector implements ThreadSafe, Startable, Serviceable {
    
    private SortedSet usecaseNames;
    private ServiceManager manager;
    
    /**
     * @return The names of all registered usecases in alphabetical order.
     */
    public String[] getUsecaseNames() {
        return (String[]) this.usecaseNames.toArray(new String[this.usecaseNames.size()]);
    }

    public void configure(Configuration config) throws ConfigurationException {
        super.configure(config);
        
        this.usecaseNames = new TreeSet();
        Configuration[] usecaseConfigs = config.getChildren("component-instance");
        for (int i = 0; i < usecaseConfigs.length; i++) {
            this.usecaseNames.add(usecaseConfigs[i].getAttribute("name"));
        }
    }

    public void start() throws Exception {
        UsecaseResolver resolver = null;
        try {
            resolver = (UsecaseResolver) this.manager.lookup(UsecaseResolver.ROLE);
            for (Iterator i = this.usecaseNames.iterator(); i.hasNext(); ) {
                resolver.register((String) i.next());
            }
        }
        finally {
            if (resolver != null) {
                this.manager.release(resolver);
            }
        }
    }

    public void stop() throws Exception {
    }

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}
