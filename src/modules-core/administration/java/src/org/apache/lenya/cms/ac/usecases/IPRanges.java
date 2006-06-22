package org.apache.lenya.cms.ac.usecases;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.lenya.ac.IPRange;

/**
 * Manage IP ranges.
 * 
 * @version $Id: IPRanges.java 407305 2006-05-17 16:21:49Z andreas $
 */
public class IPRanges extends AccessControlUsecase {

    protected static final String IP_RANGES = "ipRanges";

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();
        
        IPRange[] ipRanges = getIpRangeManager().getIPRanges();
        List ipRangeList = new ArrayList();
        ipRangeList.addAll(Arrays.asList(ipRanges));
        Collections.sort(ipRangeList);
        setParameter(IP_RANGES, ipRangeList);
    }
}
