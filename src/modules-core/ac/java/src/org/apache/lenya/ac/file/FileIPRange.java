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

package org.apache.lenya.ac.file;

import java.io.File;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.ItemManager;
import org.apache.lenya.ac.Machine;
import org.apache.lenya.ac.impl.AbstractIPRange;
import org.apache.lenya.ac.impl.ItemConfiguration;
import org.apache.lenya.net.InetAddressUtil;

/**
 * IP range that is stored in a file.
 */
public class FileIPRange extends AbstractIPRange implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Main method.
     * @param args The command-line arguments.
     * @deprecated This should be moved to a JUnit test.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out
                    .println("Usage: network, netmask, ip (e.g. 192.168.0.64 255.255.255.240 192.168.0.70)");
            return;
        }
        try {
            InetAddress networkAddress = InetAddress.getByName(args[0]);
            InetAddress subnetMask = InetAddress.getByName(args[1]);
            Machine machine = new Machine(args[2]);

            InetAddressUtil util = new InetAddressUtil(new ConsoleLogger());
            if (util.contains(networkAddress, subnetMask, machine.getAddress())) {
                System.out.println("true");
            } else {
                System.out.println("false");
            }
        } catch (final UnknownHostException e) {
            System.err.println(e);
        } catch (final AccessControlException e) {
            System.err.println(e);
        }
    }

    /**
     * Ctor.
     * @param itemManager The item manager.
     * @param logger The logger.
     */
    public FileIPRange(ItemManager itemManager, Logger logger) {
        super(itemManager, logger);
    }

    /**
     * Ctor.
     * @param itemManager The item manager.
     * @param logger The logger.
     * @param id The IP range ID.
     */
    public FileIPRange(ItemManager itemManager, Logger logger, String id) {
        super(itemManager, logger, id);
        FileItemManager fileItemManager = (FileItemManager) itemManager;
        setConfigurationDirectory(fileItemManager.getConfigurationDirectory());
    }

    /**
     * @see org.apache.lenya.ac.impl.AbstractIPRange#save()
     */
    public void save() throws AccessControlException {
        DefaultConfigurationSerializer serializer = new DefaultConfigurationSerializer();
        Configuration config = createConfiguration();

        try {
            serializer.serializeToFile(getFile(), config);
        } catch (Exception e) {
            throw new AccessControlException(e);
        }
    }

    /**
     * Returns the configuration file.
     * @return A file object.
     */
    protected File getFile() {
        File xmlPath = getConfigurationDirectory();
        File xmlFile = new File(xmlPath, getId() + FileIPRangeManager.SUFFIX);
        return xmlFile;
    }

    /**
     * @see org.apache.lenya.ac.Item#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration config) throws ConfigurationException {
        new ItemConfiguration().configure(this, config);

        String networkAddress = config.getChild(ELEMENT_NETWORK_ADDRESS).getValue();
        String subnetMask = config.getChild(ELEMENT_SUBNET_MASK).getValue();

        try {
            setNetworkAddress(networkAddress);
            setSubnetMask(subnetMask);
        } catch (AccessControlException e) {
            throw new ConfigurationException("Configuring IP range [" + getId() + "] failed: ", e);
        }

    }

    protected static final String IP_RANGE = "ip-range";
    protected static final String ELEMENT_NETWORK_ADDRESS = "network-address";
    protected static final String ELEMENT_SUBNET_MASK = "subnet-mask";

    /**
     * Create a configuration from the current user details. Can be used for saving.
     * @return a <code>Configuration</code>
     */
    protected Configuration createConfiguration() {
        DefaultConfiguration config = new DefaultConfiguration(IP_RANGE);
        new ItemConfiguration().save(this, config);

        DefaultConfiguration networkAddressConfig = new DefaultConfiguration(
                ELEMENT_NETWORK_ADDRESS);
        networkAddressConfig.setValue(getNetworkAddress().getHostAddress());
        config.addChild(networkAddressConfig);

        DefaultConfiguration subnetMaskConfig = new DefaultConfiguration(ELEMENT_SUBNET_MASK);
        subnetMaskConfig.setValue(getSubnetMask().getHostAddress());
        config.addChild(subnetMaskConfig);

        return config;
    }
}