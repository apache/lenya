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
    
    /** Creates a new instance of Role */
    public Role(String name) {
        assert name != null;
        this.name = name;
    }
    
    protected String name;
    
	private Set groups = new HashSet();

    public String getName() {
        return name;
    }
    
    public void addGroup(Group group) {
        assert group != null;
    	groups.add(group);
    }
    
    public void removeGroup(Group group) {
    	groups.remove(group);
    }
    
    public Iterator getGroups() {
    	return groups.iterator();
    }
    
    public boolean equals(Object otherObject) {
        boolean equals = false;
        
        if (otherObject instanceof Role) {
            Role otherRole = (Role) otherObject;
            equals = getName().equals(otherRole.getName());
        }
        
        return equals;
    }
    
    public int hashCode() {
        return getName().hashCode();
    }
    
    public String toString() {
        return getName();
    }
    
    
}
