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

/* $Id: FileIPRange.java,v 1.2 2004/03/03 12:56:32 gregor Exp $  */

package org.apache.lenya.ac.file;

import java.io.File;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;
import org.apache.lenya.ac.AccessControlException;
import org.apache.lenya.ac.IPRange;
import org.apache.lenya.ac.Machine;
import org.apache.lenya.ac.impl.AbstractIPRange;
import org.apache.lenya.ac.impl.ItemConfiguration;

/**
 * IP range that is stored in a file.
 */
public class FileIPRange extends AbstractIPRange {

	/**
	 * Main method.
	 * 
	 * @param args
	 *            The command-line arguments.
	 * @deprecated This should bemoved to a JUnit test.
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println(
				"Usage: network, netmask, ip (e.g. 192.168.0.64 255.255.255.240 192.168.0.70)");
			return;
		}
		IPRange ipr = new FileIPRange();
		try {
			ipr.setNetworkAddress(args[0]);
			ipr.setSubnetMask(args[1]);
			if (ipr.contains(new Machine(args[2]))) {
				System.out.println("true");
			} else {
				System.out.println("false");
			}
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	/**
	 * Ctor.
	 */
	public FileIPRange() {
	}

	/**
	 * Ctor.
	 * 
	 * @param configurationDirectory
	 *            The configuration directory.
	 * @param id
	 *            The IP range ID.
	 */
	public FileIPRange(File configurationDirectory, String id) {
		super(id);
		setConfigurationDirectory(configurationDirectory);
	}

	/**
	 * @see org.apache.lenya.cms.ac.IPRange#save()
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
	 * 
	 * @return A file object.
	 */
	protected File getFile() {
		File xmlPath = getConfigurationDirectory();
		File xmlFile = new File(xmlPath, getId() + FileIPRangeManager.SUFFIX);
		return xmlFile;
	}

	/**
	 * @see org.apache.lenya.cms.ac.Item#configure(org.apache.avalon.framework.configuration.Configuration)
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

	public static final String IP_RANGE = "ip-range";
	public static final String ELEMENT_NETWORK_ADDRESS = "network-address";
	public static final String ELEMENT_SUBNET_MASK = "subnet-mask";

	/**
	 * Create a configuration from the current user details. Can be used for saving.
	 * 
	 * @return a <code>Configuration</code>
	 */
	protected Configuration createConfiguration() {
		DefaultConfiguration config = new DefaultConfiguration(IP_RANGE);
		new ItemConfiguration().save(this, config);

		DefaultConfiguration networkAddressConfig =
			new DefaultConfiguration(ELEMENT_NETWORK_ADDRESS);
		networkAddressConfig.setValue(getNetworkAddress().getHostAddress());
		config.addChild(networkAddressConfig);

		DefaultConfiguration subnetMaskConfig = new DefaultConfiguration(ELEMENT_SUBNET_MASK);
		subnetMaskConfig.setValue(getSubnetMask().getHostAddress());
		config.addChild(subnetMaskConfig);

		return config;
	}
}
