/*
 * User.java
 *
 * Created on 9. April 2003, 12:40
 */

package org.apache.lenya.cms.ac;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author  nobby
 */
public class User {
    
    /** Creates a new instance of User */
    public User() {
    }
    
    private Set roles = new HashSet();
    
    public void addRole(Role role) {
        assert role != null;
        roles.add(role);
    }
    
    public Role[] getRoles() {
        return (Role[]) roles.toArray(new Role[roles.size()]);
    }
    
    public boolean hasRole(Role role) {
        return roles.contains(role);
    }
    
}
