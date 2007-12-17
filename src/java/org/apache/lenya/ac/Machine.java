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

package org.apache.lenya.ac;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

/**
 * A machine (representing an IP address).
 * @version $Id: Machine.java 473841 2006-11-12 00:46:38Z gregor $
 */
public class Machine implements Identifiable {

    /**
     * Creates a new machine object. This method accepts
     * numeric IPv4 addresses like <code>"129.168.0.32"</code>,
     * numeric IPv6 addresses like <code>"1080::8:800:200C:417A"</code>
     * as well as hostnames (if DNS resolution is available) like
     * <code>"localhost"</code> or <code>"www.apache.com"</code>.
     * 
     * @param ip a <code>String</code> like <code>"192.168.0.32"</code>,
     *      <code>"::1"</code>, ...
     * .
     * @throws AccessControlException when the conversion of the
     *      <code>String</code> to an <code>InetAddress</code> failed
     */
    public Machine(String ip) throws AccessControlException {
        try {
            setAddress(InetAddress.getByName(ip));
        } catch(UnknownHostException uhe) {
            throw new AccessControlException
                ("Failed to convert address [" + ip + "]: ", uhe);
        }
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

    public Accreditable[] getAccreditables(AccreditableManager manager) {
        IPRange[] ranges = getIpRanges(manager);
        Set accrs = new HashSet();
        for (int i = 0; i < ranges.length; i++) {
            accrs.add(ranges[i]);
        }
        return (Accreditable[]) accrs.toArray(new Accreditable[accrs.size()]);
    }

    /**
     * Returns the IP address.
     * @return The IP address.
     */
    public String getIp() {
        return getAddress().getHostAddress();
    }

    /**
     * Converts a string to an IP address.
     * @param string The IP address, represented by a string.
     * @return An InetAddress object.
     * @throws AccessControlException when something went wrong.
     * 
     * @deprecated This method is unnecessary and does not work for IPv6.
     *      Use <code>InetAddress.getByName(string)</code> instead!
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
    
    /**
     * Returns the IP ranges this machine belongs to.
     * @param manager The accreditable manager to obtain the IP ranges from.
     * @return An array of IP ranges.
     */
    public IPRange[] getIpRanges(AccreditableManager manager) {
        Set ranges = new HashSet();
        try {
            IPRange[] allRanges = manager.getIPRangeManager().getIPRanges();
            for (int i = 0; i < allRanges.length; i++) {
                if (allRanges[i].contains(this)) {
                    ranges.add(allRanges[i]);
                }
            }
        } catch (AccessControlException e) {
            throw new RuntimeException(e);
        }
        
        return (IPRange[]) ranges.toArray(new IPRange[ranges.size()]);
    }

}
