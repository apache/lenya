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

/* $Id: RCMLEntry.java,v 1.9 2004/03/01 16:18:22 gregor Exp $  */

package org.apache.lenya.cms.rc;

public class RCMLEntry {
	
    private String identity = null;
    private long time = 0;
    private short type = 0;

    /**
     * Creates a new RCMLEntry object.
     *
     * @param identity DOCUMENT ME!
     * @param time DOCUMENT ME!
     */
    public RCMLEntry(String identity, long time) {
        this.identity = identity;
        this.time = time;
    }
    
    /**
     * Get the identity.
     *  
     * @return the identity
     */
    public String getIdentity() {
        return identity;
    }

    /**
     * Get the time.
     * 
     * @return the time
     */
    public long getTime() {
        return time;
    }

    /**
     * Get the type.
     * 
     * @return the type
     */
    public short getType() {
        return type;
    }

    /**
     * Set the type.
     * 
     * @param s the type
     */
    protected void setType(short s) {
        type = s;
    }

}
