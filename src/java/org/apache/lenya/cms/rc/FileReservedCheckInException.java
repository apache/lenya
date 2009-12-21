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

import java.util.Date;


/**
 * Reserved check-in exception
 */
public class FileReservedCheckInException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String source = null;
    private Date date = null;
    private String username = null;
    private String typeString = null;
    private short type;

    /**
     * Creates a new FileReservedCheckInException object.
     *
     * @param _source The source document
     * @param rcml The RCML
     *
     * @throws Exception if an error occurs
     */
    public FileReservedCheckInException(String _source, RCML rcml)
        throws Exception {
        this.source = _source;

        try {
            RCMLEntry rcmlEntry = rcml.getLatestEntry();

            this.username = rcmlEntry.getIdentity();
            this.date = new Date(rcmlEntry.getTime());
            this.type = rcmlEntry.getType();

            if (this.type == RCML.co) {
                this.typeString = "Checkout";
            } else {
                this.typeString = "Checkin";
            }
        } catch (Exception exception) {
            throw new Exception("Unable to create FileReservedCheckInException object!");
        }
    }

    /**
     * Returns the exception message
     * @return The exception message
     */
    public String getMessage() {
        return "Unable to check in the file " + this.source + " because of a " + this.typeString +
        " by user " + this.username + " at " + this.date;
    }
    /**
     * Get the date
     * @return the date
     */
    public Date getDate() {
        return new Date(this.date.getTime());
    }

    /**
     * Get the typeString
     * @return the type string
     */
    public String getTypeString() {
        return this.typeString;
    }

    /**
     * Get the user name.
     * @return the user name
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Get the source
     * @return The source
     */
    public String getSource() {
        return this.source;
    }
}
