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

/* $Id: FileReservedCheckOutException.java,v 1.10 2004/03/01 16:18:22 gregor Exp $  */

package org.apache.lenya.cms.rc;

import java.util.Date;


public class FileReservedCheckOutException extends Exception {
    private String source = null;
    private Date checkOutDate = null;
    private String checkOutUsername = null;

    /**
     * Creates a new FileReservedCheckOutException object.
     *
     * @param source DOCUMENT ME!
     * @param rcml DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public FileReservedCheckOutException(String source, RCML rcml)
        throws Exception {
        this.source = source;

        try {
            CheckOutEntry coe = rcml.getLatestCheckOutEntry();

            checkOutUsername = coe.getIdentity();
            checkOutDate = new Date(coe.getTime());
        } catch (Exception exception) {
            throw new Exception("Unable to create FileReservedCheckOutException object!");
        }
    }
    
    /**
     * Get the date of the checkout.
     * 
     * @return the date of the checkout
     */
    public Date getCheckOutDate() {
        return checkOutDate;
    }

    /**
     * Get the user name who did this checkout.
     * 
     * @return the user name of this checkout
     */
    public String getCheckOutUsername() {
        return checkOutUsername;
    }

}
