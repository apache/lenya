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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.cocoon.ProcessingException;
import org.apache.lenya.cms.ac.cocoon.CredentialWrapper;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.usecase.UsecaseException;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.IPRange;
import org.apache.lenya.ac.Item;
import org.apache.lenya.ac.Policy;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.impl.Credential;
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
    protected static final String URL_CREDENTIALS = "urlCredentials";
    protected static final String PARENT_CREDENTIALS = "parentCredentials";
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

            User[] users = getUserManager().getUsers();
            String[] userIds = new String[users.length];
            for (int i = 0; i < users.length; i++) {
                userIds[i] = users[i].getId();
            }
            Arrays.sort(userIds);
            setParameter("users", userIds);

            Group[] groups = getGroupManager().getGroups();
            String[] groupIds = new String[groups.length];
            for (int i = 0; i < groups.length; i++) {
                groupIds[i] = groups[i].getId();
            }
            Arrays.sort(groupIds);
            setParameter("groups", groupIds);

            IPRange[] ipRanges = getIpRangeManager().getIPRanges();
            String[] ipRangeIds = new String[ipRanges.length];
            for (int i = 0; i < ipRanges.length; i++) {
                ipRangeIds[i] = ipRanges[i].getId();
            }
            Arrays.sort(ipRangeIds);
            setParameter("ipRanges", ipRangeIds);

            Role[] roles = getRoleManager().getRoles();
            String visitorRole = "";
            String[] roleIds = new String[roles.length];
            for (int i = 0; i < roles.length; i++) {
                roleIds[i] = roles[i].getId();
                if (roles[i].getId().equals("visit")) {
                    visitorRole = roles[i].getId();
                }
            }
            Arrays.sort(roleIds);
            setParameter("roles", roleIds);
            setParameter("visitorRole", visitorRole);
            
            setParameter(URL_CREDENTIALS, getURICredentials());
            setParameter(PARENT_CREDENTIALS, getParentCredentials());

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
            getLogger().error("Could not read value for AccessControl usecase. ", e);
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
            int lastSlashIndex = getPolicyURL().lastIndexOf("/");
            if (lastSlashIndex != -1) {
                ancestorUrl = getPolicyURL().substring(0, lastSlashIndex);
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
            Policy policy = getPolicyManager().getPolicy(getAccreditableManager(), getPolicyURL());
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
                    getPolicyURL());
            policy.setSSL(ssl);
            getPolicyManager().saveSubtreePolicy(getPolicyURL(), policy);
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
                    getPolicyURL());
            Accreditable accreditable = (Accreditable) item;

            if (operation.equals(ADD)) {
                policy.addRole(accreditable, role);
            } else if (operation.equals(DELETE)) {
                policy.removeRole(accreditable, role);
            }

            getPolicyManager().saveSubtreePolicy(getPolicyURL(), policy);

        } catch (Exception e) {
            throw new ProcessingException("Manipulating credential failed: ", e);
        }
    }

    /**
     * Returns the URI credential wrappers for the request of this object model.
     * @return An array of CredentialWrappers.
     * @throws ProcessingException when something went wrong.
     */
    public CredentialWrapper[] getURICredentials() throws ProcessingException {
        return getCredentials(true);
    }

    /**
     * Returns the credential wrappers for the parent URI of the URL belonging to the request of
     * this object model.
     * @return An array of CredentialWrappers.
     * @throws ProcessingException when something went wrong.
     */
    public CredentialWrapper[] getParentCredentials() throws ProcessingException {
        return getCredentials(false);
    }

    /**
     * Returns the credentials of the policy of the selected URL.
     * @param urlOnly If true, the URL policy credentials are returned. If false, the credentials of
     *            all ancestor policies are returned.
     * @return An array of CredentialWrappers.
     * @throws ProcessingException when something went wrong.
     */
    public CredentialWrapper[] getCredentials(boolean urlOnly) throws ProcessingException {

        List credentials = new ArrayList();

        DefaultPolicy policies[] = getPolicies(urlOnly);
        List policyCredentials = new ArrayList();
        for (int i = 0; i < policies.length; i++) {
            Credential[] creds = policies[i].getCredentials();
            for (int j = 0; j < creds.length; j++) {
                policyCredentials.add(creds[j]);
            }
        }
        for (Iterator i = policyCredentials.iterator(); i.hasNext();) {
            Credential credential = (Credential) i.next();
            Accreditable accreditable = credential.getAccreditable();
            Role[] roles = credential.getRoles();
            for (int j = 0; j < roles.length; j++) {
                credentials.add(new CredentialWrapper(accreditable, roles[j]));
            }
        }
        return (CredentialWrapper[]) credentials.toArray(new CredentialWrapper[credentials.size()]);
    }

    /**
     * Returns the policies for a certain URL.
     * @param onlyUrl If true, only the URL policies are returned. Otherwise, all ancestor policies
     *            are returned.
     * @return An array of DefaultPolicy objects.
     * @throws ProcessingException when something went wrong.
     */
    protected DefaultPolicy[] getPolicies(boolean onlyUrl) throws ProcessingException {

        DefaultPolicy[] policies;

        try {
            if (onlyUrl) {
                policies = new DefaultPolicy[1];
                AccreditableManager policyManager = getAccreditableManager();
                policies[0] = getPolicyManager().buildSubtreePolicy(policyManager, getPolicyURL());
            } else {
                String ancestorUrl = "";

                String currentUrl = getPolicyURL();
                if (currentUrl.endsWith("/")) {
                    currentUrl = currentUrl.substring(0, currentUrl.length() - 1);
                }

                int lastSlashIndex = currentUrl.lastIndexOf("/");
                if (lastSlashIndex != -1) {
                    ancestorUrl = currentUrl.substring(0, lastSlashIndex);
                }
                policies = getPolicyManager().getPolicies(getAccreditableManager(), ancestorUrl);
            }
        } catch (AccessControlException e) {
            throw new ProcessingException(e);
        }

        return policies;
    }
    
    protected String getPolicyURL() {
        String infoUrl = getSourceURL();
        URLInformation info = new URLInformation(infoUrl);
        
        String area = getParameterAsString("acArea");
        String url = "/" + info.getPublicationId() + "/" + area + info.getDocumentUrl();
        return url;
    }

}