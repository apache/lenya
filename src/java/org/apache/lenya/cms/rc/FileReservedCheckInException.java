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

/* $Id: FileReservedCheckInException.java,v 1.13 2004/03/01 16:18:22 gregor Exp $  */

package org.apache.lenya.cms.rc;

import java.util.Date;


/**
 * Reserved check-in exception
 */
public class FileReservedCheckInException extends Exception {
    private String source = null;
    private Date date = null;
    private String username = null;
    private String typeString = null;
    private short type;

    /**
     * Creates a new FileReservedCheckInException object.
     *
     * @param source DOCUMENT ME!
     * @param rcml DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public FileReservedCheckInException(String source, RCML rcml)
        throws Exception {
        this.source = source;

        try {
            RCMLEntry rcmlEntry = rcml.getLatestEntry();

            username = rcmlEntry.getIdentity();
            date = new Date(rcmlEntry.getTime());
            type = rcmlEntry.getType();

            if (type == RCML.co) {
                typeString = "Checkout";
            } else {
                typeString = "Checkin";
            }
        } catch (Exception exception) {
            throw new Exception("Unable to create FileReservedCheckInException object!");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getMessage() {
        return "Unable to check in the file " + this.source + " because of a " + this.typeString +
        " by user " + this.username + " at " + this.date;
    }
    /**
     * Get the date
     * 
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Get the typeString
     * 
     * @return the type string
     */
    public String getTypeString() {
        return typeString;
    }

    /**
     * Get the user name.
     * 
     * @return the user name
     */
    public String getUsername() {
        return username;
    }

    /**
     * Get source
     * 
     * @return source
     */
    public String getSource() {
        return source;
    }
}
