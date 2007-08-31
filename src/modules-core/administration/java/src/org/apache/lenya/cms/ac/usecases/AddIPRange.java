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
