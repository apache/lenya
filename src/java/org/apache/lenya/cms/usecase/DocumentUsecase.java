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
 * <li><code>document</code> - the document</li>
 * </ul>
 */
public class DocumentUsecase extends WorkflowUsecase {

    protected static final String DOCUMENT = "document";
    
    /**
     * Ctor.
     */
    public DocumentUsecase() {
        super();
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doInitialize()
     */
    protected void doInitialize() {
        super.doInitialize();
        try {
            this.sourceDocument = getUnitOfWork().getIdentityMap().getFactory().getFromURL(
                    getSourceURL());
        } catch (DocumentBuildException e) {
            throw new RuntimeException(e);
        }
    }

    private Document sourceDocument = null;

    /**
     * Returns the source document.
     * @return A document.
     */
    protected Document getSourceDocument() {
        return this.sourceDocument;
    }

    private Document targetDocument = null;

    /**
     * Sets the target document.
     * @param document A document.
     */
    protected void setTargetDocument(Document document) {
        this.targetDocument = document;
    }

    /**
     * Returns the document to be redirected to after the usecase has been
     * completed. If the parameter <code>success</code> is false, the source
     * document is returned (override this method to change this behaviour).
     * @param success If the usecase was successfully completed.
     * @return A document.
     */
    protected Document getTargetDocument(boolean success) {
        Document document;
        if (this.targetDocument != null) {
            document = this.targetDocument;
        } else {
            document = getSourceDocument();
        }
        return document;
    }

    /**
     * If {@link #setTargetDocument(Document)}was not called, the URL of the
     * source document ({@link #getSourceDocument()}) is returned.
     * @see org.apache.lenya.cms.usecase.Usecase#getTargetURL(boolean)
     */
    public String getTargetURL(boolean success) {
        return getTargetDocument(success).getCanonicalWebappURL();
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getSourceURL()
     */
    protected String getSourceURL() {
        if (this.sourceDocument == null) {
            return super.getSourceURL();
        }
        return getSourceDocument().getCanonicalWebappURL();
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#setTargetURL(java.lang.String)
     */
    protected void setTargetURL(String url) {
        try {
            this.sourceDocument = getUnitOfWork().getIdentityMap().getFactory().getFromURL(url);
        } catch (DocumentBuildException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Triggers a workflow event on the source document by calling
     * {@link #triggerWorkflow(String, Document)}.
     * @param event the event.
     */
    protected void triggerWorkflow(String event) {
        triggerWorkflow(event, getSourceDocument());
    }
    
    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#initParameters()
     */
    protected void initParameters() {
        super.initParameters();
        
        setParameter(DOCUMENT, getSourceDocument());
    }
}