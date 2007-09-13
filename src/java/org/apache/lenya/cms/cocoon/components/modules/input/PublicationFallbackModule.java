/*
 * Created on 09.08.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.lenya.cms.cocoon.components.modules.input;

import java.util.Map;

import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.Publication;

/**
 * @author nobby
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java -
 * Code Style - Code Templates
 */
public class PublicationFallbackModule extends FallbackModule {

    /**
     * Ctor.
     */
    public PublicationFallbackModule() {
        super();
    }

    /**
     * @throws ConfigurationException
     * @see org.apache.lenya.cms.cocoon.components.modules.input.FallbackModule#getBaseURIs(java.util.Map)
     */
    protected String[] getBaseURIs(Map objectModel) throws ConfigurationException {
        String[] superUris = super.getBaseURIs(objectModel);
        String[] uris = new String[superUris.length + 1];

        PageEnvelope envelope = getEnvelope(objectModel);
        String publicationId = envelope.getPublication().getId();

        String publicationUri = "context://" + Publication.PUBLICATION_PREFIX_URI + "/"
                + publicationId + "/lenya";
        uris[0] = publicationUri;

        for (int i = 0; i < superUris.length; i++) {
            uris[i + 1] = superUris[i];
        }

        return uris;
    }
}