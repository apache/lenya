/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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

/* $Id: AbstractItem.java,v 1.2 2004/03/03 12:56:33 gregor Exp $  */

package org.apache.lenya.ac.impl;

import org.apache.lenya.ac.Item;

/**
 * Abstract superclass for all access control objects that can be
 * managed by an {@link ItemManager}. It is only used for code reuse. 
 */
public abstract class AbstractItem implements Item {

    private String id;
    private String description = "";
    private String name = "";
    
    /**
     * Ctor.
     */
    public AbstractItem() {
    }

    /**
     * Sets the ID.
     * @param string The ID.
     */
    protected void setId(String string) {
        assert isValidId(string);
        id = string;
    }

    /**
     * Returns the ID.
     * @return The ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the description of this object.
     * @return A string.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of this object.
     * @param description A string.
     */
    public void setDescription(String description) {
        assert description != null;
        this.description = description;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getId();
        
    }

    /**
     * Returns the name of this object.
     *
     * @return A <code>String</code>.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the full name
     *
     * @param name the new full name
     */
    public void setName(String name) {
        assert name != null;
        this.name = name;
    }
    
    /**
     * Checks if a string is a valid ID.
     * @param id The string to test.
     * @return A boolean value.
     */
    public static boolean isValidId(String id) {
        return id != null && id.matches("\\w+");
    }
    
    /**
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object otherObject) {
        boolean equals = false;

        if (getClass().isInstance(otherObject)) {
            AbstractItem otherManageable = (AbstractItem) otherObject;
            equals = getId().equals(otherManageable.getId());
        }

        return equals;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return getId().hashCode();
    }
    
}
