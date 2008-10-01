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

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.lenya.ac.attr.AttributeManager;
import org.apache.lenya.ac.attr.AttributeRuleEvaluator;
import org.apache.lenya.ac.attr.AttributeRuleEvaluatorFactory;
import org.apache.lenya.ac.attr.AttributeSet;

public class AttributeManagerImpl extends AbstractLogEnabled implements Serviceable,
        AttributeManager, Disposable, Initializable, ThreadSafe, Component {

    private ServiceManager manager;
    private ServiceSelector attrSetSelector;
    private AttributeRuleEvaluatorFactory evaluatorFactory;
    private Map name2attributeSet = new HashMap();

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

    public synchronized AttributeSet getAttributeSet(String name) {
        AttributeSet attrs = (AttributeSet) this.name2attributeSet.get(name);
        if (attrs == null) {
            try {
                attrs = (AttributeSet) this.attrSetSelector.select(name);
            } catch (ServiceException e) {
                throw new RuntimeException(e);
            }
            this.name2attributeSet.put(name, attrs);
        }
        return attrs;
    }

    public AttributeRuleEvaluator getEvaluator() {
        return this.evaluatorFactory.getEvaluator();
    }

    public void dispose() {
        if (this.attrSetSelector != null) {
            this.manager.release(this.attrSetSelector);
        }
        if (this.evaluatorFactory != null) {
            this.manager.release(this.evaluatorFactory);
        }
    }

    public void initialize() throws Exception {
        this.attrSetSelector = (ServiceSelector) this.manager
                .lookup(AttributeSet.ROLE + "Selector");
        this.evaluatorFactory = (AttributeRuleEvaluatorFactory) this.manager
                .lookup(AttributeRuleEvaluatorFactory.ROLE);
    }

}
