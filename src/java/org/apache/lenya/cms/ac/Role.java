/*
 * Role.java
 *
 * Created on 9. April 2003, 12:40
 */

package org.apache.lenya.cms.ac;

/**
 * A Role embodies the privilege to do certain things. 
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann</a>
 */
public class Role {
    
    /**
     * Creates a new instance of Role.
     */
    public Role() {
    }
    
    /**
     * Creates a new instance of Role.
     * @param name The role name.
     */
    public Role(String name) {
        assert name != null;
        this.name = name;
    }
    
    private String name;
    
    /**
     * Returns the name of this role.
     * @return A string.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name of this role.
     * @param name The name.
     */
    public void setName(String name) {
        assert name != null && !"".equals(name);
        this.name = name;
    }
    
    
    /**
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object otherObject) {
        boolean equals = false;
        
        if (otherObject instanceof Role) {
            Role otherRole = (Role) otherObject;
            equals = getName().equals(otherRole.getName());
        }
        
        return equals;
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return getName().hashCode();
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getName();
    }
    
    
}
