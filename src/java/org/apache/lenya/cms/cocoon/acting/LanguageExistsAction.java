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

/* $Id: LanguageExistsAction.java,v 1.4 2004/03/01 16:18:21 gregor Exp $  */

package org.apache.lenya.cms.cocoon.acting;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.AbstractAction;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentDoesNotExistException;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeException;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;

/**
 * Action that checks the sitetree if there is a node with the 
 * current document-id and the current language, i.e. if the 
 * current document has a version in the current language.
 */
public class LanguageExistsAction extends AbstractAction {

    /**
     * Check if the current document-id has a document for the 
     * currently requested language.
     * 
     * If yes return an empty map, if not return null.
     * 
     * @param redirector a <code>Redirector</code> value
     * @param resolver a <code>SourceResolver</code> value
     * @param objectModel a <code>Map</code> value
     * @param source a <code>String</code> value
     * @param parameters a <code>Parameters</code> value
     *
     * @return an empty <code>Map</code> if there is a version of this 
     * document for the current language, null otherwiese
     *
     * @exception DocumentDoesNotExistException if there is no document with the specified document-id.
     * @exception PageEnvelopeException if the PageEnvelope could not be created.
     * @exception DocumentException if the language information could not be fetched from the document.
     */
    public Map act(
        Redirector redirector,
        SourceResolver resolver,
        Map objectModel,
        String source,
        Parameters parameters)
        throws PageEnvelopeException, DocumentDoesNotExistException, DocumentException {

        PageEnvelope pageEnvelope =
            PageEnvelopeFactory.getInstance().getPageEnvelope(objectModel);

        Document doc = pageEnvelope.getDocument();
        String language = doc.getLanguage();

        if (!doc.existsInAnyLanguage()) {
            throw new DocumentDoesNotExistException("Document " + doc.getId() + " does not exist");
        }
        List availableLanguages = Arrays.asList(doc.getLanguages());

        if (availableLanguages.contains(language)) {
            return Collections.unmodifiableMap(Collections.EMPTY_MAP);
        }
        return null;
    }
}