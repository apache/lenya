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

package org.apache.lenya.ac;

import java.net.InetAddress;


/**
 * An IP range.
 */
public interface IPRange extends Accreditable, Item, Groupable {
    
    /**
     * Sets the network address.
     * 
     * @param address A string, e.g. 192.168.0.32
     * 
     * @throws AccessControlException when the conversion of the String to an
     * InetAddress failed.
     */
    void setNetworkAddress(String address) throws AccessControlException;
    
    /**
     * Sets the network address.
     * 
     * @param address A byte array of the length 4.
     * 
     * @throws AccessControlException when the conversion of the byte array to an
     * InetAddress failed.
     */
    void setNetworkAddress(byte[] address) throws AccessControlException;
    
    /**
     * Returns the network address.
     * @return An InetAddress value.
     */
    InetAddress getNetworkAddress();
    
    /**
     * Sets the subnet mask.
     * 
     * @param mask A string, e.g. 192.168.0.32
     * 
     * @throws AccessControlException when the conversion of the String to an
     * InetAddress failed.
     */
    void setSubnetMask(String mask) throws AccessControlException;
    
    /**
     * Sets the subnet mask.
     * 
     * @param mask A byte array of the length 4.
     * 
     * @throws AccessControlException when the conversion of the byte array to an
     * InetAddress failed.
     */
    void setSubnetMask(byte[] mask) throws AccessControlException;
    
    /**
     * Returns the subnet mask.
     * @return An InetAddress value.
     */
    InetAddress getSubnetMask();
    
    /**
     * Checks if this IP range contains a certain machine.
     * @param machine The machine to check for.
     * @return A boolean value.
     */
    boolean contains(Machine machine);

    /**
     * Save the IP range.
     * @throws AccessControlException if the save failed
     */
    void save() throws AccessControlException;
    
    /**
     * Delete an IP range.
     * @throws AccessControlException if the delete failed
     */
    void delete() throws AccessControlException;
}