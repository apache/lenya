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

import java.util.Map;

/**
 * Common entry point for creating page envelopes.
 */
public class PageEnvelopeFactory {
    /**
     * Creates a new PageEnvelopeFactory.
     */
    protected PageEnvelopeFactory() {
    }

    private static PageEnvelopeFactory instance;

    /**
     * Returns the singleton PageEnvelopeFactory.
     * @return The factory.
     */
    public static PageEnvelopeFactory getInstance() {
        if (instance == null) {
            instance = new PageEnvelopeFactory();
        }
        return instance;
    }

    /**
     * Returns the page envelope for the object model of a Cocoon component.
     * @param map The document identity map to use.
     * @param objectModel The object model.
     * @return A page envelope.
     * @throws PageEnvelopeException if something went wrong.
     */
    public PageEnvelope getPageEnvelope(DocumentIdentityMap map, Map objectModel)
            throws PageEnvelopeException {
        PageEnvelope envelope = new PageEnvelope(map, objectModel);
        return envelope;
    }

}