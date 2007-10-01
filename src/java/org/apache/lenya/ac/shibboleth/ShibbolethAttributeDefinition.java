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
package org.apache.lenya.ac.shibboleth;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.lenya.ac.AttributeDefinition;
import org.apache.lenya.ac.saml.AttributeTranslator;

/**
 * Shibboleth-based attribute definition. The attribute names are the possible
 * result names of the Shibboleth attribute translator.
 */
public class ShibbolethAttributeDefinition extends AbstractLogEnabled implements
        AttributeDefinition, Serviceable, ThreadSafe {

    private AttributeTranslator translator;
    private ServiceManager manager;

    public String[] getAttributeNames() {
        if (this.translator == null) {
            try {
                this.translator = (AttributeTranslator) this.manager
                        .lookup(AttributeTranslator.ROLE);
            } catch (ServiceException e) {
                throw new RuntimeException(e);
            }
        }
        return this.translator.getSupportedResultNames();
    }

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}
