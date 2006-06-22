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
        
        String networkString = "";
        String subnetString = "";
        
        for (int i = 0; i < 4; i++) {
            if (i > 0) {
                networkString += ".";
                subnetString += ".";
            }
            Part netPart = (Part) getParameter(IPRangeProfile.NETWORK_ADDRESS + "-" + i);
            networkString += netPart.getValue();
            Part subPart = (Part) getParameter(IPRangeProfile.SUBNET_MASK + "-" + i);
            subnetString += subPart.getValue();
        }

        InetAddress networkAddress = InetAddress.getByName(networkString);
        ipRange.setNetworkAddress(networkAddress.getAddress());

        InetAddress subnetMask = InetAddress.getByName(subnetString);
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

            List partNumbers = new ArrayList();
            for (byte i = 0; i < 4; i++) {
                setParameter(NETWORK_ADDRESS + "-" + i, new Part(i, ""
                        + networkAddress.getAddress()[i]));
                setParameter(SUBNET_MASK + "-" + i, new Part(i, "" + subnetMask.getAddress()[i]));
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
                    usecase.addErrorMessage("Part " + (i + 1) + " of the " + names[type]
                            + " is not valid.");
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