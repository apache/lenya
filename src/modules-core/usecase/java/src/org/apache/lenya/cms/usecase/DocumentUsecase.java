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

package org.apache.lenya.cms.usecase;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;

/**
 * <p>
 * Extends AbstractUsecase with document helper methods.
 * </p>
 * <p>
 * Some parameters are initialized by default:
 * </p>
 * <ul>
 * <li><code>document</code>- the document</li>
 * </ul>
 */
public class DocumentUsecase extends AbstractUsecase {

    protected static final String DOCUMENT = "document";
    protected static final String TARGET_DOCUMENT = "private.targetDocument";

    /**
     * Ctor.
     */
    public DocumentUsecase() {
        super();
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doCheckPreconditions()
     */
    protected void doCheckPreconditions() throws Exception {
        super.doCheckPreconditions();
        if (getSourceDocument() == null) {
            addErrorMessage("This usecase can only be invoked on documents!");
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setSourceURL(java.lang.String)
     */
    public void setSourceURL(String url) {
        try {
            if (getDocumentFactory().isDocument(url)) {
                Document document = getDocumentFactory().getFromURL(url);
                setParameter(DOCUMENT, document);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        super.setSourceURL(url);
    }

    /**
     * Returns the source document.
     * @return A document.
     */
    protected Document getSourceDocument() {
        String url = super.getSourceURL();
        setSourceURL(url);
        return (Document) getParameter(DOCUMENT);
    }

    /**
     * Sets the target document.
     * @param document A document.
     */
    protected void setTargetDocument(Document document) {
        setParameter(TARGET_DOCUMENT, document);
    }

    /**
     * Returns the document to be redirected to after the usecase has been completed. If the
     * parameter <code>success</code> is false, the source document is returned (override this
     * method to change this behaviour).
     * @param success If the usecase was successfully completed.
     * @return A document.
     */
    protected Document getTargetDocument(boolean success) {
        Document document = (Document) getParameter(TARGET_DOCUMENT);
        if (document == null) {
            document = getSourceDocument();
        }
        return document;
    }

    /**
     * If {@link #setTargetDocument(Document)}was not called, the URL of the source document (
     * {@link #getSourceDocument()}) is returned.
     * @see org.apache.lenya.cms.usecase.Usecase#getTargetURL(boolean)
     */
    public String getTargetURL(boolean success) {
        Document document = getTargetDocument(success);
        String documentUrl = document.getCanonicalDocumentURL();
        String url = "/" + document.getPublication().getId() + "/" + document.getArea()
                + documentUrl;
        return url + getExitQueryString();
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getSourceURL()
     */
    public String getSourceURL() {
        if (getSourceDocument() == null) {
            return super.getSourceURL();
        }
        return getSourceDocument().getCanonicalWebappURL();
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#setTargetURL(java.lang.String)
     */
    protected void setTargetURL(String url) {
        try {
            Document target = getDocumentFactory().getFromURL(url);
            setParameter(TARGET_DOCUMENT, target);
        } catch (DocumentBuildException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();

        setParameter(DOCUMENT, getSourceDocument());
    }
}