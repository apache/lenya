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
package org.apache.lenya.cms.repo.metadata.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.lenya.cms.repo.RepositoryException;
import org.apache.lenya.cms.repo.metadata.ElementSet;
import org.apache.lenya.cms.repo.metadata.MetaDataRegistry;

/**
 * Meta data registry implementation.
 */
public class MetaDataRegistryImpl implements MetaDataRegistry {

    private Map elementSets = new HashMap();

    public String[] getElementSetNames() throws RepositoryException {
        Set names = elementSets.keySet();
        return (String[]) names.toArray(new String[names.size()]);
    }

    public ElementSet getElementSet(String name) throws RepositoryException {
        if (!elementSets.containsKey(name)) {
            throw new RepositoryException("The element set [" + name + "] does not exist!");
        }
        return (ElementSet) elementSets.get(name);
    }

    /**
     * Registers an element set.
     * @param name The name.
     * @param elementSet The element set.
     * @throws RepositoryException if the name is already used.
     */
    public void registerElementSet(String name, ElementSet elementSet) throws RepositoryException {
        if (this.elementSets.containsKey(name)) {
            throw new RepositoryException("An element set is already registered for [" + name
                    + "].");
        }
        this.elementSets.put(name, elementSet);
    }
}
