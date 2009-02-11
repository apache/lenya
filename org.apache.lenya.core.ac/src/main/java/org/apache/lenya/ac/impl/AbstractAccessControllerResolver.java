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

package org.apache.lenya.ac.impl;

import org.apache.cocoon.util.AbstractLogEnabled;
import org.apache.commons.lang.Validate;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.AccessController;
import org.apache.lenya.ac.AccessControllerResolver;
import org.apache.lenya.ac.cache.URLKeyUtil;
import org.apache.lenya.util.CacheMap;

/**
 * Abstract implementation for access controller resolvers.
 * @version $Id$
 */
public abstract class AbstractAccessControllerResolver extends AbstractLogEnabled implements
        AccessControllerResolver {

    protected static final int CAPACITY = 1000;
    private CacheMap cache;
    private SourceResolver sourceResolver;

    protected CacheMap getCache() {
        if (this.cache == null) {
            this.cache = new CacheMap(CAPACITY, getLogger());
        }
        return this.cache;
    }

    /**
     * @see org.apache.lenya.ac.AccessControllerResolver#resolveAccessController(java.lang.String)
     */
    public AccessController resolveAccessController(String webappUrl) throws AccessControlException {
        Validate.notNull(webappUrl, "webapp URL");

        AccessController controller = null;
        Object key = null;

        try {
            key = generateCacheKey(webappUrl, getSourceResolver());
            getLogger().debug("Access controller cache key: [" + key + "]");

        } catch (Exception e) {
            throw new AccessControlException(e);
        }

        CacheMap cache = getCache();

        synchronized (cache) {
            controller = (AccessController) cache.get(key);
            if (controller == null) {
                getLogger().debug("No access controller in cache.");
                controller = doResolveAccessController(webappUrl);
                cache.put(key, controller);
            } else {
                getLogger().debug("Getting access controller from cache.");
            }
        }

        return controller;
    }

    /**
     * Generates a cache key for the access controller.
     * @param webappUrl The webapp URL.
     * @param resolver The source resolver.
     * @return An object.
     * @throws AccessControlException when something went wrong.
     */
    protected Object generateCacheKey(String webappUrl, SourceResolver resolver)
            throws AccessControlException {
        Validate.notNull(webappUrl, "webapp URL");
        Validate.notNull(resolver, "resolver");
        Object key;
        try {
            key = URLKeyUtil.generateKey(resolver, webappUrl);
        } catch (Exception e) {
            throw new AccessControlException(e);
        }
        return key;
    }

    /**
     * The actual resolving method.
     * @param webappUrl The URL within the web application.
     * @return An access controller.
     * @throws AccessControlException when something went wrong.
     */
    protected abstract AccessController doResolveAccessController(String webappUrl)
            throws AccessControlException;

    public void setSourceResolver(SourceResolver sourceResolver) {
        this.sourceResolver = sourceResolver;
    }

    protected SourceResolver getSourceResolver() {
        return sourceResolver;
    }

}
