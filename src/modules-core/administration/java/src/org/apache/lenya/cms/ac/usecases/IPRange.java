package org.apache.lenya.cms.ac.usecases;


/**
 * Show information about an IP range.
 * 
 * @version $Id: IPRange.java 407305 2006-05-17 16:21:49Z andreas $
 */
public class IPRange extends AccessControlUsecase {

    protected static final String IP_RANGE_ID = "ipRangeId";
    protected static final String IP_RANGE = "ipRange";

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setParameter(java.lang.String, java.lang.Object)
     */
    public void setParameter(String name, Object value) {
        super.setParameter(name, value);

        if (name.equals(IP_RANGE_ID)) {
            String ipRangeId = (String) value;
            org.apache.lenya.ac.IPRange ipRange = getIpRangeManager().getIPRange(ipRangeId);
            if (ipRange == null) {
                addErrorMessage("iprange_no_such_iprange", new String[] { ipRangeId });
            } else {
                setParameter(IP_RANGE, ipRange);
            }
        }
    }
}