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

import org.apache.log4j.Category;

/**
 * A utility class for InetAddress
 *
 * @author Michael Wechner
 * @version $Id: InetAddressUtil.java,v 1.4 2003/10/27 17:24:39 andreas Exp $
 */
public class InetAddressUtil {
	
	private static final Category log = Category.getInstance(InetAddressUtil.class);
	
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
    	
    	short part = 0;
    	while (contained && part < 3) {
    		
			int networkC = getClassPart(network, part);
			int netmaskC = getClassPart(netmask, part);
			int ipC = getClassPart(ip, part);
			
			int firstHostAddress = networkC + 1;
			int broadcastAddress = networkC + (256 - netmaskC - 1);
			int lastHostAddress = broadcastAddress - 1;
			
    		contained = contained && firstHostAddress <= ipC && ipC <= lastHostAddress;
    		
//			if (log.isDebugEnabled()) {
				log.debug("---------------------------------------");
				log.debug("Checking part           [" + part + "]");
				log.debug("    Network:            [" + network.getHostAddress() + "]");
				log.debug("    Netmask:            [" + netmask.getHostAddress() + "]");
				log.debug("    Address:            [" + ip.getHostAddress() + "]");
				log.debug("    Network class part: [" + networkC + "]");
				log.debug("    Netmask class part: [" + netmaskC + "]");
				log.debug("    Address class part: [" + ipC + "]");
				log.debug("    First host address: [" + firstHostAddress + "]");
				log.debug("    Last host address:  [" + lastHostAddress + "]");
				log.debug("    Contained:          [" + contained + "]");
//			}

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
    public static int getClassPart(InetAddress ip, short partNumber) {
        String[] parts = ip.getHostAddress().split("\\.");
        String part = parts[partNumber];
        return new Integer(part).intValue();
    }
}
