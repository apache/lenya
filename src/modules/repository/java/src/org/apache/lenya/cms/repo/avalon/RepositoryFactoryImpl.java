/*
 * Copyright  1999-2005 The Apache Software Foundation
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
package org.apache.lenya.cms.repo.avalon;

import java.util.Map;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.lenya.cms.repo.AssetTypeResolver;
import org.apache.lenya.cms.repo.Repository;
import org.apache.lenya.cms.repo.RepositoryException;
import org.apache.lenya.cms.repo.RepositoryManager;
import org.apache.lenya.cms.repo.adapter.ResourceTypeWrapperResolver;

/**
 * Repository factory implementation.
 */
public class RepositoryFactoryImpl implements RepositoryFactory, Contextualizable, Configurable,
        ThreadSafe, Serviceable {

    public void contextualize(Context context) throws ContextException {
        this.context = context;
    }

    public void configure(Configuration config) throws ConfigurationException {
        this.factoryClassName = config.getChild("repository-factory").getAttribute("class");
    }

    private String factoryClassName;
    private Repository repository;
    private Context context;

    public Repository getRepository() throws RepositoryException {
        if (this.repository == null) {
            Map objectModel = ContextHelper.getObjectModel(this.context);
            String webappPath = ObjectModelHelper.getContext(objectModel).getRealPath("/");
            this.repository = RepositoryManager.getRepository(webappPath, this.factoryClassName);
            AssetTypeResolver resolver = new ResourceTypeWrapperResolver(manager);
            this.repository.setAssetTypeResolver(resolver);
        }
        return this.repository;
    }
    
    private ServiceManager manager;

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}
