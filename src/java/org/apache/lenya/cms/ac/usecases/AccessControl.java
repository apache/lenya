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
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.usecase.UsecaseException;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Item;
import org.apache.lenya.ac.Policy;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.impl.DefaultAccessController;
import org.apache.lenya.ac.impl.DefaultPolicy;
import org.apache.lenya.ac.impl.InheritingPolicyManager;

/**
 * Usecase to display the AccessControl tab in the site area for a document. This is a mix-in class
 * that ideally would inherit both from AccessControlUsecase and DocumentUsecase. FIXME i just took
 * the appropriate code from DocumentUsecase, maybe its possible to have a saner inheritance?
 * 
 * @version $Id: AccessControl.java 123980 2005-01-03 15:00:14Z andreas $
 */

public class AccessControl extends AccessControlUsecase {

    private Item[] items = null;

    protected static final String ADD = "add";
    protected static final String DELETE = "delete";

    private static String[] types = { "user", "group", "iprange", "role" };
    private static String[] operations = { ADD, DELETE };

    protected static final String SSL = "ssl";
    protected static final String ANCESTOR_SSL = "ancestorSsl";
    protected static final String DOCUMENT = "document";
    private String COMPLETE_AREA = "private.completeArea";

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
            URLInformation info = new URLInformation(getSourceURL());
            setParameter(COMPLETE_AREA, info.getCompleteArea());

            DocumentIdentityMap map = (DocumentIdentityMap) getUnitOfWork().getIdentityMap();
            Document sourceDocument = map.getFromURL(getSourceURL());
            setParameter(DOCUMENT, sourceDocument);

            setParameter(SSL, Boolean.toString(isSSLProtected()));
            setParameter(ANCESTOR_SSL, Boolean.toString(isAncestorSSLProtected()));

            Role[] roles = getRoleManager().getRoles();
            String visitorRole = "";
            for (int i = 0; i < roles.length; i++) {
                if (roles[i].getId().equals("visit")) {
                    visitorRole = roles[i].getId();
                }
            }

            setParameter("visitorRole", visitorRole);

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
                            addErrorMessage("role_no_such_role", new String[] { roleId });
                        }

                        manipulateCredential(item, role, operations[j]);
                    }
                }
            }
        } catch (final Exception e) {
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
    public void doExecute() throws Exception {
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#advance()
     */
    public void advance() throws UsecaseException {
        super.advance();
        try {
            if (getParameterAsString("change_ssl") != null) {
                String ssl = getParameterAsString("ssl");
                if (ssl != null && ssl.equalsIgnoreCase(Boolean.toString(true))) {
                    setSSLProtected(true);
                } else {
                    setSSLProtected(false);
                }
                deleteParameter("change_ssl");
                deleteParameter("ssl");
            }
        } catch (Exception e) {
            throw new UsecaseException(e);
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setParameter(java.lang.String, java.lang.Object)
     */
    public void setParameter(String name, Object value) {
        super.setParameter(name, value);
    }

    /**
     * @return Item the item
     */
    public Item[] getItems() {
        return this.items;
    }

    /**
     * Returns if one of the ancestors of this URL is SSL protected.
     * @return A boolean value.
     * @throws ProcessingException when something went wrong.
     */
    protected boolean isAncestorSSLProtected() throws ProcessingException {
        boolean ssl;
        try {
            String ancestorUrl = "";
            int lastSlashIndex = getSourceURL().lastIndexOf("/");
            if (lastSlashIndex != -1) {
                ancestorUrl = getSourceURL().substring(0, lastSlashIndex);
            }

            Policy policy = getPolicyManager().getPolicy(getAccreditableManager(), ancestorUrl);
            ssl = policy.isSSLProtected();
        } catch (AccessControlException e) {
            throw new ProcessingException("Resolving policy failed: ", e);
        }
        return ssl;
    }

    /**
     * Returns if one of the ancestors of this URL is SSL protected.
     * @return A boolean value.
     * @throws ProcessingException when something went wrong.
     */
    protected boolean isSSLProtected() throws ProcessingException {
        boolean ssl;
        try {
            Policy policy = getPolicyManager().getPolicy(getAccreditableManager(), getSourceURL());
            ssl = policy.isSSLProtected();
        } catch (AccessControlException e) {
            throw new ProcessingException("Resolving policy failed: ", e);
        }
        return ssl;
    }

    /**
     * Sets if this URL is SSL protected.
     * @param ssl A boolean value.
     * @throws ProcessingException when something went wrong.
     */
    protected void setSSLProtected(boolean ssl) throws ProcessingException {
        try {
            DefaultPolicy policy = getPolicyManager().buildSubtreePolicy(getAccreditableManager(),
                    getSourceURL());
            policy.setSSL(ssl);
            getPolicyManager().saveSubtreePolicy(getSourceURL(), policy);
        } catch (AccessControlException e) {
            throw new ProcessingException("Resolving policy failed: ", e);
        }
    }

    protected InheritingPolicyManager getPolicyManager() {
        return (InheritingPolicyManager) ((DefaultAccessController) getAccessController())
                .getPolicyManager();
    }

    protected AccreditableManager getAccreditableManager() {
        return ((DefaultAccessController) getAccessController()).getAccreditableManager();
    }

    /**
     * Changes a credential by adding or deleting an item for a role.
     * @param item The item to add or delete.
     * @param role The role.
     * @param operation The operation, either {@link #ADD}or {@link #DELETE}.
     * @throws ProcessingException when something went wrong.
     */
    protected void manipulateCredential(Item item, Role role, String operation)
            throws ProcessingException {

        try {
            DefaultPolicy policy = getPolicyManager().buildSubtreePolicy(getAccreditableManager(),
                    getSourceURL());
            Accreditable accreditable = (Accreditable) item;

            if (operation.equals(ADD)) {
                policy.addRole(accreditable, role);
            } else if (operation.equals(DELETE)) {
                policy.removeRole(accreditable, role);
            }

            getPolicyManager().saveSubtreePolicy(getSourceURL(), policy);

        } catch (Exception e) {
            throw new ProcessingException("Manipulating credential failed: ", e);
        }
    }
}