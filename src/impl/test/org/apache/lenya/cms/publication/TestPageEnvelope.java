/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;

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
        super();
        setContext("");

        try {
            Session session = RepositoryUtil.createSession(manager, null, false);
            DocumentFactory map = DocumentUtil.createDocumentFactory(manager, session);
            setDocument(map.getFromURL(url));
        } catch (Exception e) {
            throw new PageEnvelopeException(e);
        }
    }
}