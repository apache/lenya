/*
 * Role.java
 *
 * Created on 9. April 2003, 12:40
 */

package org.apache.lenya.cms.ac;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author  nobby
 */
public class Role {

	/**
	 * Create a new instance of <code>Role</code>
	 * 
	 * @param name the name of the role
	 */    
    public Role(String name) {
        assert name != null;
        this.name = name;
    }
    
    private String name;
    
	private Set groups = new HashSet();

	/**
	 * Get the name
	 * 
	 * @return a <code>String</code>
	 */
    public String getName() {
        return name;
    }
    
    /**
     * Add a group
     * 
     * @param group the group to add
     */
    public void addGroup(Group group) {
        assert group != null;
    	groups.add(group);
    }
    
    /**
     * Remove a specific group
     * 
     * @param group the group to remove
     */
    public void removeGroup(Group group) {
    	groups.remove(group);
    }
    
    /**
     * Get all groups
     * 
     * @return an <code>Iterator</code>
     */
    public Iterator getGroups() {
    	return groups.iterator();
    }
    
    /** (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object otherObject) {
        boolean equals = false;
        
        if (otherObject instanceof Role) {
            Role otherRole = (Role) otherObject;
            equals = getName().equals(otherRole.getName());
        }
        
        return equals;
    }
    
    /** (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return getName().hashCode();
    }
    
    /** (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getName();
    }
    
    
}
