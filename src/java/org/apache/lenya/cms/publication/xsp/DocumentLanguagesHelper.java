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

/* $Id: DocumentLanguagesHelper.java,v 1.6 2004/03/01 16:18:27 gregor Exp $  */

package org.apache.lenya.cms.publication.xsp;

import java.util.Map;

import org.apache.cocoon.ProcessingException;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeException;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;

/**
 * Helper class for the policy GUI.
 */
public class DocumentLanguagesHelper {

    private PageEnvelope pageEnvelope = null;

    /**
	 * Create a new DocumentlanguageHelper.
	 * 
	 * @param objectModel the objectModel
	 * 
	 * @throws ProcessingException if the page envelope could not be created.
	 */
    public DocumentLanguagesHelper(Map objectModel) throws ProcessingException {
        try {
            this.pageEnvelope = PageEnvelopeFactory.getInstance().getPageEnvelope(objectModel);
        } catch (PageEnvelopeException e) {
            throw new ProcessingException(e);
        }
    }

    /**
	 * Compute the URL for a given language and the parameters given in the contructor.
	 * 
	 * @param language the language
	 * 
	 * @return the url for the given language
	 * 
	 * @throws ProcessingException if the document for the given language could not be created.
	 */
    public String getUrl(String language) throws ProcessingException {
        Document doc = getDocument(language);
        return pageEnvelope.getContext() + doc.getCompleteURL();
    }

    /**
	 * Compute the info area URL for a given language and the parameters given in the contructor.
	 * 
	 * @param language the language
	 * 
	 * @return the url for the given language
	 * 
	 * @throws ProcessingException if the document for the given language could not be created.
	 */
    public String getInfoUrl(String language) throws ProcessingException {
        Document doc = getDocument(language);
        return pageEnvelope.getContext() + doc.getCompleteInfoURL();
    }

    /**
	 * Create a document for a given language and the parameters given in the contructor.
	 * 
	 * @param language the language
	 * 
	 * @return the document with the given language
	 * 
	 * @throws ProcessingException if the document for the given language could not be created.
	 */
    protected Document getDocument(String language) throws ProcessingException {
        DocumentBuilder builder = pageEnvelope.getPublication().getDocumentBuilder();
        Document document = builder.buildLanguageVersion(pageEnvelope.getDocument(), language);
        return document;
    }
}
