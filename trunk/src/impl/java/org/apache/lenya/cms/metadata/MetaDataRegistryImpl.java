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
package org.apache.lenya.cms.metadata;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.thread.ThreadSafe;

/**
 * Meta data registry implementation.
 */
public class MetaDataRegistryImpl extends AbstractLogEnabled implements MetaDataRegistry,
        ThreadSafe {

    public ElementSet getElementSet(String namespaceUri) throws MetaDataException {
        if (!isRegistered(namespaceUri)) {
            throw new MetaDataException("The namespace URI [" + namespaceUri
                    + "] is not registered.");
        }
        return (ElementSet) this.namespace2set.get(namespaceUri);
    }

    public boolean isRegistered(String namespaceUri) throws MetaDataException {
        return this.namespace2set.containsKey(namespaceUri);
    }

    private Map namespace2set = new HashMap();

    public void register(String namespaceUri, ElementSet elementSet) throws MetaDataException {
        if (this.namespace2set.containsKey(namespaceUri)) {
            throw new MetaDataException("The namespace [" + namespaceUri
                    + "] is already registered.");
        }
        this.namespace2set.put(namespaceUri, elementSet);
    }

    public String[] getNamespaceUris() throws MetaDataException {
        Set keys = this.namespace2set.keySet();
        return (String[]) keys.toArray(new String[keys.size()]);
    }

}
