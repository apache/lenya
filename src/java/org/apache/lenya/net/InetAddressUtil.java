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

/* $Id: InetAddressUtil.java,v 1.11 2004/03/01 16:18:25 gregor Exp $  */

package org.apache.lenya.net;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Category;

/**
 * A utility class for InetAddress. Also see http://jodies.de/ipcalc
 */
public class InetAddressUtil {

    private static final Category log = Category.getInstance(InetAddressUtil.class);

    /**
     * Ctor.
     */
    private InetAddressUtil() {
    }

    /**
     * Checks if a subnet contains a specific IP address.
     * @param network The network address.
     * @param netmask The subnet mask.
     * @param ip The IP address to check.
     * @return A boolean value.
     */
    public static boolean contains(InetAddress network, InetAddress netmask, InetAddress ip) {

        log.debug("=======================================");
        log.debug("Checking IP address");

        boolean contained = true;

        int part = checkNetmask(netmask);
        if (0 <= part && part <= 3) {
        } else {
            return false; // illegal netmask
        }

        int networkPart = getClassPart(network, part);
        int netmaskPart = getClassPart(netmask, part);
        int ipPart = getClassPart(ip, part);

        int firstHostAddress = networkPart + 1;
        int broadcastAddress = networkPart + (256 - netmaskPart - 1);
        int lastHostAddress = broadcastAddress - 1;

        contained = contained && firstHostAddress <= ipPart && ipPart <= lastHostAddress;
        for (int i = 0; i < part; i++) {
            contained = contained && getClassPart(network, i) == getClassPart(ip, i);
        }

        if (log.isDebugEnabled()) {
            log.debug("---------------------------------------");
            log.debug("Checking part           [" + part + "]");
            log.debug("    Network:            [" + network.getHostAddress() + "]");
            log.debug("    Netmask:            [" + netmask.getHostAddress() + "]");
            log.debug("    Address:            [" + ip.getHostAddress() + "]");
            log.debug("    Network class part: [" + networkPart + "]");
            log.debug("    Netmask class part: [" + netmaskPart + "]");
            log.debug("    Address class part: [" + ipPart + "]");
            log.debug("    First host address: [" + firstHostAddress + "]");
            log.debug("    Last host address:  [" + lastHostAddress + "]");
            log.debug("    Contained:          [" + contained + "]");
        }

        log.debug("---------------------------------------");
        log.debug("Contained:              [" + contained + "]");
        log.debug("=======================================");

        return contained;
    }

    /**
     * Returns the n-th part of an InetAddress.
     * @param ip The address.
     * @param partNumber The number of the part.
     * @return An integer value.
     */
    public static int getClassPart(InetAddress ip, int partNumber) {
        String[] parts = ip.getHostAddress().split("\\.");
        String part = parts[partNumber];
        return new Integer(part).intValue();
    }

    /**
     * Check netmask, e.g. 255.255.255.240 is fine, 255.255.240.16 is illegal (needs to be 255.255.240.0)
     * @param netmask The netmask address.
     * @return An integer value. -1 if illegal netmask, otherwise 0, 1, 2, 3
     */
    public static int checkNetmask(InetAddress netmask) {
        String[] parts = netmask.getHostAddress().split("\\.");
        Integer[] numbers = new Integer[4];
        for (int i = 0; i < 4; i++) {
            numbers[i] = new Integer(parts[i]);
        }

        for (int i = 0; i < 4; i++) {
            log.debug(".checkNetmask(): Check part: " + numbers[i]);
            if (0 <= numbers[i].intValue() && numbers[i].intValue() <= 255) {
                if (numbers[i].intValue() != 255) {
                    for (int k = i + 1; k < 4; k++) {
                        if (numbers[k].intValue() != 0) {
                            log.error(".checkNetmask(): Illegal Netmask: " + netmask);
                            return -1;
                        }
                    }
                    return i;
                } else {
                    continue;
                }
            } else {
                // FIXME: This check not really be necessary because java.net.UnknownHostException should be thrown long time before
                log.error(".checkNetmask(): Illegal Netmask: " + netmask);
                return -1;
            }
        }
        log.error(".checkNetmask(): Illegal Netmask: " + netmask);
        return -1;
    }

    /**
     * Converts a string to an IP addres.
     * @param string The IP address, represented by a string.
     * @return An InetAddress object.
     * @throws AccessControlException when something went wrong.
     */
    public static InetAddress getAddress(String string) throws UnknownHostException {
        String[] strings = string.split("\\.");

        InetAddress address;
        byte[] numbers = new byte[strings.length];
        for (int i = 0; i < strings.length; i++) {
            int number = Integer.parseInt(strings[i]);
            if (number > 127) {
                number = number - 256;
            }
            numbers[i] = (byte) number;
        }

        address = InetAddress.getByAddress(numbers);
        return address;
    }

}
