/*
$Id: FileReservedCheckOutException.java,v 1.8 2003/07/14 14:24:20 egli Exp $
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
package org.apache.lenya.cms.rc;

import java.util.Date;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner (http://www.lenya.com)
 * @version 0.7.5
 */
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
