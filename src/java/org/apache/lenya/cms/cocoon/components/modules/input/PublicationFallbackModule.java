/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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

package org.apache.lenya.cms.cocoon.components.modules.input;

import java.util.Map;

import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.Publication;

/**
 * Publication Fallback Module
 */
public class PublicationFallbackModule extends FallbackModule {

    /**
     * Ctor.
     */
    public PublicationFallbackModule() {
        super();
    }

    /**
     * @see org.apache.lenya.cms.cocoon.components.modules.input.FallbackModule#getBaseURIs(java.util.Map,
     *      java.lang.String)
     */
    protected String[] getBaseURIs(Map objectModel, String name) throws ConfigurationException {
        String[] superUris = super.getBaseURIs(objectModel, name);
        String[] uris = new String[superUris.length + 1];

        PageEnvelope envelope = getEnvelope(objectModel, name);
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