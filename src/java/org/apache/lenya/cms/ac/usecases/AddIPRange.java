/*
 * Created on 04.01.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.lenya.cms.ac.usecases;

import java.io.File;

import org.apache.lenya.ac.IPRange;
import org.apache.lenya.ac.file.FileIPRange;
import org.apache.lenya.ac.file.FileIPRangeManager;
import org.apache.lenya.ac.impl.AbstractItem;
import org.apache.lenya.cms.ac.usecases.IPRangeProfile.Part;
import org.apache.lenya.cms.usecase.UsecaseException;

/**
 * Usecase to add an IP range.
 * 
 * @version $Id:$
 */
public class AddIPRange extends AccessControlUsecase {

    /**
     * Validates the request parameters.
     * @throws UsecaseException if an error occurs.
     */
    void validate() throws UsecaseException {

        String id = getParameterAsString(IPRangeProfile.ID);

        IPRange existingIPRange = getIpRangeManager().getIPRange(id);

        if (existingIPRange != null) {
            addErrorMessage("This IP range already exists.");
        }

        if (!AbstractItem.isValidId(id)) {
            addErrorMessage("This is not a valid IP range ID.");
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckExecutionConditions()
     */
    protected void doCheckExecutionConditions() throws Exception {
        validate();
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        File configDir = ((FileIPRangeManager) getIpRangeManager()).getConfigurationDirectory();

        String id = getParameterAsString(IPRangeProfile.ID);
        String name = getParameterAsString(IPRangeProfile.NAME);
        String description = getParameterAsString(IPRangeProfile.DESCRIPTION);

        IPRange ipRange = new FileIPRange(configDir, id);
        ipRange.setName(name);

        ipRange.setDescription(description);
        ipRange.save();
        getIpRangeManager().add(ipRange);
    }
    
    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();
        for (byte i = 0; i < 4; i++) {
            setParameter(IPRangeProfile.NETWORK_ADDRESS + "-" + i, new Part(i));
            setParameter(IPRangeProfile.SUBNET_MASK + "-" + i, new Part(i));
        }
    }
}