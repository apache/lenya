/*
 * PageEnvelopeAction.java
 *
 * Created on 10. April 2003, 13:43
 */

package org.lenya.cms.cocoon.acting;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.cocoon.acting.AbstractAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.lenya.cms.publication.PageEnvelope;

/**
 *
 * @author  nobby
 */
public class PageEnvelopeAction
    extends AbstractAction {
    
    public java.util.Map act(org.apache.cocoon.environment.Redirector redirector,
        org.apache.cocoon.environment.SourceResolver resolver,
        java.util.Map objectModel,
        String str,
        org.apache.avalon.framework.parameters.Parameters parameters)
            throws java.lang.Exception {
                
        Request request = ObjectModelHelper.getRequest(objectModel);
        PageEnvelope envelope = new PageEnvelope(resolver, request);
                
        Map result = new HashMap();
        
        result.put(PageEnvelope.PUBLICATION_ID, envelope.getPublication().getId());
        result.put(PageEnvelope.CONTEXT, envelope.getContext());
        
        return result;
    }
    
}
