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

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.excalibur.store.impl.MRUMemoryStore;
import org.apache.lenya.cms.publication.Document;

/**
 * Cache for meta data.
 */
public class MetaDataCache implements ThreadSafe, Serviceable, Component {

    public static final String ROLE = MetaDataCache.class.getName();
    protected static final String STORE_ROLE = MetaDataCache.ROLE + "Store";
    private MRUMemoryStore store;
    protected ServiceManager manager;

    /**
     * Get a meta data object from the cache.
     * @param document The document.
     * @param namespaceUri The namespace URI.
     * @return A meta data object.
     * @throws MetaDataException if an error occurs.
     */
    public synchronized MetaData getMetaData(String cacheKey, MetaData meta, String namespaceUri)
            throws MetaDataException {
        MRUMemoryStore store = getStore();
        String key = getCacheKey(cacheKey, namespaceUri);

        MetaData cachedMeta = null;
        if (store.containsKey(key)) {
            cachedMeta = (MetaData) store.get(key);
            if (meta.getLastModified() > cachedMeta.getLastModified()) {
                cachedMeta = null;
            }
        }
        if (cachedMeta == null) {
            cachedMeta = new CacheableMetaData(meta);
            store.hold(key, cachedMeta);
        }
        return cachedMeta;
    }

    protected String getCacheKey(String cacheKey, String namespaceUri) {
        return cacheKey + ":" + namespaceUri;
    }

    protected MRUMemoryStore getStore() {
        if (this.store == null) {
            synchronized (this) {
                try {
                    this.store = (MRUMemoryStore) this.manager.lookup(STORE_ROLE);
                } catch (ServiceException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return this.store;
    }

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}
