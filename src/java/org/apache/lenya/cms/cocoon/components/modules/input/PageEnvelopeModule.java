/*
 * PageEnvelopeModule.java
 *
 * Created on 16. Mai 2003, 12:40
 */

package org.apache.lenya.cms.cocoon.components.modules.input;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.components.modules.input.AbstractInputModule;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeException;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;

/**
 *
 * @author  andreas
 */
public class PageEnvelopeModule
    extends AbstractInputModule {
        
    protected PageEnvelope getEnvelope(Map objectModel) {
        
        PageEnvelope envelope = null;
        
        try {
            Publication publication = PublicationFactory.getPublication(objectModel);
            Request request = ObjectModelHelper.getRequest(objectModel);
            envelope = new PageEnvelope(publication, request);
        }
        catch (Exception e) {
            getLogger().error("Resolving page envelope failed: ", e);
        }
        
        return envelope;
    }
        
    public Object getAttribute(String name, Configuration modeConf, Map objectModel)
        throws ConfigurationException {
            
        PageEnvelope envelope = getEnvelope(objectModel);
        Object value = null;
            
        if (name.equals(PageEnvelope.AREA)) {
            value = envelope.getArea();
        }
        if (name.equals(PageEnvelope.CONTEXT)) {
            value = envelope.getContext();
        }
        if (name.equals(PageEnvelope.PUBLICATION_ID)) {
            value = envelope.getPublication().getId();
        }
        if (name.equals(PageEnvelope.PUBLICATION)) {
            value = envelope.getPublication();
        }
        if (name.equals(PageEnvelope.DOCUMENT_ID)) {
            value = envelope.getDocumentId();
        }
        if (name.equals(PageEnvelope.DOCUMENT_URL)) {
            value = envelope.getDocumentURL();
        }
        return value;
    }
    
    public Iterator getAttributeNames(Configuration modeConf, Map objectModel)
        throws ConfigurationException {
            
        return Arrays.asList(PageEnvelope.PARAMETER_NAMES).iterator();
    }
    
    public Object[] getAttributeValues(String name, Configuration modeConf, Map objectModel)
        throws ConfigurationException {
        
        Object objects[] = { getAttribute(name, modeConf, objectModel) };
        return objects;
    }
    
}
