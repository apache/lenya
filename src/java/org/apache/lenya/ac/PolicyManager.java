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

package org.apache.lenya.ac;

import org.apache.avalon.framework.component.Component;

/**
 * A policy manager.
 * @version $Id: PolicyManager.java 473841 2006-11-12 00:46:38Z gregor $
 */
public interface PolicyManager extends Component {

    /**
     * The Avalon role.
     */
    String ROLE = PolicyManager.class.getName();

    /**
	 * Returns the policy for a given page.
	 * 
	 * @param controller The access controller.
	 * @param url The url inside the web application.
	 * @return The policy.
	 * @throws AccessControlException when something went wrong.
	 */
    Policy getPolicy(AccreditableManager controller, String url) throws AccessControlException;

    /**
     * Called when an accreditable was added. Used to create the admin interface policy.
     * 
     * @param manager The accreditable manager the accreditable belonged to.
     * @param accreditable The accreditable that was removed.
     * @throws AccessControlException when something went wrong.
     */
    void accreditableAdded(AccreditableManager manager, Accreditable accreditable) throws AccessControlException;

    /**
	 * Called when an accreditable was removed. Used to clean up the policies
     * and to remove the admin interface policy.
	 * 
     * @param manager The accreditable manager the accreditable belonged to.
	 * @param accreditable The accreditable that was removed.
     * @throws AccessControlException when something went wrong.
	 */
    void accreditableRemoved(AccreditableManager manager, Accreditable accreditable) throws AccessControlException;

}
