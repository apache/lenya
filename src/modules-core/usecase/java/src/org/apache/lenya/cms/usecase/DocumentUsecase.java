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

package org.apache.lenya.cms.usecase;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentFactory;

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

    /*
    public void setParameter(String name, Object value) {
        if (name.equals(SOURCE_URL)) {
            setSourceURL((String) value);
        }
        else {
            super.setParameter(name, value);
        }
    }
    */

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setSourceURL(java.lang.String)
    public void setSourceURL(String url) {
        try {
            DocumentFactory factory = getDocumentFactory();
            if (factory.isDocument(url)) {
                Document document = factory.getFromURL(url);
                setParameter(DOCUMENT, document);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        super.setParameter(SOURCE_URL, url);
    }
     */

    /**
     * Returns the source document.
     * @return A document.
     */
    protected Document getSourceDocument() {
        Document doc = (Document) getParameter(DOCUMENT);
        if (doc == null || doc.getFactory().getSession() != getSession()) {
            try {
                DocumentFactory factory = getDocumentFactory();
                String sourceUrl = getParameterAsString(SOURCE_URL);
                if (factory.isDocument(sourceUrl)) {
                    doc = factory.getFromURL(sourceUrl);
                    setParameter(DOCUMENT, doc);
                }
            } catch (DocumentBuildException e) {
                throw new RuntimeException(e);
            }
        }
        return doc;
    }

    /**
     * Sets the target document for the case that the usecase execution
     * succeeded (see {@link #getTargetDocument(boolean)}).
     * @param document A document.
     */
    protected void setTargetDocument(Document document) {
        setParameter(TARGET_DOCUMENT, document);
    }

    /**
     * Returns the document to be redirected to after the usecase has been
     * completed. If the parameter <code>success</code> is false, the source
     * document is returned (override this method to change this behaviour).
     * @param success If the usecase was successfully completed.
     * @return A document.
     */
    protected Document getTargetDocument(boolean success) {
        if (success) {
            return (Document) getParameter(TARGET_DOCUMENT, getSourceDocument());
        } else {
            return getSourceDocument();
        }
    }

    /**
     * If {@link #setTargetDocument(Document)}was not called, the URL of the
     * source document ( {@link #getSourceDocument()}) is returned.
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
        Document doc = getSourceDocument();
        if (doc == null || !doc.hasLink()) {
            return super.getSourceURL();
        }
        return doc.getCanonicalWebappURL();
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#setDefaultTargetURL(java.lang.String)
     */
    protected void setDefaultTargetURL(String url) {
        try {
            Document target = getDocumentFactory().getFromURL(url);
            setParameter(TARGET_DOCUMENT, target);
        } catch (DocumentBuildException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
    protected void initParameters() {
        super.initParameters();

        setParameter(DOCUMENT, getSourceDocument());
    }
     */
}