/*
 * Created on 04.01.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.lenya.cms.ac.usecases;

import java.net.InetAddress;

import org.apache.lenya.ac.IPRange;

/**
 * Usecase to change the profile of an IP range.
 *
 * @version $Id:$
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
