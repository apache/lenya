/*
 * Created on Jul 24, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.lenya.cms.ac2.xsp;

import org.apache.lenya.cms.ac.Group;
import org.apache.lenya.cms.ac.IPRange;
import org.apache.lenya.cms.ac.Item;
import org.apache.lenya.cms.ac.Role;
import org.apache.lenya.cms.ac.User;
import org.apache.lenya.cms.ac2.Accreditable;

/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class CredentialWrapper {

    /**
     * Returns the accreditable ID.
     * @return A string.
     */
    public String getAccreditableId() {
        return accreditableId;
    }

    /**
     * Returns the accreditable name.
     * @return A string.
     */
    public String getAccreditableName() {
        return accreditableName;
    }

    /**
     * Returns the role ID.
     * @return A string.
     */
    public String getRoleId() {
        return roleId;
    }

    /**
     * Returns the role name.
     * @return A string.
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * Returns the accreditable type ({@link #USER}, {@link GROUP}, or {@link IPRANGE})
     * @return A string.
     */
    public String getType() {
        return type;
    }

    /**
     * Ctor.
     * @param accreditable The accreditable of the credential to wrap.
     * @param role The role of the credential to wrap.
     */
    public CredentialWrapper(Accreditable accreditable, Role role) {
        if (accreditable instanceof Item) {
            Item item = (Item) accreditable;
            accreditableId = item.getId();
            accreditableName = item.getName();
        
            if (item instanceof User) {
                type = USER;
            }
            else if (item instanceof Group) {
                type = GROUP;
            }
            else if (item instanceof IPRange) {
                type = IPRANGE;
            }
        }
        else {
            accreditableId = "world";
            accreditableName = "the world";
            type = "world";
        }
        roleId = role.getId();
        roleName = role.getName();
        
    }
    
    public static final String USER = "user";
    public static final String GROUP = "group";
    public static final String IPRANGE = "iprange";
    
    private String type;
    private String accreditableId;
    private String accreditableName;
    private String roleId;
    private String roleName;

}
