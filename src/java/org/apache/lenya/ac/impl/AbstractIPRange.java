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

/* $Id: AbstractIPRange.java,v 1.4 2004/03/03 12:56:33 gregor Exp $  */

package org.apache.lenya.ac.impl;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.IPRange;
import org.apache.lenya.ac.Machine;
import org.apache.lenya.net.InetAddressUtil;
import org.apache.log4j.Category;

/**
 * A range of IP addresses, expressed by a network address and a
 * subnet mask.
 */
public abstract class AbstractIPRange extends AbstractGroupable implements IPRange {

    private static final Category log = Category.getInstance(AbstractIPRange.class);

    /**
     * Ctor.
     */
    public AbstractIPRange() {
        try {
            byte[] address = { 127, 0, 0, 0 };
            networkAddress = InetAddress.getByAddress(address);
            byte[] mask = { -1, -1, -1, 0 };
            subnetMask = InetAddress.getByAddress(mask);
        } catch (UnknownHostException ignore) {
        }
    }

    /**
     * Ctor.
     * @param id The IP range ID.
     */
    public AbstractIPRange(String id) {
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
        try {
            networkAddress = Machine.getAddress(address);
        } catch (Exception e) {
            throw new AccessControlException("Failed to convert address [" + address + "]: ", e);
        }
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
        // TODO implement class
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
