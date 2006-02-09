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
package org.apache.lenya.cms.jcr.mock;

import java.util.HashMap;
import java.util.Map;

import org.apache.lenya.cms.repo.AssetType;
import org.apache.lenya.cms.repo.AssetTypeResolver;
import org.apache.lenya.cms.repo.RepositoryException;

/**
 * Mock asset type resolver.
 */
public class AssetTypeResolverImpl implements AssetTypeResolver {

    private Map types = new HashMap();
    
    /**
     * @param type The type to register.
     */
    public void register(AssetType type) {
        types.put(type.getName(), type);
    }
    
    public AssetType resolve(String name) throws RepositoryException {
        if (!types.containsKey(name)) {
            throw new RepositoryException("The asset type [" + name + "] is not registered!");
        }
        return (AssetType) types.get(name);
    }

    public boolean canResolve(String name) throws RepositoryException {
        return types.containsKey(name);
    }

}
