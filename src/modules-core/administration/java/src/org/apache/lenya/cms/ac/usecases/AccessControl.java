package org.apache.lenya.cms.ac.usecases;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.cocoon.ProcessingException;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.Publication;
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
import org.apache.lenya.ac.Credential;
import org.apache.lenya.ac.ModifiablePolicy;
import org.apache.lenya.ac.InheritingPolicyManager;

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

    private Item[] items = null;

    protected static final String ADD = "add";

    protected static final String DELETE = "delete";

    protected static final String UP = "up";

    protected static final String DOWN = "down";

    protected static final String USER = "user";

    protected static final String GROUP = "group";

    protected static final String IPRANGE = "ipRange";

    protected static final String ROLE = "role";

    protected static final String SUB_USER = "subuser";

    protected static final String SUB_GROUP = "subgroup";

    protected static final String SUB_IPRANGE = "subipRange";

    private static String[] types = { USER, GROUP, IPRANGE, SUB_USER,
            SUB_GROUP, SUB_IPRANGE };

    private static String[] operations = { ADD, DELETE, DOWN, UP };

    protected static final String SSL = "ssl";

    protected static final String ANCESTOR_SSL = "ancestorSsl";

    protected static final String DOCUMENT = "document";

    protected static final String URL_CREDENTIALS = "urlCredentials";

    protected static final String SUB_CREDENTIALS = "subCredentials";

    protected static final String PARENT_CREDENTIALS = "parentCredentials";

    private static final String METHOD = "method";

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

            DocumentFactory map = getDocumentFactory();
            Document sourceDocument = map.getFromURL(getSourceURL());
            setParameter(DOCUMENT, sourceDocument);

            setParameter(SSL, Boolean.toString(isSSLProtected()));
            setParameter(ANCESTOR_SSL, Boolean
                    .toString(isAncestorSSLProtected()));

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
            setParameter(SUB_CREDENTIALS, getSubtreeCredentials());
            setParameter(PARENT_CREDENTIALS, getParentCredentials());

        } catch (final Exception e) {
            addErrorMessage("Could not read a value.");
            getLogger().error(
                    "Could not read value for AccessControl usecase. ", e);
        }

    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckPreconditions()
     */
    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();
        URLInformation info = new URLInformation(getSourceURL());
        String acArea = getParameterAsString(AC_AREA);
        if (!acArea.equals(Publication.LIVE_AREA)
                && !info.getArea().equals(acArea)) {
            addErrorMessage("This usecase can only be invoked in the configured area.");
        }
    }

    /**
     * Validates the request parameters.
     * 
     * @throws UsecaseException
     *             if an error occurs.
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

            for (int i = 0; i < types.length; i++) {
                for (int j = 0; j < operations.length; j++) {
                    String type = types[i];
                    String paramName = operations[j] + "Credential_" + type;
                    if (getParameterAsString(paramName) != null) {
                        boolean inherit = false;
                        if (type.startsWith("sub")) {
                            type = type.substring("sub".length());
                            inherit = true;
                        }
                        String roleId = getParameterAsString(ROLE);
                        String method = getParameterAsString(METHOD);

                        String id = getParameterAsString(type);
                        Item item = null;
                        if (type.equals(USER)) {
                            item = getUserManager().getUser(id);
                        } else if (type.equals(GROUP)) {
                            item = getGroupManager().getGroup(id);
                        } else if (type.equals(IPRANGE)) {
                            item = getIpRangeManager().getIPRange(id);
                        }
                        if (item == null) {
                            addErrorMessage("no_such_accreditable",
                                    new String[] { type, id });
                        } else {
                            Role role = getRoleManager().getRole(roleId);
                            if (role == null) {
                                addErrorMessage("role_no_such_role",
                                        new String[] { roleId });
                            }
                            manipulateCredential(item, role, operations[j],
                                    method, inherit);
                            if (inherit)
                                setParameter(SUB_CREDENTIALS,
                                        getSubtreeCredentials());
                            else
                                setParameter(URL_CREDENTIALS,
                                        getURICredentials());
                        }
                        deleteParameter(paramName);
                    }
                }
            }

        } catch (Exception e) {
            throw new UsecaseException(e);
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
     * @return Item the item
     */
    public Item[] getItems() {
        return this.items;
    }

    /**
     * Returns if one of the ancestors of this URL is SSL protected.
     * 
     * @return A boolean value.
     * @throws ProcessingException
     *             when something went wrong.
     */
    protected boolean isAncestorSSLProtected() throws ProcessingException {
        boolean ssl;
        try {
            String ancestorUrl = "";
            int lastSlashIndex = getPolicyURL().lastIndexOf("/");
            if (lastSlashIndex != -1) {
                ancestorUrl = getPolicyURL().substring(0, lastSlashIndex);
            }

            Policy policy = getPolicyManager().getPolicy(
                    getAccreditableManager(), ancestorUrl);
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
     * @throws ProcessingException
     *             when something went wrong.
     */
    protected boolean isSSLProtected() throws ProcessingException {
        boolean ssl;
        try {
            Policy policy = getPolicyManager().getPolicy(
                    getAccreditableManager(), getPolicyURL());
            ssl = policy.isSSLProtected();
        } catch (AccessControlException e) {
            throw new ProcessingException("Resolving policy failed: ", e);
        }
        return ssl;
    }

    /**
     * Sets if this URL is SSL protected.
     * 
     * @param ssl
     *            A boolean value.
     * @throws ProcessingException
     *             when something went wrong.
     */
    protected void setSSLProtected(boolean ssl) throws ProcessingException {
        try {
            ModifiablePolicy policy = (ModifiablePolicy) getPolicyManager()
                    .buildSubtreePolicy(getAccreditableManager(),
                            getPolicyURL());
            policy.setSSL(ssl);
            getPolicyManager().saveSubtreePolicy(getPolicyURL(), policy);
        } catch (AccessControlException e) {
            throw new ProcessingException("Resolving policy failed: ", e);
        }
    }

    protected InheritingPolicyManager getPolicyManager() {
        return (InheritingPolicyManager) getAccessController()
                .getPolicyManager();
    }

    protected AccreditableManager getAccreditableManager() {
        return getAccessController().getAccreditableManager();
    }

    /**
     * Changes a credential by adding or deleting an item for a role.
     * 
     * @param item
     *            The item to add or delete.
     * @param role
     *            The role.
     * @param operation
     *            The operation, either {@link #ADD}or {@link #DELETE}.
     * @param inherit
     * @param method2
     * @throws ProcessingException
     *             when something went wrong.
     */
    protected void manipulateCredential(Item item, Role role, String operation,
            String method, boolean inherit) throws ProcessingException {
        ModifiablePolicy policy = null;
        try {
            if (inherit)
                policy = (ModifiablePolicy) getPolicyManager()
                        .buildSubtreePolicy(getAccreditableManager(),
                                getPolicyURL());
            else
                policy = (ModifiablePolicy) getPolicyManager().buildURLPolicy(
                        getAccreditableManager(), getPolicyURL());
            Accreditable accreditable = (Accreditable) item;

            if (operation.equals(ADD)) {
                policy.addRole(accreditable, role, method);
            } else if (operation.equals(DELETE)) {
                policy.removeRole(accreditable, role);
            } else if (operation.equals(UP)) {
                policy.moveRoleUp(accreditable, role);
            } else if (operation.equals(DOWN)) {
                policy.moveRoleDown(accreditable, role);
            }
            if (inherit)
                getPolicyManager().saveSubtreePolicy(getPolicyURL(), policy);
            else
                getPolicyManager().saveURLPolicy(getPolicyURL(), policy);

        } catch (Exception e) {
            throw new ProcessingException("Manipulating credential failed: ", e);
        }
    }

    /**
     * Returns the URI credential wrappers for the request of this object model.
     * 
     * @return An array of CredentialWrappers.
     * @throws ProcessingException
     *             when something went wrong.
     */
    public CredentialWrapper[] getURICredentials() throws ProcessingException {
        return getCredentials(true, false);
    }

    /**
     * Returns the URI credential wrappers for the request of this object model.
     * 
     * @return An array of CredentialWrappers.
     * @throws ProcessingException
     *             when something went wrong.
     */
    public CredentialWrapper[] getSubtreeCredentials()
            throws ProcessingException {
        return getCredentials(false, true);
    }

    /**
     * Returns the credential wrappers for the parent URI of the URL belonging
     * to the request of this object model.
     * 
     * @return An array of CredentialWrappers.
     * @throws ProcessingException
     *             when something went wrong.
     */
    public CredentialWrapper[] getParentCredentials()
            throws ProcessingException {
        return getCredentials(false, false);
    }

    /**
     * Returns the credentials of the policy of the selected URL.
     * 
     * @param urlOnly
     *            If true, the URL policy credentials are returned. If false,
     *            the credentials of all ancestor policies are returned.
     * @return An array of CredentialWrappers.
     * @throws ProcessingException
     *             when something went wrong.
     */
    public CredentialWrapper[] getCredentials(boolean urlOnly, boolean inherit)
            throws ProcessingException {

        List credentials = new ArrayList();

        ModifiablePolicy policies[] = getPolicies(urlOnly, inherit);
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
                        "AccessControlException - receiving credential failed: ",
                        e);
            }
        }
        for (Iterator i = policyCredentials.iterator(); i.hasNext();) {
            Credential credential = (Credential) i.next();
            Accreditable accreditable = credential.getAccreditable();
            Role role = credential.getRole();
            String method = credential.getMethod();
            credentials.add(new CredentialWrapper(accreditable, role, method));
        }
        return (CredentialWrapper[]) credentials
                .toArray(new CredentialWrapper[credentials.size()]);
    }

    /**
     * Returns the policies for a certain URL.
     * 
     * @param onlyUrl
     *            If true, only the URL policies are returned. Otherwise, all
     *            ancestor policies are returned.
     * @return An array of DefaultPolicy objects.
     * @throws ProcessingException
     *             when something went wrong.
     */
    protected ModifiablePolicy[] getPolicies(boolean onlyUrl, boolean inherit)
            throws ProcessingException {

        ModifiablePolicy[] policies;

        try {
            if (onlyUrl) {
                policies = new ModifiablePolicy[1];
                AccreditableManager policyManager = getAccreditableManager();
                policies[0] = (ModifiablePolicy) getPolicyManager()
                        .buildURLPolicy(policyManager, getPolicyURL());
            } else if (!onlyUrl && inherit) {
                policies = new ModifiablePolicy[1];
                AccreditableManager policyManager = getAccreditableManager();
                policies[0] = (ModifiablePolicy) getPolicyManager()
                        .buildSubtreePolicy(policyManager, getPolicyURL());
            } else {
                String ancestorUrl = "";

                String currentUrl = getPolicyURL();
                if (currentUrl.endsWith("/")) {
                    currentUrl = currentUrl.substring(0,
                            currentUrl.length() - 1);
                }

                int lastSlashIndex = currentUrl.lastIndexOf("/");
                if (lastSlashIndex != -1) {
                    ancestorUrl = currentUrl.substring(0, lastSlashIndex);
                }
                Policy[] pArray = getPolicyManager().getPolicies(
                        getAccreditableManager(), ancestorUrl);
                policies = new ModifiablePolicy[pArray.length];
                for (int i = 0; i < pArray.length; i++) {
                    policies[i] = (ModifiablePolicy) pArray[i];
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
        String url = "/" + info.getPublicationId() + "/" + area
                + info.getDocumentUrl();
        return url;
    }

}