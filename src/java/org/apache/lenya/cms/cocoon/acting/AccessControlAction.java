/*
$Id: AccessControlAction.java,v 1.4 2003/08/07 13:19:01 andreas Exp $
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

package org.apache.lenya.cms.cocoon.acting;

import java.util.Collections;
import java.util.Map;

import org.apache.avalon.framework.component.ComponentSelector;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.ConfigurableComposerAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.lenya.cms.ac2.AccessController;
import org.apache.lenya.cms.ac2.AccessControllerResolver;
import org.apache.lenya.util.ServletHelper;

/**
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann</a>
 */
public abstract class AccessControlAction extends ConfigurableComposerAction {

    private AccessController accessController;

    /**
     * <p>
     * Invokes the access control functionality.
     * If no access controller was found for the requested URL, an empty map is returned.
     * </p>
     * <p>
     * This is a template method. Implement doAct() to add your functionality.
     * </p>
     * @see org.apache.cocoon.acting.Action#act(org.apache.cocoon.environment.Redirector, org.apache.cocoon.environment.SourceResolver, java.util.Map, java.lang.String, org.apache.avalon.framework.parameters.Parameters)
     */
    public Map act(
        Redirector redirector,
        SourceResolver sourceResolver,
        Map objectModel,
        String source,
        Parameters parameters)
        throws Exception {

        ComponentSelector selector = null;
        AccessControllerResolver resolver = null;
        accessController = null;

        Request request = ObjectModelHelper.getRequest(objectModel);

        Map result = null;

        try {
            selector =
                (ComponentSelector) manager.lookup(AccessControllerResolver.ROLE + "Selector");
            resolver =
                (AccessControllerResolver) selector.select(
                    AccessControllerResolver.DEFAULT_RESOLVER);

            String webappUrl = ServletHelper.getWebappURI(request);
            accessController = resolver.resolveAccessController(webappUrl);

            if (accessController == null) {
                result = Collections.EMPTY_MAP;
            } else {
                accessController.setupIdentity(request);
                result = doAct(redirector, sourceResolver, objectModel, source, parameters);
            }

        } finally {
            if (selector != null) {
                if (resolver != null) {
                    if (accessController != null) {
                        resolver.release(accessController);
                    }
                    selector.release(resolver);
                }
                manager.release(selector);
            }
        }
        return result;
    }

    /**
     * The actual act method.
     * @param redirector  The <code>Redirector</code> in charge
     * @param resolver    The <code>SourceResolver</code> in charge
     * @param objectModel The <code>Map</code> with object of the
     *                    calling environment which can be used
     *                    to select values this controller may need
     *                    (ie Request, Response).
     * @param source      A source <code>String</code> to the Action
     * @param parameters  The <code>Parameters</code> for this invocation
     * @return Map        The returned <code>Map</code> object with
     *                    sitemap substitution values which can be used
     *                    in subsequent elements attributes like src=
     *                    using a xpath like expression: src="mydir/{myval}/foo"
     *                    If the return value is null the processing inside
     *                    the <map:act> element of the sitemap will
     *                    be skipped.
     * @exception Exception Indicates something is totally wrong
     */
    protected abstract Map doAct(
        Redirector redirector,
        SourceResolver resolver,
        Map objectModel,
        String source,
        Parameters parameters)
        throws Exception;

    /**
     * Returns the access controller.
     * @return An access controller.
     */
    public AccessController getAccessController() {
        return accessController;
    }

}
