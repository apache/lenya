/*
 * $Id
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.apache.lenya.cms.ac2;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.lenya.cms.ac.AccessControlException;
import org.apache.lenya.cms.ac.GroupManager;
import org.apache.lenya.cms.ac.RoleManager;
import org.apache.lenya.cms.ac.UserManager;
import org.apache.lenya.cms.publication.PageEnvelope;

/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface AccessController {

    /**
     * Returns the user manager of this access controller.
     * @return A user manager.
     * @throws AccessControlException when something went wrong.
     */
    UserManager getUserManager() throws AccessControlException;
    
    /**
     * Returns the group manager of this access controller.
     * @return A group manager.
     * @throws AccessControlException when something went wrong.
     */
    GroupManager getGroupManager() throws AccessControlException;
    
    /**
     * Returns the role manager of this access controller.
     * @return A role manager.
     * @throws AccessControlException when something went wrong.
     */
    RoleManager getRoleManager() throws AccessControlException;
    
    /**
     * Returns the policy manager of this access controller.
     * @return A policy manager.
     * @throws AccessControlException when something went wrong.
     */
    PolicyManager getPolicyManager() throws AccessControlException;
    
    /**
     * Returns the policy for a given page.
     * @param envelope The envelope of the page.
     * @return The policy.
     * @throws AccessControlException when something went wrong.
     */
    Policy getPolicy(PageEnvelope envelope) throws AccessControlException;
    
    /**
     * Configures this access controller.
     * @param configuration The configuration.
     * @throws ConfigurationException if something went wrong.
     */
    void configure(Configuration configuration) throws ConfigurationException;

}
