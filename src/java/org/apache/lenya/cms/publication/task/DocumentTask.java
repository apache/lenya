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

/* $Id: DocumentTask.java,v 1.2 2004/03/01 16:18:27 gregor Exp $  */

package org.apache.lenya.cms.publication.task;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.task.ExecutionException;

/**
 * Abstract super class for document-based tasks.
 */
public abstract class DocumentTask extends PublicationTask {

    public static final String PARAMETER_DOCUMENT_ID = "document-id";
    public static final String PARAMETER_DOCUMENT_AREA = "document-area";
    public static final String PARAMETER_DOCUMENT_LANGUAGE = "document-language";

    /**
     * Returns the document specified using the default document parameters
     * ({@link #PARAMETER_DOCUMENT_ID}, {@link #PARAMETER_DOCUMENT_AREA}, {@link #PARAMETER_DOCUMENT_LANGUAGE}).
     * @return A document.
     * @throws ExecutionException when something went wrong.
     */
    protected Document getDocument() throws ExecutionException {
        Document document;
        try {
            String id = getParameters().getParameter(PARAMETER_DOCUMENT_ID);
            String area = getParameters().getParameter(PARAMETER_DOCUMENT_AREA);
            String language = getParameters().getParameter(PARAMETER_DOCUMENT_LANGUAGE);
            document = getDocument(id, area, language);
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
        return document;
    }

    /**
     * Creates a document.
     * @param documentId The document ID.
     * @param area The area.
     * @param language The language.
     * @return A document.
     * @throws ExecutionException when something went wrong.
     */
    protected Document getDocument(String documentId, String area, String language)
        throws ExecutionException {
        Publication publication = getPublication();
        DocumentBuilder builder = publication.getDocumentBuilder();
        String url = builder.buildCanonicalUrl(publication, area, documentId, language);
        Document document;
        try {
            document = builder.buildDocument(publication, url);
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
        return document;
    }

}
