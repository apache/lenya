/*
$Id: Machine.java,v 1.6 2003/08/12 15:14:39 andreas Exp $
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

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.lenya.cms.ac2.Accreditable;
import org.apache.lenya.cms.ac2.Identifiable;

/**
 * @author andreas
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Machine implements Identifiable {

    /**
     * Creates a new machine object.
     * @param ip The IP address.
     * @throws AccessControlException when something went wrong.
     */
    public Machine(String ip) throws AccessControlException {
        setAddress(getAddress(ip));
    }

    private InetAddress address;

    /**
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object otherObject) {
        boolean equals = false;

        if (otherObject instanceof Machine) {
            Machine otherMachine = (Machine) otherObject;
            equals = getAddress().equals(otherMachine.getAddress());
        }

        return equals;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return getAddress().hashCode();
    }

    /**
     * @see org.apache.lenya.cms.ac2.Accreditable#getAccreditables()
     */
    public Accreditable[] getAccreditables() {
        Accreditable[] ranges = getIPRanges();
        Accreditable[] accreditables = new Accreditable[ranges.length + 1];
        accreditables[0] = this;
        for (int i = 0; i < ranges.length; i++) {
            accreditables[i+1] = ranges[i];
        }
        return accreditables;
    }

    /**
     * Returns the IP address.
     * @return The IP address.
     */
    public String getIp() {
        return toString();
    }

    /**
     * Converts a string to an IP addres.
     * @param string The IP address, represented by a string.
     * @return An InetAddress object.
     * @throws AccessControlException when something went wrong.
     */
    protected static InetAddress getAddress(String string)
        throws AccessControlException {
        String[] strings = string.split("\\.");

        InetAddress address;
        try {
            byte[] numbers = new byte[strings.length];
            for (int i = 0; i < strings.length; i++) {
                int number = Integer.parseInt(strings[i]);
                if (number > 127) {
                    number = number - 256;
                }
                numbers[i] = (byte) number;
            }

            address = InetAddress.getByAddress(numbers);
        } catch (Exception e) {
            throw new AccessControlException(
                "Failed to convert address [" + string + "]: ",
                e);
        }
        return address;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getAddress().toString();
    }

    /**
     * Returns the IP address.
     * @return An IP address.
     */
    public InetAddress getAddress() {
        return address;
    }

    /**
     * Sets the IP address.
     * @param address An IP address.
     */
    public void setAddress(InetAddress address) {
        this.address = address;
    }

    private List ipRanges = new ArrayList();
    
    /**
     * Adds an IP range to this machine.
     * @param range An IP range this machine belongs to.
     */
    public void addIPRange(IPRange range) {
        assert range != null;
        assert !ipRanges.contains(range);
        ipRanges.add(range);
    }
    
    /**
     * Returns the IP ranges this machine belongs to.
     * @return An array of IP ranges.
     */
    public IPRange[] getIPRanges() {
        return (IPRange[]) ipRanges.toArray(new IPRange[ipRanges.size()]);
    }
    
}
