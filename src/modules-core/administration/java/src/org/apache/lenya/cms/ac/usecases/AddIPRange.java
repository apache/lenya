package org.apache.lenya.cms.ac.usecases;


import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.lenya.ac.IPRange;
import org.apache.lenya.ac.ItemUtil;
import org.apache.lenya.cms.ac.usecases.IPRangeProfile.Part;

/**
 * Usecase to add an IP range.
 */
public class AddIPRange extends AccessControlUsecase {

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {
        String id = getParameterAsString(IPRangeProfile.ID);

        IPRange existingIPRange = getIpRangeManager().getIPRange(id);

        if (existingIPRange != null) {
            addErrorMessage("This IP range already exists.");
        }

        if (!ItemUtil.isValidId(id)) {
            addErrorMessage("This is not a valid IP range ID.");
        }

        IPRangeProfile.validateAddresses(this);
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {

        String id = getParameterAsString(IPRangeProfile.ID);
        String name = getParameterAsString(IPRangeProfile.NAME);
        String description = getParameterAsString(IPRangeProfile.DESCRIPTION);

        IPRange ipRange = getIpRangeManager().add(id);
        ContainerUtil.enableLogging(ipRange, getLogger());

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
        
        setExitParameter(IPRangeProfile.ID, id);
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();
        List partNumbers = new ArrayList();
        for (byte i = 0; i < 4; i++) {
            setParameter(IPRangeProfile.NETWORK_ADDRESS + "-" + i, new Part(i));
            setParameter(IPRangeProfile.SUBNET_MASK + "-" + i, new Part(i));
            partNumbers.add(new Integer(i));
        }
        setParameter(IPRangeProfile.PART_NUMBERS, partNumbers);
    }
}