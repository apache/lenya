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
package org.apache.lenya.cms.ac.usecases;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.cocoon.ProcessingException;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.URLInformation;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.Accreditable;
import org.apache.lenya.ac.AccreditableManager;
import org.apache.lenya.ac.Group;
import org.apache.lenya.ac.IPRange;
import org.apache.lenya.ac.Item;
import org.apache.lenya.ac.Policy;
import org.apache.lenya.ac.Role;
import org.apache.lenya.ac.User;
import org.apache.lenya.ac.Credential;
import org.apache.lenya.ac.ModifiablePolicy;
import org.apache.lenya.ac.InheritingPolicyManager;
import org.apache.lenya.ac.World;

/**
 * Usecase to display the AccessControl tab in the site area for a document.
 * This is a mix-in class that ideally would inherit both from
 * AccessControlUsecase and DocumentUsecase. FIXME i just took the appropriate
 * code from DocumentUsecase, maybe its possible to have a saner inheritance?
 * 
 * @version $Id: AccessControl.java 408702 2006-05-22 16:03:49Z andreas $
 */

public class AccessControl extends AccessControlUsecase {

    protected static final String AC_AREA = "acArea";
    protected static final String ADD = "add";
    protected static final String DELETE = "delete";
    protected static final String UP = "up";
    protected static final String DOWN = "down";
    protected static final String USER = "user";
    protected static final String GROUP = "group";
    protected static final String IPRANGE = "ipRange";
    protected static final String WORLD = "world";
    protected static final String ROLE = "role";
    protected static final String SUB_USER = "subuser";
    protected static final String SUB_GROUP = "subgroup";
    protected static final String SUB_IPRANGE = "subipRange";

    private static String[] types = { USER, GROUP, IPRANGE, SUB_USER, SUB_GROUP, SUB_IPRANGE, WORLD };
    private static String[] operations = { ADD, DELETE, DOWN, UP };

    protected static final String SSL = "ssl";
    protected static final String ANCESTOR_SSL = "ancestorSsl";
    protected static final String DOCUMENT = "document";
    protected static final String SUB_CREDENTIALS = "subCredentials";
    protected static final String PARENT_CREDENTIALS = "parentCredentials";
    private static final String METHOD = "method";
    private String COMPLETE_AREA = "private.completeArea";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();

