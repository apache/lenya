/*
 * Situation.java
 *
 * Created on 8. April 2003, 17:42
 */

package org.apache.lenya.cms.workflow;

import org.apache.lenya.cms.ac.Role;
import org.apache.lenya.cms.ac.User;
import org.apache.lenya.workflow.Situation;

/**
 *
 * @author  andreas
 */
public class CMSSituation implements Situation {

    /** Creates a new instance of Situation */
    protected CMSSituation(Role roles[]) {
        this.roles = roles;
    }

    private Role roles[];

    /**
     * @return
     */
    public Role[] getRoles() {
        return roles;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "(roles: " + roles + ")"; 
    }

}
