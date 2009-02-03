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
 * A checkin entry
 */
public class CheckInEntry extends RCMLEntry {
    /**
     * Creates a new CheckInEntry object.
     * @param sessionId The session ID.
     * @param identity The identity to use
     * @param time The time
     * @param version The version number.
     * @param backup If this entry has a backup.
     */
    public CheckInEntry(String sessionId, String identity, long time, int version, boolean backup) {
        super(sessionId, identity, time);
        setType(RCML.ci);
        this.version = version;
        this.backup = backup;
    }
    
    /**
     * @return The version number.
     */
    public int getVersion() {
        return this.version;
    }

    private int version = 0;
    private boolean backup;
    
    public boolean hasBackup() {
        return this.backup;
    }
    
}
