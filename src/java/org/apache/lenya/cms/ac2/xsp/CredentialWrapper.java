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
     * @return
     */
    public String getAccreditableId() {
        return accreditableId;
    }

    /**
     * @return
     */
    public String getAccreditableName() {
        return accreditableName;
    }

    /**
     * @return
     */
    public String getRoleId() {
        return roleId;
    }

    /**
     * @return
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * @return
     */
    public String getType() {
        return type;
    }

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
