/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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
package org.apache.lenya.cms.usecase;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.Serviceable;

/**
 * Usecase resolver implementation.
 *
 * @version $Id:$ 
 */
public class UsecaseResolverImpl extends AbstractLogEnabled implements UsecaseResolver,
        Serviceable, Disposable {

    /**
     * Ctor.
     */
    public UsecaseResolverImpl() {
    }

    private ServiceSelector selector;

    /**
     * @see org.apache.lenya.cms.usecase.UsecaseResolver#resolve(java.lang.String)
     */
    public Usecase resolve(String name) throws ServiceException {
        Usecase usecase = (Usecase) this.selector.select(name);
        return usecase;
    }

    private ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
        this.selector = (ServiceSelector) manager.lookup(Usecase.ROLE + "Selector");
    }

    /**
     * @see org.apache.lenya.cms.usecase.UsecaseResolver#release(org.apache.lenya.cms.usecase.Usecase)
     */
    public void release(Usecase usecase) throws ServiceException {
        if (usecase == null) {
            throw new IllegalArgumentException("The usecase to release must not be null.");
        }
        this.selector.release(usecase);

    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose() {
        if (this.selector != null) {
            this.manager.release(selector);
        }
    }

}