        try {
            URLInformation info = new URLInformation(getSourceURL());
            setParameter(COMPLETE_AREA, info.getCompleteArea());

            DocumentFactory map = getDocumentFactory();
            if (map.isDocument(getSourceURL())) {
                Document sourceDocument = map.getFromURL(getSourceURL());
                setParameter(DOCUMENT, sourceDocument);
            }

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
            Set roleIds = new TreeSet();
            for (int i = 0; i < roles.length; i++) {
                if (roles[i].isAssignable()) {
                    roleIds.add(roles[i].getId());
                    if (roles[i].getId().equals("visit")) {
                        visitorRole = roles[i].getId();
                    }
                }
            }
            setParameter("roles", roleIds.toArray(new String[roleIds.size()]));
            setParameter("visitorRole", visitorRole);

            setParameter(SUB_CREDENTIALS, getSubtreeCredentials());
            setParameter(PARENT_CREDENTIALS, getParentCredentials());

        } catch (final Exception e) {
            addErrorMessage("Could not read a value.");
            getLogger().error("Could not read value for AccessControl usecase. ", e);
        }

    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckPreconditions()
     */
    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();
        URLInformation info = new URLInformation(getSourceURL());
        String acArea = getParameterAsString(AC_AREA);
        if (!acArea.equals(Publication.LIVE_AREA) && !info.getArea().equals(acArea)) {
            addErrorMessage("This usecase can only be invoked in the configured area.");
        }
    }

    protected void doCheckExecutionConditions() throws Exception {
        super.doCheckExecutionConditions();
        for (int i = 0; i < types.length; i++) {
            for (int j = 0; j < operations.length; j++) {
                String type = types[i];
                String paramName = operations[j] + "Credential_" + type;
                if (getParameterAsString(paramName) != null) {
                    String roleId = getParameterAsString(ROLE);
                    String id = getParameterAsString(type);
                    Accreditable item = getAccreditable(type, id);
                    if (item == null) {
                        addErrorMessage("no_such_accreditable", new String[] { type, id });
                    } else {
                        Role role = getRoleManager().getRole(roleId);
                        if (role == null) {
                            addErrorMessage("role_no_such_role", new String[] { roleId });
                        }
                        if (!role.isAssignable()) {
                            addErrorMessage("cannot-assign-role", new String[] { roleId });
                        }
                        if (operations[j].equals(ADD)) {
                            ModifiablePolicy policy = getPolicy();
                            if (containsCredential(policy, item, role)) {
                                addErrorMessage("credential-already-contained", new String[] {
                                        ((Item) item).getId(), role.getId() });
                            }
                        }
                    }
                    if (hasErrors()) {
                        deleteParameter(paramName);
                    }
                }
            }
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    public void doExecute() throws Exception {
        super.doExecute();
        if (getParameterAsString("change_ssl") != null) {
            String ssl = getBooleanCheckboxParameter("ssl");
            setSSLProtected(Boolean.valueOf(ssl).booleanValue());
        }

        for (int i = 0; i < types.length; i++) {
            for (int j = 0; j < operations.length; j++) {
                String type = types[i];
                String paramName = operations[j] + "Credential_" + type;
                if (getParameterAsString(paramName) != null) {
                    String roleId = getParameterAsString(ROLE);
                    String method = getParameterAsString(METHOD);
                    String id = getParameterAsString(type);
                    Accreditable item = getAccreditable(type, id);
                    Role role = getRoleManager().getRole(roleId);
                    manipulateCredential(item, role, operations[j], method);
                    setParameter(SUB_CREDENTIALS, getSubtreeCredentials());
                    deleteParameter(paramName);
                }
            }
        }
    }

    protected Accreditable getAccreditable(String type, String id) {
        Accreditable item = null;
        if (type.equals(USER)) {
            item = getUserManager().getUser(id);
        } else if (type.equals(GROUP)) {
            item = getGroupManager().getGroup(id);
        } else if (type.equals(IPRANGE)) {
            item = getIpRangeManager().getIPRange(id);
        } else if (type.equals(WORLD)) {
            item = World.getInstance();
        }
        return item;
    }

    /**
     * Returns if one of the ancestors of this URL is SSL protected.
     * 
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
     * 
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
     * 
     * @param ssl A boolean value.
     * @throws ProcessingException when something went wrong.
     */
    protected void setSSLProtected(boolean ssl) throws ProcessingException {
        try {
            ModifiablePolicy policy = getPolicy();
            policy.setSSL(ssl);
            getPolicyManager().saveSubtreePolicy(getPolicyURL(), policy);
        } catch (AccessControlException e) {
            throw new ProcessingException("Resolving policy failed: ", e);
        }
    }

    protected InheritingPolicyManager getPolicyManager() {
        return (InheritingPolicyManager) getAccessController().getPolicyManager();
    }

    protected AccreditableManager getAccreditableManager() {
        return getAccessController().getAccreditableManager();
    }

    /**
     * Changes a credential by adding or deleting an item for a role.
     * 
     * @param accreditable The accreditable to add or delete.
     * @param role The role.
     * @param operation The operation, either {@link #ADD}or {@link #DELETE}.
     * @param method
     * @throws ProcessingException when something went wrong.
     */
    protected void manipulateCredential(Accreditable accreditable, Role role, String operation,
            String method) throws ProcessingException {
        try {
            ModifiablePolicy policy = getPolicy();

            if (operation.equals(ADD)) {
                policy.addRole(accreditable, role, method);
            } else if (operation.equals(DELETE)) {
                policy.removeRole(accreditable, role);
            } else if (operation.equals(UP)) {
                policy.moveRoleUp(accreditable, role);
            } else if (operation.equals(DOWN)) {
                policy.moveRoleDown(accreditable, role);
            }
            getPolicyManager().saveSubtreePolicy(getPolicyURL(), policy);

        } catch (Exception e) {
            throw new ProcessingException("Manipulating credential failed: ", e);
        }
    }

    protected ModifiablePolicy getPolicy() throws AccessControlException {
        return (ModifiablePolicy) getPolicyManager().buildSubtreePolicy(getAccreditableManager(),
                getPolicyURL());
    }

    protected boolean containsCredential(ModifiablePolicy policy, Accreditable accreditable,
            Role role) throws AccessControlException {
        Credential[] credentials = policy.getCredentials();
        boolean contains = false;
        int i = 0;
        while (!contains && i < credentials.length) {
            Accreditable credAccr = credentials[i].getAccreditable();
            Role credRole = credentials[i].getRole();
            contains = credAccr.equals(accreditable) && credRole.equals(role);
            i++;
        }
        return contains;
    }

    /**
     * Returns the credential wrappers for the request of this object model.
     * 
     * @return An array of CredentialWrappers.
     * @throws ProcessingException when something went wrong.
     */
    public CredentialWrapper[] getSubtreeCredentials() throws ProcessingException {
        return getCredentials(true);
    }

    /**
     * Returns the credential wrappers for the parent URI of the URL belonging
     * to the request of this object model.
     * 
     * @return An array of CredentialWrappers.
     * @throws ProcessingException when something went wrong.
     */
    public CredentialWrapper[] getParentCredentials() throws ProcessingException {
        return getCredentials(false);
    }

    /**
     * Returns the credentials of the policy of the selected URL.
     * 
     * @return An array of CredentialWrappers.
     * @throws ProcessingException when something went wrong.
     */
    public CredentialWrapper[] getCredentials(boolean inherit) throws ProcessingException {

        List credentials = new ArrayList();

        ModifiablePolicy policies[] = getPolicies(inherit);
        List policyCredentials = new ArrayList();
        for (int i = 0; i < policies.length; i++) {
            Credential[] creds;
            try {
                creds = policies[i].getCredentials();
                for (int j = 0; j < creds.length; j++) {
                    policyCredentials.add(creds[j]);
                }
            } catch (AccessControlException e) {
                throw new ProcessingException(
                        "AccessControlException - receiving credential failed: ", e);
            }
        }
        for (Iterator i = policyCredentials.iterator(); i.hasNext();) {
            Credential credential = (Credential) i.next();
            Accreditable accreditable = credential.getAccreditable();
            Role role = credential.getRole();
            String method = credential.getMethod();
            credentials.add(new CredentialWrapper(accreditable, role, method));
        }
        return (CredentialWrapper[]) credentials.toArray(new CredentialWrapper[credentials.size()]);
    }

    /**
     * Returns the policies for a certain URL.
     * 
     * @param inherit If true, all ancestor policies are returned. Otherwise,
     *            only the URL policies are returned.
     * @return An array of DefaultPolicy objects.
     * @throws ProcessingException when something went wrong.
     */
    protected ModifiablePolicy[] getPolicies(boolean inherit) throws ProcessingException {

        ModifiablePolicy[] policies;

        try {
            if (inherit) {
                policies = new ModifiablePolicy[1];
                AccreditableManager policyManager = getAccreditableManager();
                policies[0] = (ModifiablePolicy) getPolicyManager().buildSubtreePolicy(
                        policyManager, getPolicyURL());
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
                Policy[] pArray = getPolicyManager().getPolicies(getAccreditableManager(),
                        ancestorUrl);
                policies = new ModifiablePolicy[pArray.length];
                for (int i = 0; i < pArray.length; i++) {
                    policies[policies.length - 1 - i] = (ModifiablePolicy) pArray[i];
                }
            }
        } catch (AccessControlException e) {
            throw new ProcessingException(e);
        }

        return policies;
    }

    protected String getPolicyURL() {
        String infoUrl = getSourceURL();
        URLInformation info = new URLInformation(infoUrl);

        String area = getParameterAsString(AC_AREA);
        String url = "/" + info.getPublicationId() + "/" + area + info.getDocumentUrl();
        return url;
    }

}
