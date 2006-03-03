/*
 * Copyright  1999-2005 The Apache Software Foundation
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

package org.apache.lenya.cms.metadata.dublincore;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.metadata.MetaData;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.Publication;
import org.apache.log4j.Logger;

/**
 * Facade to get the DublinCore through the cms Document
 */
public final class DublinCoreHelper {

    /**
     * Constructor
     */
    private DublinCoreHelper() {
        // do nothing
    }

    private static Logger log = Logger.getLogger(DublinCoreHelper.class);
    private static Logger getLogger() {
        return log;
    }

    /**
     * Get the value of the DCIdentifier corresponding to a document id.
     * @param manager The service manager.
     * @param map The identity map.
     * @param publication The publication the document(s) belongs to.
     * @param area The area the document(s) belongs to.
     * @param documentId The document id.
     * @return a String. The value of the DCIdentifier.
     * @throws DocumentException when something with the document went wrong.
     */
    public static String getDCIdentifier(ServiceManager manager, DocumentIdentityMap map,
            Publication publication, String area, String documentId) throws DocumentException {

        String identifier = null;
        SourceResolver resolver = null;
        try {
            resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
            identifier = null;

            Document baseDocument = map.get(publication, area, documentId);
            String[] languages = baseDocument.getLanguages();

            int i = 0;
            if (languages.length > 0) {
                while (identifier == null && i < languages.length) {
                    Document document = map.get(publication, area, documentId, languages[i]);
                    if (getLogger().isDebugEnabled())
                        getLogger().debug("document file : " + document.getSourceURI());
                    MetaData dublincore = document.getMetaDataManager().getDublinCoreMetaData();
                    if (getLogger().isDebugEnabled())
                        getLogger().debug("dublincore title : "
                            + dublincore.getFirstValue(DublinCore.ELEMENT_TITLE));
                    identifier = dublincore.getFirstValue(DublinCore.ELEMENT_IDENTIFIER);
                    i = i + 1;
                }
            }
            if (languages.length < 1 || identifier == null) {
                MetaData dublincore = baseDocument.getMetaDataManager().getDublinCoreMetaData();
                identifier = dublincore.getFirstValue(DublinCore.ELEMENT_IDENTIFIER);
            }
        } catch (final DocumentException e) {
            throw e;
        } catch (final Exception e) {
            throw new DocumentException(e);
        }
        finally {
            if (resolver != null) {
                manager.release(resolver);
            }
        }

        return identifier;
    }
}
