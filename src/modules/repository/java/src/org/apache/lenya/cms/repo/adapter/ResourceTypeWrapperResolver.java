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
package org.apache.lenya.cms.repo.adapter;

import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.lenya.cms.publication.ResourceType;
import org.apache.lenya.cms.repo.AssetType;
import org.apache.lenya.cms.repo.AssetTypeResolver;
import org.apache.lenya.cms.repo.RepositoryException;

public class ResourceTypeWrapperResolver implements AssetTypeResolver {

    private ServiceManager manager;

    public ResourceTypeWrapperResolver(ServiceManager manager) {
        this.manager = manager;
    }

    private Map types = new HashMap();

    public AssetType resolve(String name) throws RepositoryException {
        if (!types.containsKey(name)) {
            ServiceSelector selector = null;
            try {
                selector = (ServiceSelector) this.manager.lookup(ResourceType.ROLE + "Selector");
                ResourceType type = (ResourceType) selector.select(name);
                AssetType assetType = new ResourceTypeWrapper(type);
                this.types.put(name, assetType);
            } catch (Exception e) {
                throw new RepositoryException(e);
            }
        }
        return (AssetType) types.get(name);
    }

    public boolean canResolve(String name) throws RepositoryException {
        if (types.containsKey(name)) {
            return true;
        } else {
            ServiceSelector selector = null;
            try {
                selector = (ServiceSelector) this.manager.lookup(ResourceType.ROLE + "Selector");
                return selector.isSelectable(name);
            } catch (Exception e) {
                throw new RepositoryException(e);
            }
        }
    }

}
