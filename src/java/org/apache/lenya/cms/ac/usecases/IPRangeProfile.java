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

/* $Id$  */

package org.apache.lenya.cms.ac.usecases;

import java.net.InetAddress;

import org.apache.lenya.ac.IPRange;

/**
 * Usecase to change the profile of an IP range.
 */
public class IPRangeProfile extends AccessControlUsecase {

    protected static final String ID = "ipRangeId";
    protected static final String NAME = "name";
    protected static final String DESCRIPTION = "description";
    protected static final String NETWORK_ADDRESS = "networkAddress";
    protected static final String SUBNET_MASK = "subnetMask";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();
        
        String name = getParameterAsString(NAME);
        String description = getParameterAsString(DESCRIPTION);
        
        getIPRange().setName(name);
        getIPRange().setDescription(description);
        getIPRange().save();
        
    }
    
    private IPRange ipRange;
    
    /**
     * @return The IP range.
     */
    protected IPRange getIPRange() {
        return this.ipRange;
    }
    
    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setParameter(java.lang.String, java.lang.Object)
     */
    public void setParameter(String name, Object value) {
        super.setParameter(name, value);
        
        if (name.equals(ID)) {
            String id = (String) value;
            this.ipRange = getIpRangeManager().getIPRange(id);
            if (this.ipRange == null) {
                throw new RuntimeException("IP range [" + id + "] not found.");
            }
            
            setParameter(NAME, this.ipRange.getName());
            setParameter(DESCRIPTION, this.ipRange.getDescription());
            
            InetAddress networkAddress = this.ipRange.getNetworkAddress();
            InetAddress subnetMask = this.ipRange.getSubnetMask();
            
            for (byte i = 0; i < 4; i++) {
                setParameter(NETWORK_ADDRESS + "-" + i, new Part(i, networkAddress.getAddress()[i]));
                setParameter(SUBNET_MASK + "-" + i, new Part(i, subnetMask.getAddress()[i]));
            }
            
        }
    }
    
    /**
     * IP address holder.
     */
    public static class Address {
        private Part[] parts = new Part[4];
        
        /**
         * Ctor.
         */
        public Address() {
            for (byte i = 0; i < this.parts.length; i++) {
                this.parts[i] = new Part(i);
            }
        }
        
        /**
         * Ctor.
         * @param address The address.
         */
        public Address(InetAddress address) {
            for (byte i = 0; i < this.parts.length; i++) {
                this.parts[i] = new Part(i, address.getAddress()[i]);
            }
        }
        
        /**
         * @return The parts of this address.
         */
        public Part[] getParts() {
            return this.parts;
        }
    }
    
    /**
     * IP address part holder.
     */
    public static class Part {
        private String value;
        private byte position;
        
        /**
         * Ctor.
         * @param position The position.
         * @param value The value.
         */
        public Part(byte position, byte value) {
            this.value = Byte.toString(value);
            this.position = position;
        }
        
        /**
         * Ctor.
         * @param position The position.
         */
        public Part(byte position) {
            this(position, (byte) 0);
        }
        
        public byte getPosition() {
            return this.position;
        }
        
        public String getValue() {
            return this.value;
        }
        
        /**
         * @param value The value.
         */
        public void setValue(String value) {
            this.value = value;
        }
        
        /**
         * Checks if the part is valid.
         * @return A boolean value.
         */
        public boolean isValid() {
            
            boolean valid = true;
            try {
                Byte.parseByte(this.value);
            }
            catch (NumberFormatException e) {
                valid = false;
            }
            
            return valid; 
        }
        
    }
    
}
