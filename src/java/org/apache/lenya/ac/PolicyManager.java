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

/* $Id: PolicyManager.java,v 1.2 2004/03/03 12:56:31 gregor Exp $  */

package org.apache.lenya.ac;

import org.apache.avalon.framework.component.Component;

public interface PolicyManager extends Component {

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
	 * Called when an accreditable was removed. Used to clean up the policies.
	 * 
     * @param manager The accreditable manager the accreditable belonged to.
	 * @param accreditable The accreditable that was removed.
     * @throws AccessControlException when something went wrong.
	 */
    void accreditableRemoved(AccreditableManager manager, Accreditable accreditable) throws AccessControlException;

}
