/*
$Id: AccessController.java,v 1.10 2003/07/29 17:23:18 andreas Exp $
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

import org.apache.avalon.framework.component.Component;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.cms.ac.AccessControlException;

/**
 * An access controller allows authenticating and authorizing identities.
 * 
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann</a>
 */
public interface AccessController extends Component {

    String NAMESPACE = "http://apache.org/cocoon/lenya/ac/1.0";
    String DEFAULT_PREFIX = "ac";
    
    String ROLE = AccessController.class.getName();

    /**
     * Authenticates a request.
     * @param request A request.
     * @return A boolean value.
     * @throws AccessControlException when something went wrong.
     */
    boolean authenticate(Request request) throws AccessControlException;

    /**
     * Authorizes a request inside a publication.
     * @param request A request.
     * @return A boolean value.
     * @throws AccessControlException when something went wrong.
     */
    boolean authorize(Request request) throws AccessControlException;
    
    /**
     * Checks if this identity was initialized by this access controller.
     * @param identity An identity.
     * @return A boolean value.
     * @throws AccessControlException when something went wrong.
     */
    //boolean ownsIdenity(Identity identity) throws AccessControlException;
    
    /**
     * Initializes the identity for this access controller.
     * @param request The request that contains the identity information.
     * @throws AccessControlException when something went wrong.
     */
    void setupIdentity(Request request) throws AccessControlException;

}
