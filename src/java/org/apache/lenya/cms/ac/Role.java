/*
 * Role.java
 *
 * Created on 9. April 2003, 12:40
 */

package org.lenya.cms.ac;

/**
 *
 * @author  nobby
 */
public class Role {
    
    /** Creates a new instance of Role */
    public Role(String name) {
        assert name != null;
    }
    
    private String name;
    
    public String getName() {
        return name;
    }
    
    public boolean equals(Object otherObject) {
        boolean equals = false;
        
        if (otherObject instanceof Role) {
            Role otherRole = (Role) otherObject;
            equals = getName().equals(otherRole.getName());
        }
        else {
            equals = super.equals(otherObject);
        }
        
        return equals;
    }
    
    public int hashCode() {
        return getName().hashCode();
    }
    
    
}
