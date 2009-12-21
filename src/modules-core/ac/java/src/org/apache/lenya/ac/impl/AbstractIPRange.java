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

/* $Id$  */

package org.apache.lenya.ac.impl;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.apache.avalon.framework.logger.Logger;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.IPRange;
import org.apache.lenya.ac.ItemManager;
import org.apache.lenya.ac.Machine;
import org.apache.lenya.net.InetAddressUtil;

/**
 * <p>
 * A range of IP addresses, expressed by a network address and a subnet mask.
 * </p>
 * <p>
 * Note: this class does not enforce that the network address and the subnet mask have the same size
 * (i.e. either both IPv4 or both IPv6 addresses). If the the network address and subnet mask have
 * different sizes, the range does not contain any hosts, that is {@link #contains(Machine)} will
 * always return <code>false</code>.
 * </p>
 */
public abstract class AbstractIPRange extends AbstractGroupable implements IPRange {
    /*
     * FIXME by zisch@dals.ch: Fixed this class for IPv6. However there are still some general
     * flaws, partly coming from the IPRange interface. A redesign of (Abstract/File)IPRange and
     * it's helper class org.apache.lenya.net.InetAddressUtil would be a good idea. Some problems of
     * this implementation are:
     *  - The whole initialization seems flawed. Objects can be in an unitialized state and the
     * class seems not to be aware of this.
     *  - Network-address and -mask can be set independently. Therefore it cannot be enforced that
     * these have the same size (i.e. that both are IPv4 or both are IPv6). This shows up in
     * InetAddressUtil.contains(...), where in a case of mismatch there is no good way to inform the
     * user about the problem. This should be done once when the AbstractIPRange object is
     * initialized.
     *  - Unless this functionality would be needed by other parts of Lenya or external software
     * (which seems not to be the case ;-), InetAddressUtil should be removed (resp. deprecated)
     * altogether, because it's mostly an internal implementation detail of AbstractIPRange.
     * AbstractIPRange should implement the contains(...)-method internally to make use of the fact
     * that the network- addresses and -masks validity and compatibility has already been checked
     * when setting these. (Once the above problems have been fixed. ;-)
     *  - Especially for IPv6 it would be nice to have the possibility to specify the netmask as the
     * number of bits (as in "::1/128" or "127.0.0.1/24").
     * 
     * FIXME II (from the previous version): why are we in the business of implementing IP ranges??
     */

    /**
     * Ctor.
     * Initializes the the IP range with the local host (127.0.0.1/24 for IPv4, ::1/128 for IPv6).
     * @param itemManager The item manager.
     * @param logger The logger.
     */
    public AbstractIPRange(ItemManager itemManager, Logger logger) {
        super(itemManager, logger);
        try {
            this.networkAddress = InetAddress.getLocalHost();
            byte[] mask = null;
            int masklen = this.networkAddress.getAddress().length;
            if (masklen == 4) {
                /* IPv4: */
                /*
                 * FIXME? by zisch@dals.ch: Should this be { -1, 0, 0, 0 }??
                 */
                mask = new byte[] { -1, -1, -1, 0 };
            } else {
                /* IPv6 (and others ;-): */
                mask = new byte[masklen];
                Arrays.fill(mask, (byte) -1);
            }
            this.subnetMask = InetAddress.getByAddress(mask);
        } catch (UnknownHostException ignore) {
            /*
             * FIXME? by zisch@dals.ch: Is it safe to ignore the exception and just leave the
             * IPRange uninitialized!?
             */
        }
    }

    /**
     * Ctor.
     * @param itemManager The item manager.
     * @param logger The logger.
     * @param id The IP range ID.
     */
    public AbstractIPRange(ItemManager itemManager, Logger logger, String id) {
        super(itemManager, logger);
        setId(id);
    }

    private File configurationDirectory;

    /**
     * Returns the configuration directory.
     * @return A file object.
     */
    public File getConfigurationDirectory() {
        return this.configurationDirectory;
    }

    protected void setConfigurationDirectory(File _configurationDirectory) {
        this.configurationDirectory = _configurationDirectory;
    }

    /**
     * Save the IP range
     * @throws AccessControlException if the save failed
     */
    public abstract void save() throws AccessControlException;

