/*
$Id: SourceCacheImpl.java,v 1.1 2003/08/13 13:10:11 andreas Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.cms.ac2.cache;

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

/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
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
        InputStream stream = null;

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

                stream = getInputStream(sourceUri);
                if (stream != null) {
                    value = builder.build(stream);
                }

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
     * @return An input stream.
     * @throws MalformedURLException when an error occurs.
     * @throws IOException when an error occurs.
     * @throws SourceNotFoundException when an error occurs.
     */
    protected InputStream getInputStream(String sourceUri)
        throws MalformedURLException, IOException, SourceNotFoundException {
        InputStream stream = null;
        Source source = null;
        try {
            source = getResolver().resolveURI(sourceUri);
            if (source.exists()) {
                stream = source.getInputStream();
            }
        } finally {
            if (source != null) {
                getResolver().release(source);
            }
        }
        return stream;
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
