/*
$Id: AbstractItem.java,v 1.3 2003/08/07 12:14:49 andreas Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/

package org.apache.lenya.cms.ac;

/**
 * Abstract superclass for all access control objects that can be
 * managed by an {@link ItemManager}. It is only used for code reuse. 
 * 
 * @author andreas
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
