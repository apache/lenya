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

/* $Id: SourceCacheImpl.java,v 1.2 2004/03/03 12:56:32 gregor Exp $  */

package org.apache.lenya.ac.cache;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.SourceValidity;
import org.apache.lenya.util.CacheMap;

public class SourceCacheImpl
    extends AbstractLogEnabled
    implements SourceCache, Serviceable, Disposable {

    /**
     * Returns the service manager.
     * @return A service manager.
     */
    public ServiceManager getManager() {
        return manager;
    }

    /**
     * Returns the source resolver.
     * @return A source resolver.
     */
    public SourceResolver getResolver() {
        return resolver;
    }

    /**
     * Ctor.
     */
    public SourceCacheImpl() {
        cache = new CacheMap(CAPACITY);
    }

    public static final int CAPACITY = 1000;
    private CacheMap cache;

    /**
     * Returns the cache.
     * @return A cache object.
     */
    protected CacheMap getCache() {
        return cache;
    }

    /**
     * @see org.apache.lenya.cms.ac2.cache.SourceCache#get(java.lang.String, org.apache.lenya.cms.ac2.cache.InputStreamBuilder)
     */
    public Object get(String sourceUri, InputStreamBuilder builder) throws CachingException {

        String key = sourceUri;
        Object value = null;

        CachedObject cachedObject = (CachedObject) getCache().get(key);
        boolean usedCache = false;
        SourceValidity sourceValidity = null;

        try {

            if (cachedObject != null) {
                getLogger().debug("Found cached object [" + cachedObject + "]");
                SourceValidity cachedValidity = cachedObject.getValidityObject();

                int result = cachedValidity.isValid();
                boolean valid = false;
                if (result == 0) {

                    // get source validity and compare

                    sourceValidity = getSourceValidity(sourceUri);

                    if (sourceValidity != null) {
                        result = cachedValidity.isValid(sourceValidity);
                        if (result == 0) {
                            sourceValidity = null;
                        } else {
                            valid = (result == 1);
                        }
                    }
                } else {
                    valid = (result > 0);
                }

                if (valid) {
                    if (this.getLogger().isDebugEnabled()) {
                        this.getLogger().debug(
                            "Using valid cached source for '" + sourceUri + "'.");
                    }
                    usedCache = true;
                    value = cachedObject.getValue();
                } else {
                    if (this.getLogger().isDebugEnabled()) {
                        this.getLogger().debug(
                            "Cached content is invalid for '" + sourceUri + "'.");
                    }
                    // remove invalid cached object
                    getCache().remove(key);
                }

            } else {
                getLogger().debug("Did not find cached object.");
            }

            if (!usedCache) {
                getLogger().debug("Did not use cache.");
                if (key != null) {
                    if (sourceValidity == null) {
                        sourceValidity = getSourceValidity(sourceUri);
                    }
                    if (sourceValidity != null) {
                        getLogger().debug("Source validity is not null.");
                    } else {
                        getLogger().debug("Source validity is null - not caching.");
                        key = null;
                    }
                }

                value = buildObject(sourceUri, builder);

                // store the response
                if (key != null) {
                    if (this.getLogger().isDebugEnabled()) {
                        this.getLogger().debug(
                            "Caching object ["
                                + value
                                + "] for further requests of ["
                                + sourceUri
                                + "].");
                    }
                    getCache().put(key, new CachedObject(sourceValidity, value));
                }
            }

        } catch (Exception e) {
            throw new CachingException(e);
        }

        return value;
    }

    /**
     * Returns the input stream to read a source from.
     * @param sourceUri The URI of the source.
     * @return An object.
     * @throws MalformedURLException when an error occurs.
     * @throws IOException when an error occurs.
     * @throws SourceNotFoundException when an error occurs.
     */
    protected Object buildObject(String sourceUri, InputStreamBuilder builder)
        throws MalformedURLException, IOException, SourceNotFoundException, BuildException {
        Object value = null;
        Source source = null;
        try {
            source = getResolver().resolveURI(sourceUri);
            if (source.exists()) {
                InputStream stream = source.getInputStream();
                value = builder.build(stream);
            }
        } finally {
            if (source != null) {
                getResolver().release(source);
            }
        }
        return value;
    }

    /**
     * Returns the validity of a source.
     * @param sourceUri The URI of the source.
     * @return A source validity object.
     * @throws MalformedURLException when an error occurs.
     * @throws IOException when an error occurs.
     */
    protected SourceValidity getSourceValidity(String sourceUri)
        throws MalformedURLException, IOException {
        SourceValidity sourceValidity;
        Source source = null;
        try {
            source = getResolver().resolveURI(sourceUri);
            sourceValidity = source.getValidity();
        } finally {
            if (source != null) {
                getResolver().release(source);
            }
        }
        return sourceValidity;
    }

    private ServiceManager manager;
    private SourceResolver resolver;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
        this.resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose() {
        if (getResolver() != null) {
            getManager().release(getResolver());
        }
    }

}
