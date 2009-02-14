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

package org.apache.lenya.ac.cache;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.SourceValidity;
import org.apache.lenya.util.CacheMap;

/**
 * Basic implementation of a source cache.
 * @version $Id$
 */
public class SourceCacheImpl implements SourceCache {
    
    private static final Log logger = LogFactory.getLog(SourceCacheImpl.class);

    private SourceResolver sourceResolver;
    protected static final int CAPACITY = 1000;
    private CacheMap cache;

    /**
     * Returns the cache.
     * @return A cache object.
     */
    protected CacheMap getCache() {
        if (this.cache == null) {
            this.cache = new CacheMap(CAPACITY, logger);
        }
        return this.cache;
    }

    /**
     * @see org.apache.lenya.ac.cache.SourceCache#get(java.lang.String,
     *      org.apache.lenya.ac.cache.InputStreamBuilder)
     */
    public synchronized Object get(String sourceUri, InputStreamBuilder builder)
            throws CachingException {

        String key = sourceUri;
        Object value = null;

        CachedObject cachedObject = (CachedObject) getCache().get(key);
        boolean usedCache = false;
        SourceValidity sourceValidity = null;

        try {
            if (cachedObject != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Found cached object [" + cachedObject + "]");
                }
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
                    if (logger.isDebugEnabled()) {
                        logger
                                .debug("Using valid cached source for '" + sourceUri + "'.");
                    }
                    usedCache = true;
                    value = cachedObject.getValue();
                } else {
                    if (logger.isDebugEnabled()) {
                        logger
                                .debug("Cached content is invalid for '" + sourceUri + "'.");
                    }
                    // remove invalid cached object
                    getCache().remove(key);
                }

            } else {
                logger.debug("Did not find cached object.");
            }

            if (!usedCache) {
                logger.debug("Did not use cache.");
                if (key != null) {
                    if (sourceValidity == null) {
                        sourceValidity = getSourceValidity(sourceUri);
                    }
                    if (sourceValidity != null) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Source validity is not null.");
                        }
                    } else {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Source validity is null - not caching.");
                        }
                        key = null;
                    }
                }

                value = buildObject(sourceUri, builder);

                // store the response
                if (key != null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(
                                "Caching object [" + value + "] for further requests of ["
                                        + sourceUri + "].");
                    }
                    getCache().put(key, new CachedObject(sourceValidity, value));
                }
            }
        } catch (final SourceNotFoundException e1) {
            throw new CachingException(e1);
        } catch (final MalformedURLException e1) {
            throw new CachingException(e1);
        } catch (final IOException e1) {
            throw new CachingException(e1);
        } catch (final BuildException e1) {
            throw new CachingException(e1);
        }

        return value;
    }

    /**
     * Returns the input stream to read a source from.
     * @param sourceUri The URI of the source.
     * @param builder The input stream builder that should be used.
     * @return An object.
     * @throws MalformedURLException when an error occurs.
     * @throws IOException when an error occurs.
     * @throws SourceNotFoundException when an error occurs.
     * @throws BuildException if an error occurs.
     */
    protected synchronized Object buildObject(String sourceUri, InputStreamBuilder builder)
            throws MalformedURLException, IOException, SourceNotFoundException, BuildException {
        Object value = null;
        Source source = null;
        try {
            source = this.sourceResolver.resolveURI(sourceUri);
            if (source.exists()) {
                InputStream stream = source.getInputStream();
                value = builder.build(stream);
            }
        } finally {
            if (source != null) {
                this.sourceResolver.release(source);
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
    protected synchronized SourceValidity getSourceValidity(String sourceUri)
            throws MalformedURLException, IOException {
        SourceValidity sourceValidity;
        Source source = null;
        try {
            source = this.sourceResolver.resolveURI(sourceUri);
            sourceValidity = source.getValidity();
        } finally {
            if (source != null) {
                this.sourceResolver.release(source);
            }
        }
        return sourceValidity;
    }

    public void setSourceResolver(SourceResolver sourceResolver) {
        this.sourceResolver = sourceResolver;
    }

}