    /**
     * Delete an IP range
     * @throws AccessControlException if the delete failed
     */
    public void delete() throws AccessControlException {
        removeFromAllGroups();
    }

    private InetAddress networkAddress;

    /**
     * Sets the network address. This method accepts numeric IPv4 addresses like
     * <code>"129.168.0.32"</code>, numeric IPv6 addresses like
     * <code>"1080::8:800:200C:417A"</code> as well as hostnames (if DNS resolution is available)
     * like <code>"localhost"</code> or <code>"www.apache.com"</code>.
     * @param address a <code>String</code> like <code>"192.168.0.32"</code>,
     *            <code>"::1"</code>, ...
     * @throws AccessControlException when the conversion of the <code>String</code> to an
     *             <code>InetAddress</code> failed
     * @see #setNetworkAddress(byte[])
     */
    public void setNetworkAddress(String address) throws AccessControlException {
        try {
            this.networkAddress = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            throw new AccessControlException("Failed to convert address [" + address + "]: ", e);
        }
    }

    /**
     * Sets the network address. The method accepts numeric IPv4 addresses (specified by byte arrays
     * of length 4) or IPv6 addresses (specified by byte arrays of length 16).
     * @param address a byte array of the length 4 or 16
     * @throws AccessControlException when the conversion of the byte array to an InetAddress
     *             failed.
     * @see #setNetworkAddress(String)
     */
    public void setNetworkAddress(byte[] address) throws AccessControlException {
        try {
            this.networkAddress = InetAddress.getByAddress(address);
        } catch (UnknownHostException e) {
            throw new AccessControlException("Failed to convert address [" + addr2string(address)
                    + "]: ", e);
        }
    }

    /**
     * Returns the network address.
     * @return an <code>InetAddress</code> representing the network address
     */
    public InetAddress getNetworkAddress() {
        return this.networkAddress;
    }

    private InetAddress subnetMask;

    /**
     * Sets the subnet mask. See {@link #setNetworkAddress(String)} for the allowed formats of the
     * <code>mask</code> string. (However, the hostname format will usually not be of much use for
     * setting the mask.)
     * <p>
     * Only valid subnet masks are accepted, for which the binary representation is a sequence of
     * 1-bits followed by a sequence of 0-bits. For example <code>"255.128.0.0"</code> is valid
     * while <code>"255.128.0.1"</code> is not.
     * @param mask a <code>String</code> like <code>"255.255.255.0"</code>
     * @throws AccessControlException when the conversion of the String to an
     *             <code>InetAddress</code> failed.
     * @see #setSubnetMask(byte[])
     */
    public void setSubnetMask(String mask) throws AccessControlException {
        try {
            /* use setSubnetMask(...) to check the mask-format: */
            setSubnetMask(InetAddress.getByName(mask).getAddress());
        } catch (final UnknownHostException e) {
            throw new AccessControlException("Failed to convert mask [" + mask + "]: ", e);
        }

    }

