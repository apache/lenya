/*
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
package org.apache.lenya.net;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Category;

/**
 * A utility class for InetAddress. Also see http://jodies.de/ipcalc
 *
 * @author Michael Wechner
 * @version $Id: InetAddressUtil.java,v 1.8 2003/11/13 16:11:58 andreas Exp $
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
    		
//			if (log.isDebugEnabled()) {
				log.error("---------------------------------------");
				log.error("Checking part           [" + part + "]");
				log.error("    Network:            [" + network.getHostAddress() + "]");
				log.error("    Netmask:            [" + netmask.getHostAddress() + "]");
				log.error("    Address:            [" + ip.getHostAddress() + "]");
				log.error("    Network class part: [" + networkPart + "]");
				log.error("    Netmask class part: [" + netmaskPart + "]");
				log.error("    Address class part: [" + ipPart + "]");
				log.error("    First host address: [" + firstHostAddress + "]");
				log.error("    Last host address:  [" + lastHostAddress + "]");
				log.error("    Contained:          [" + contained + "]");
//			}
    	
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

        for(int i = 0; i < 4; i++) {
            log.error(".checkNetmask(): Check part: " + numbers[i]);
            if (0 <= numbers[i].intValue() && numbers[i].intValue() <= 255) {
                if (numbers[i].intValue() != 255) {
                    for(int k = i + 1; k < 4; k++) {
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
