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
package org.apache.lenya.cms.ac.usecases;

import org.apache.cocoon.ProcessingException;
import org.apache.lenya.cms.usecase.UsecaseException;

import org.apache.lenya.ac.Item;
import org.apache.lenya.ac.Role;
import org.apache.lenya.cms.ac.cocoon.PolicyHelper;

/**
 * Usecase to display the AccessControl tab in the site area for a document.
 * This is a mix-in class that ideally would inherit both from
 * AccessControlUsecase and DocumentUsecase. FIXME i just took the appropriate
 * code from DocumentUsecase, maybe its possible to have a saner inheritance?
 * 
 * @version $Id: AccessControl.java 123980 2005-01-03 15:00:14Z andreas $
 */

public class AccessControl extends AccessControlUsecase {

    private PolicyHelper helper = null;
    private Item[] items = null;

    private static String[] types = { "user", "group", "iprange", "role" };
    private static String[] operations = { "add", "delete" };

    /**
     * Ctor.
     */
    public AccessControl() {
        super();
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();

        try {
            Role[] roles = getRoleManager().getRoles();
            String visitorRole = "";
            for (int i = 0; i < roles.length; i++) {
                if (roles[i].getId().equals("visit")) {
                    visitorRole = roles[i].getId();
                }
            }

            setParameter("visitorRole", visitorRole);

            this.helper = new PolicyHelper(getLogger());
            //FIXME expects the component manager
            // helper.setup(objectModel, this.manager, area);

            for (int i = 0; i < types.length; i++) {
                Item[] _items = null;

                if (types[i].equals("user")) {
                    _items = getUserManager().getUsers();
                } else if (types[i].equals("group")) {
                    _items = getGroupManager().getGroups();
                } else if (types[i].equals("iprange")) {
                    _items = getIpRangeManager().getIPRanges();
                } else if (types[i].equals("role")) {
                    _items = getRoleManager().getRoles();
                }
                for (int j = 0; j < operations.length; j++) {
                    if (getParameterAsString(operations[j] + "_credential_" + types[i]) != null) {
                        String roleId = getParameterAsString("role_id");

                        String accreditableId = getParameterAsString("accreditable_id");
                        Item item = null;
                        for (int k = 0; k < _items.length; k++) {
                            if (accreditableId.equals(_items[k].getId())) {
                                item = _items[k];
                            }
                        }

                        Role role = getRoleManager().getRole(roleId);

                        if (role == null) {
                            addErrorMessage("Role [" + roleId + "] does not exist!");
                        }

                        this.helper.manipulateCredential(item, role, operations[j]);
                    }
                }
            }
        } catch (final ProcessingException e) {
            addErrorMessage("Could not read a value.");
            getLogger().error("Could not read value for AccessControl usecase. " + e.toString());
        }

    }

    /**
     * Validates the request parameters.
     * @throws UsecaseException if an error occurs.
     */
    void validate() throws UsecaseException {
	    // do nothing
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {
        validate();
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();
        if (getParameterAsString("change_ssl") != null) {
            if (getParameterAsString("ssl") != null) {
                this.helper.setUrlSSLProtected(true);
            } else {
                this.helper.setUrlSSLProtected(false);
            }
        }

    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setParameter(java.lang.String,
     *      java.lang.Object)
     */
    public void setParameter(String name, Object value) {
        super.setParameter(name, value);
    }

    /**
     * @return PolicyHelper the policy helper
     */
    public PolicyHelper getHelper() {
        return this.helper;
    }

    /**
     * @return Item the item
     */
    public Item[] getItems() {
        return this.items;
    }
}