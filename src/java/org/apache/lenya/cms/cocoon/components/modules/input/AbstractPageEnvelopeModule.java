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

/* $Id: AbstractPageEnvelopeModule.java,v 1.2 2004/03/01 16:18:24 gregor Exp $  */

package org.apache.lenya.cms.cocoon.components.modules.input;

import java.util.Map;

import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.cocoon.components.modules.input.AbstractInputModule;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;

/**
 * Abstract superclass for classes which need access to the page envelope.
 */
public abstract class AbstractPageEnvelopeModule extends AbstractInputModule {

    /**
     * Get the the page envelope for the given objectModel.
     * 
     * @param objectModel the objectModel for which the page enevelope is requested.
     * 
     * @return a <code>PageEnvelope</code>
     * 
     * @throws ConfigurationException if the page envelope could not be instantiated.
     */
    protected PageEnvelope getEnvelope(Map objectModel) throws ConfigurationException {
        
        PageEnvelope envelope = null;

        if (getLogger().isDebugEnabled()) {
            Request request = ObjectModelHelper.getRequest(objectModel);
            getLogger().debug("Resolving page envelope for URL [" + request.getRequestURI() + "]");
        }

        try {
            envelope = PageEnvelopeFactory.getInstance().getPageEnvelope(objectModel);
        } catch (Exception e) {
            throw new ConfigurationException("Resolving page envelope failed: ", e);
        }

        return envelope;
    }

}