    /**
     * Sets the subnet mask.
     * <p>
     * Only valid subnet masks are accepted, for which the binary representation is a sequence of
     * 1-bits followed by a sequence of 0-bits. For example <code>{ 255, 128, 0, 0 }</code> is
     * valid while <code>{ 255, 128, 0, 1 }</code> is not.
     * @param mask A byte array of the length 4.
     * @throws AccessControlException when the conversion of the byte array to an InetAddress
     *             failed.
     * @see #setSubnetMask(String)
     */
    public void setSubnetMask(byte[] mask) throws AccessControlException {
        /*
         * check for correct netmask (i.e. any number of 1-bits followed by 0-bits filling the right
         * part of the mask) ...
         * 
         * FIXME: This "algorithm" is rather unelegant. There should be a better way to do it! ;-)
         */
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("CHECK_NETMASK: check " + addr2string(mask));
        }
        int i = 0;
        CHECK_NETMASK: while (i < mask.length) {
            int b = mask[i++] & 0xff;
            /* the initial byte(s) must be 255: */
            if (b != 0xff) {
                /* first byte != 255, test all possibilities: */
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("CHECK_NETMASK: first byte != 255: idx: " + (i - 1)
                            + ", mask[idx]: 0x" + b);
                }
                /* check if 0: */
                if (b == 0) {
                    break CHECK_NETMASK;
                }
                for (int tst = 0xfe; tst != 0; tst = (tst << 1) & 0xff) {
                    getLogger().debug("CHECK_NETMASK: tst == 0x" + Integer.toHexString(tst));
                    if (b == tst) {
                        break CHECK_NETMASK;
                    }
                }
                /*
                 * Invalid byte found, i.e. one which is not element of { 11111111, 11111110,
                 * 11111100, 11111000, ..., 00000000 }
                 */
                throw new AccessControlException("Invalid byte in mask [" + addr2string(mask) + "]");
            }
        }
        /* the remaining byte(s) (if any) must be 0: */
        while (++i < mask.length) {
            if (mask[i] != 0) {
                /*
                 * Invalid byte found, i.e. some non-zero byte right of the first non-zero byte.
                 */
                throw new AccessControlException("Invalid non-zero byte in mask ["
                        + addr2string(mask) + "]");
            }
        }

        /* convert the checked mask to InetAddress: */
        try {
            this.subnetMask = InetAddress.getByAddress(mask);
        } catch (final UnknownHostException e) {
            throw new AccessControlException(
                    "Failed to convert mask [" + addr2string(mask) + "]: ", e);
        }
    }

    /**
     * Returns the subnet mask.
     * @return An InetAddress value.
     */
    public InetAddress getSubnetMask() {
        return this.subnetMask;
    }

    /**
     * Checks if a network address / subnet mask combination describes a valid subnet.
     * @param networkAddress The network address.
     * @param subnetMask The subnet mask.
     * @return A boolean value.
     * @deprecated This method is currently not implemented, probably not necessary.and could be
     *             removed in the future. Therefore it should not be used.
     */
    public static boolean isValidSubnet(InetAddress networkAddress, InetAddress subnetMask) {
        /*
         * FIXME? by zisch@dals.ch: Is this method really necessary (what for?) and (if so)
         * shouldn't it be an internal (private) utility-method??
         */
        // TODO implement class
        return false;
    }

    /**
     * Checks if this IP range contains a certain machine.
     * <p>
     * Note: if the network address and the subnet mask of this IP range have different sizes (i.e.
     * one is IPv4 and one is IPv6), this method will always return <code>false</code>, no matter
     * what machine has been specified!
     * <p>
     * Further, if the machine address and the IP range (i.e. network address and subnet mask) have
     * different sizes, the method will return <code>false</code>. (In other words: an IPv4 range
     * never contains an IPv6 address and the other way round.)
     * <p>
     * Note that the above can lead to confusion. For example the local subnet in IPv4 (
     * <code>127.0.0.0/8</code>) will <b>not </b> contain the localhost in IPv6 (
     * <code>::1</code>), and the localhost in IPv4 (<code>127.0.0.1</code>) will <b>not </b>
     * be contained in the local subnet in IPv6 (<code>::1/128</code>).
     * @param machine the machine to check for
     * @return a boolean value
     * @see InetAddressUtil#contains
     */
    public boolean contains(Machine machine) {
        /*
         * FIXME? by zisch@dals.ch: Maybe some mapping between IPv4/v6 should be done here, p.e. for
         * the localhost (see the javdoc comment above)? (I'm not a TCP/IP-guru, so I'm not sure
         * about this. ;-)
         */
        getLogger().debug("Checking IP range: [" + getId() + "]");
        InetAddressUtil util = new InetAddressUtil(getLogger());
        return util.contains(this.networkAddress, this.subnetMask, machine.getAddress());
    }

    /**
     * Format the specified numeric IP address.
     * @param addr the raw numeric IP address
     * @return the formatted address
     */
    private static String addr2string(byte[] addr) {
        StringBuffer buf = new StringBuffer();
        if (addr.length > 4) {
            /* IPv6-format if more than 4 bytes: */
            for (int i = 0; i < addr.length; i++) {
                if (i > 0 && (i & 1) == 0) {
                    buf.append(':');
                }
                String hex = Integer.toHexString(addr[i] & 0xff);
                if (hex.length() == 1) {
                    buf.append('0');
                }
                buf.append(hex);
            }
        } else {
            /* IPv4-format: */
            for (int i = 0; i < addr.length; i++) {
                if (i > 0) {
                    buf.append('.');
                }
                buf.append(addr[i] & 0xff);
            }
        }
        return buf.toString();
    }
}