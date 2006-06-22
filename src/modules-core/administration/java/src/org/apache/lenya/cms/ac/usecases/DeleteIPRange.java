package org.apache.lenya.cms.ac.usecases;


/**
 * Delete an IP range.
 *
 * @version $Id: DeleteIPRange.java 407305 2006-05-17 16:21:49Z andreas $
 */
public class DeleteIPRange extends AccessControlUsecase {

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doExecute()
     */
    protected void doExecute() throws Exception {
        super.doExecute();

        String id = getParameterAsString(IPRangeProfile.ID);
        org.apache.lenya.ac.IPRange ipRange = getIpRangeManager().getIPRange(id);
        if (ipRange == null) {
            throw new RuntimeException("IP range [" + ipRange + "] not found.");
        }
        
        getIpRangeManager().remove(ipRange);
        ipRange.delete();
    }
}
