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
package org.apache.lenya.cms.ac.usecases;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.lenya.ac.IPRange;
import org.apache.lenya.cms.usecase.AbstractUsecase;

/**
 * Usecase to change the profile of an IP range.
 */
public class IPRangeProfile extends AccessControlUsecase {

    protected static final String ID = "ipRangeId";
    protected static final String NAME = "name";
    protected static final String DESCRIPTION = "description";
    protected static final String NETWORK_ADDRESS = "networkAddress";
    protected static final String SUBNET_MASK = "subnetMask";
    protected static final String PART_NUMBERS = "partNumbers";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {
        IPRangeProfile.validateAddresses(this);
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();

        String name = getParameterAsString(NAME);
        String description = getParameterAsString(DESCRIPTION);

        IPRange ipRange = getIPRange();

        ipRange.setName(name);
        ipRange.setDescription(description);

        StringBuffer networkBuffer = new StringBuffer();
        StringBuffer subnetBuffer = new StringBuffer();

        for (int i = 0; i < 4; i++) {
            if (i > 0) {
                networkBuffer.append(".");
                subnetBuffer.append(".");
            }
            Part netPart = (Part) getParameter(IPRangeProfile.NETWORK_ADDRESS + "-" + i);
            networkBuffer.append(netPart.getValue());
            Part subPart = (Part) getParameter(IPRangeProfile.SUBNET_MASK + "-" + i);
            subnetBuffer.append(subPart.getValue());
        }

        InetAddress networkAddress = InetAddress.getByName(networkBuffer.toString());
        ipRange.setNetworkAddress(networkAddress.getAddress());

        InetAddress subnetMask = InetAddress.getByName(subnetBuffer.toString());
        ipRange.setSubnetMask(subnetMask.getAddress());

        ipRange.save();

    }

    private IPRange ipRange;

    /**
     * @return The IP range.
     */
    protected IPRange getIPRange() {
        return this.ipRange;
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setParameter(java.lang.String,
     *      java.lang.Object)
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

            List partNumbers = new ArrayList();
            for (byte i = 0; i < 4; i++) {
                String addrPart = Integer.toString(0xFF & networkAddress.getAddress()[i]);
                setParameter(NETWORK_ADDRESS + "-" + i, new Part(i, addrPart));
                String maskPart = Integer.toString(0xFF & subnetMask.getAddress()[i]);
                setParameter(SUBNET_MASK + "-" + i, new Part(i, maskPart));
                partNumbers.add(new Integer(i));
            }
            setParameter(IPRangeProfile.PART_NUMBERS, partNumbers);

        }
    }

    protected static void validateAddresses(AbstractUsecase usecase) {
        String[] names = { "network address", "subnet mask" };
        String[] params = { IPRangeProfile.NETWORK_ADDRESS, IPRangeProfile.SUBNET_MASK };

        for (byte type = 0; type < names.length; type++) {
            for (byte i = 0; i < 4; i++) {
                String paramName = params[type] + "-" + i;
                Part part = new Part(i);
                part.setValue(usecase.getParameterAsString(paramName));
                if (!part.isValid()) {
                	String[] parameters = { Integer.toString(i + 1), names[type] };
                    usecase.addErrorMessage("invalid-ip-address-part", parameters);
                }
                usecase.setParameter(paramName, part);
            }
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
         * @param _position The position.
         * @param _value The value.
         */
        public Part(byte _position, String _value) {
            this.value = _value;
            this.position = _position;
        }

        /**
         * Ctor.
         * @param _position The position.
         */
        public Part(byte _position) {
            this(_position, "0");
        }

        /**
         * Returns the position
         * @return The position
         */
        public byte getPosition() {
            return this.position;
        }

        /**
         * Returns the value
         * @return The value
         */
        public String getValue() {
            return this.value;
        }

        /**
         * @param _value The value.
         */
        public void setValue(String _value) {
            this.value = _value;
        }

        /**
         * Checks if the part is valid.
         * @return A boolean value.
         */
        public boolean isValid() {

            boolean valid = true;
            try {
                int i = Integer.parseInt(this.value);
                if (!(0 <= i && i <= 255)) {
                    valid = false;
                }
            } catch (NumberFormatException e) {
                valid = false;
            }

            return valid;
        }

    }

}
