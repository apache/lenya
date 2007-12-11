/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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

/* $Id$  */

package org.apache.lenya.cms.rc;

/**
 * A RCML entry
 */
public class RCMLEntry {
	
    private String identity = null;
    private long time = 0;
    private short type = 0;
    private String sessionId;

    /**
     * Creates a new RCMLEntry object.
     * @param sessionId The session ID.
     * @param _identity The identity of the person for this RCML
     * @param _time The time of the checkin / checkout
     */
    public RCMLEntry(String sessionId, String _identity, long _time) {
        this.identity = _identity;
        this.time = _time;
        this.sessionId = sessionId;
    }
    
    /**
     * Get the identity of the creator (i.e. the user name)
     * FIXME: this should be changed to an o.a.l.ac.Identity object
     * @return the identity
     */
    public String getIdentity() {
        return this.identity;
    }

    /**
     * Get the creation time.
     * @return the time
     */
    public long getTime() {
        return this.time;
    }

    /**
     * Get the type (checkin or checkout).
     * @see org.apache.lenya.cms.rc.RCML.ci
     * @see org.apache.lenya.cms.rc.RCML.co
     * @return the type
     */
    public short getType() {
        return this.type;
    }
    
    /**
     * Set the type (checkin or checkout).
     * @see org.apache.lenya.cms.rc.RCML.ci
     * @see org.apache.lenya.cms.rc.RCML.co
     * @param s the type
     */
    protected void setType(short s) {
        this.type = s;
    }
    
    public String getSessionId() {
        return this.sessionId;
    }

}
