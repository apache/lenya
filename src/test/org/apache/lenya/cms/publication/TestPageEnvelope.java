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

/* $Id$  */

package org.apache.lenya.cms.publication;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.transaction.IdentityMapImpl;

/**
 * To change the template for this generated type comment go to Window>Preferences>Java>Code
 * Generation>Code and Comments
 */
public class TestPageEnvelope extends PageEnvelope {
    /**
     * Constructs a test page envelope.
     * @param publication The publication.
     * @param url The document url (starting with a slash).
     * @throws PageEnvelopeException when something goes wrong.
     */
    public TestPageEnvelope(Publication publication, String url, ServiceManager manager,
            Logger logger) throws PageEnvelopeException {
        setContext("");

        try {
            IdentityMapImpl idMap = new IdentityMapImpl(logger);
            DocumentIdentityMap map = new DocumentIdentityMap(idMap, manager, logger);
            setDocument(map.getFromURL(url));
        } catch (DocumentBuildException e) {
            throw new PageEnvelopeException(e);
        }
    }
}