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
package org.apache.lenya.cms.ac;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.lenya.net.InetAddressUtil;
import org.apache.log4j.Category;

/**
 * A range of IP addresses, expressed by a network address and a
 * subnet mask.
 * 
 * @author Andreas Hartmann
 * @author Michael Wechner
 * @version $Id: IPRange.java,v 1.6 2003/10/20 17:03:20 andreas Exp $
 */
public abstract class IPRange extends AbstractGroupable {
	
	private static final Category log = Category.getInstance(IPRange.class);

    /**
     * Ctor.
     */    
    public IPRange() {
        try {
            byte[] address = { 127, 0, 0, 0 };
            networkAddress = InetAddress.getByAddress(address);
            byte[] mask = { -1, -1, -1, 0 };
            subnetMask = InetAddress.getByAddress(mask);
        }
        catch (UnknownHostException ignore) {
        }
    }

    /**
     * Ctor.
     * @param id The IP range ID.
     */    
    public IPRange(String id) {
        setId(id);
    }

    private File configurationDirectory;

    /**
     * Returns the configuration directory.
     * @return A file object.
     */
    public File getConfigurationDirectory() {
        return configurationDirectory;
    }

    /**
     * @see org.apache.lenya.cms.ac.Item#setConfigurationDirectory(java.io.File)
     */
    public void setConfigurationDirectory(File configurationDirectory) {
        this.configurationDirectory = configurationDirectory;
    }

    /**
     * Save the IP range
     *
     * @throws AccessControlException if the save failed
     */
    public abstract void save() throws AccessControlException;

    /**
     * Delete an IP range
     *
     * @throws AccessControlException if the delete failed
     */
    public void delete() throws AccessControlException {
        removeFromAllGroups();
    }
    
    private InetAddress networkAddress;
    
    /**
     * Sets the network address.
     * 
     * @param address A string, e.g. 192.168.0.32
     * 
     * @throws AccessControlException when the conversion of the String to an
     * InetAddress failed.
     */
    public void setNetworkAddress(String address) throws AccessControlException {
        networkAddress = Machine.getAddress(address);
    }
    
    /**
     * Sets the network address.
     * 
     * @param address A byte array of the length 4.
     * 
     * @throws AccessControlException when the conversion of the byte array to an
     * InetAddress failed.
     */
    public void setNetworkAddress(byte[] address) throws AccessControlException {
        try {
            networkAddress = InetAddress.getByAddress(address);
        } catch (UnknownHostException e) {
            throw new AccessControlException("Failed to convert address [" + address + "]: ", e);
        }
    }
    
    /**
     * Returns the network address.
     * @return An InetAddress value.
     */
    public InetAddress getNetworkAddress() {
        return networkAddress;
    }
    
    private InetAddress subnetMask;
    
    /**
     * Sets the subnet mask.
     * 
     * @param mask A string, e.g. 192.168.0.32
     * 
     * @throws AccessControlException when the conversion of the String to an
     * InetAddress failed.
     */
    public void setSubnetMask(String mask) throws AccessControlException {
        subnetMask = Machine.getAddress(mask);
    }
    
    /**
     * Sets the subnet mask.
     * 
     * @param mask A byte array of the length 4.
     * 
     * @throws AccessControlException when the conversion of the byte array to an
     * InetAddress failed.
     */
    public void setSubnetMask(byte[] mask) throws AccessControlException {
        try {
            subnetMask = InetAddress.getByAddress(mask);
        } catch (UnknownHostException e) {
            throw new AccessControlException("Failed to convert mask [" + mask + "]: ", e);
        }
    }
    
    /**
     * Returns the subnet mask.
     * @return An InetAddress value.
     */
    public InetAddress getSubnetMask() {
        return subnetMask;
    }
    
    /**
     * Checks if a network address / subnet mask combination describes a
     * valid subnet.
     * @param networkAddress The network address.
     * @param subnetMask The subnet mask.
     * @return A boolean value.
     */
    public static boolean isValidSubnet(InetAddress networkAddress, InetAddress subnetMask) {
        // TODO
        return false;
    }

    /**
     * Checks if this IP range contains a certain machine.
     * @param machine The machine to check for.
     * @return A boolean value.
     */
    public boolean contains(Machine machine) {
    	log.debug("Checking IP range: [" + getId() + "]");
        return InetAddressUtil.contains(networkAddress, subnetMask, machine.getAddress());
    }
}
