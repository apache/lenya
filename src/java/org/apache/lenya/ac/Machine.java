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

/* $Id: Machine.java,v 1.2 2004/03/03 12:56:31 gregor Exp $  */

package org.apache.lenya.ac;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

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
        return getAddress().getHostAddress();
    }

    /**
     * Converts a string to an IP addres.
     * @param string The IP address, represented by a string.
     * @return An InetAddress object.
     * @throws AccessControlException when something went wrong.
     */
    public static InetAddress getAddress(String string)
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
        return getIp();
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
