/*
$Id: AbstractAccessControllerResolver.java,v 1.4 2003/10/31 15:16:45 andreas Exp $
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

package org.apache.lenya.cms.ac2;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.ac.AccessControlException;
import org.apache.lenya.cms.ac2.cache.URLKeyUtil;
import org.apache.lenya.util.CacheMap;

/**
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann</a>
 */
public abstract class AbstractAccessControllerResolver
    extends AbstractLogEnabled
    implements AccessControllerResolver, Serviceable, ThreadSafe {

    protected static final int CAPACITY = 1000;
    private CacheMap cache = new CacheMap(CAPACITY);

    /**
     * @see org.apache.lenya.cms.ac2.AccessControllerResolver#resolveAccessController(java.lang.String)
     */
    public AccessController resolveAccessController(String webappUrl)
        throws AccessControlException {

        SourceResolver resolver = null;
        AccessController controller = null;
        Object key = null;

        try {
            resolver = (SourceResolver) getManager().lookup(SourceResolver.ROLE);
            key = generateCacheKey(webappUrl, resolver);
            getLogger().debug("Access controller cache key: [" + key + "]");

        } catch (Exception e) {
            throw new AccessControlException(e);
        } finally {
            if (resolver != null) {
                getManager().release(resolver);
            }
        }

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

    /**
     * @see org.apache.lenya.cms.ac2.AccessControllerResolver#release(org.apache.lenya.cms.ac2.AccessController)
     */
    public void release(AccessController controller) {
        /*
        if (controller != null) {
            getManager().release(controller);
        }
        */
    }

    private ServiceManager manager;

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException {
        getLogger().debug("Servicing [" + getClass().getName() + "]");
        this.manager = manager;
    }

    /**
     * Returns the service manager of this Serviceable.
     * @return A service manager.
     */
    public ServiceManager getManager() {
        return manager;
    }

}
