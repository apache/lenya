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
import org.lenya.cms.publishing.PublishingEnvironment;
import org.lenya.cms.rc.RCEnvironment;

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
        PageEnvelope envelope = null; 
        try {
          envelope = new PageEnvelope(resolver, request);
        }
        catch (Exception e) {
          getLogger().error(e.getMessage());
          return null;
        }        
        Map result = new HashMap();
        
        result.put(PageEnvelope.PUBLICATION_ID, envelope.getPublication().getId());
        result.put(PageEnvelope.CONTEXT, envelope.getContext());
        result.put(PageEnvelope.AREA, envelope.getArea());
        result.put(PublishingEnvironment.PUBLICATION_PATH, envelope.getPublication().getEnvironment().getPublicationPath());
        result.put(RCEnvironment.RCML_DIRECTORY, envelope.getRCEnvironment().getRCMLDirectory());
        result.put(RCEnvironment.BACKUP_DIRECTORY, envelope.getRCEnvironment().getBackupDirectory());

        return result;
    }
    
}
