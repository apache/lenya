/*
$Id: ComposableAccessControllerResolver.java,v 1.5 2003/08/11 16:06:17 andreas Exp $
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

import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.lenya.cms.ac.AccessControlException;

/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ComposableAccessControllerResolver
    extends AbstractAccessControllerResolver
    implements Configurable, Disposable {

    /**
     * @see org.apache.lenya.cms.ac2.AbstractAccessControllerResolver#doResolveAccessController(java.lang.String)
     */
    public AccessController doResolveAccessController(String url) throws AccessControlException {

        AccessController controller = null;

        try {
            
            if (selector == null) {
                selector =
                    (ServiceSelector) getManager().lookup(AccessControllerResolver.ROLE + "Selector");
            }

            String[] types = getResolverTypes();
            int i = 0;
            while (controller == null && i < types.length) {

                getLogger().debug("Trying to resolve AC resolver for type [" + types[i] + "]");
                AccessControllerResolver resolver =
                    (AccessControllerResolver) selector.select(types[i]);
                controller = resolver.resolveAccessController(url);
                setResolver(controller, resolver);
                getLogger().debug("Resolved access controller [" + controller + "]");
                i++;
            }

        } catch (ServiceException e) {
            throw new AccessControlException(e);
        }

        return controller;
    }

    private Map controllerToResolver = new HashMap();

    /**
     * @see org.apache.lenya.cms.ac2.AccessControllerResolver#release(org.apache.lenya.cms.ac2.AccessController)
     */
    public void release(AccessController controller) {
        assert controller != null;
        AccessControllerResolver resolver = getResolver(controller);
        resolver.release(controller);
        selector.release(resolver);
    }

    /**
     * Returns the access controller resolver that was used to resolve a
     * specific access controller.
     * @param controller The access controller.
     * @return An AC resolver.
     */
    protected AccessControllerResolver getResolver(AccessController controller) {
        AccessControllerResolver resolver =
            (AccessControllerResolver) controllerToResolver.get(controller);
        return resolver;
    }
    
    /**
     * Sets the access controller resolver that was used to resolve a
     * specific access controller.
     * @param controller The access controller.
     * @param resolver An AC resolver.
     */
    protected void setResolver(AccessController controller, AccessControllerResolver resolver) {
        controllerToResolver.put(controller, resolver);
    }

    protected static final String RESOLVER_ELEMENT = "resolver";
    protected static final String TYPE_ATTRIBUTE = "type";

    private String[] resolverTypes;
    private ServiceSelector selector;

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration configuration) throws ConfigurationException {
        Configuration[] accessControllerConfigs = configuration.getChildren(RESOLVER_ELEMENT);
        resolverTypes = new String[accessControllerConfigs.length];
        for (int i = 0; i < accessControllerConfigs.length; i++) {
            resolverTypes[i] = accessControllerConfigs[i].getAttribute(TYPE_ATTRIBUTE);
        }
    }

    /**
     * Returns the access controller types.
     * @return A string array.
     */
    protected String[] getResolverTypes() {
        return resolverTypes;
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose() {
        if (selector != null) {
            getManager().release(selector);
        }
    }

}
