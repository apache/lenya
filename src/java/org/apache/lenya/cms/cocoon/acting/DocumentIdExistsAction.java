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

/* $Id: DocumentIdExistsAction.java,v 1.3 2004/03/01 16:18:21 gregor Exp $  */

package org.apache.lenya.cms.cocoon.acting;

import java.util.Collections;
import java.util.Map;

import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.AbstractAction;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeException;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.SiteTree;
import org.apache.lenya.cms.publication.SiteTreeException;

/**
 * Action that checks the sitetree if there is a node with the 
 * current document-id. This is used to prevent creation of documents
 * with non-unique document-ids
 */
public class DocumentIdExistsAction extends AbstractAction {

    /**
     * Check if there is a doument in the site tree with the given
     * document-id.
     * 
     * If yes return an empty map, if not return null.
     * 
     * @param redirector a <code>Redirector</code> value
     * @param resolver a <code>SourceResolver</code> value
     * @param objectModel a <code>Map</code> value
     * @param source a <code>String</code> value
     * @param parameters a <code>Parameters</code> value
     *
     * @return an empty <code>Map</code> if there is a document 
     * with the given document-id, null otherwiese
     *
     * @exception DocumentDoesNotExistException if there is no document with the specified document-id.
     * @exception PageEnvelopeException if the PageEnvelope could not be created.
     * @exception DocumentException if the language information could not be fetched from the document.
     */

    public static final String DOCUMENT_ID_PARAMETER_NAME = "document-id";

    public Map act(
        Redirector redirector,
        SourceResolver resolver,
        Map objectModel,
        String source,
        Parameters parameters)
        throws PageEnvelopeException, SiteTreeException, ParameterException {

        String documentId = parameters.getParameter(DOCUMENT_ID_PARAMETER_NAME);

        if (documentId == null) {
            return null;
        }

        PageEnvelope pageEnvelope =
            PageEnvelopeFactory.getInstance().getPageEnvelope(objectModel);

        SiteTree siteTree =
            pageEnvelope.getPublication().getSiteTree(
                Publication.AUTHORING_AREA);

        if (siteTree.getNode(documentId) == null) {
            return Collections.EMPTY_MAP;
        }
        return null;
    }
}