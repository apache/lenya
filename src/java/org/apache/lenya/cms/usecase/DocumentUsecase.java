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

import java.util.Map;

import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.util.ServletHelper;

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
    private String completeArea;

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
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#doInitialize()
     */
    protected void doInitialize() {
        super.doInitialize();
        try {
            Map objectModel = ContextHelper.getObjectModel(getContext());
            Request request = ObjectModelHelper.getRequest(objectModel);
            String webappUri = ServletHelper.getWebappURI(request);

            DocumentFactory factory = getUnitOfWork().getIdentityMap().getFactory();
            PublicationFactory pubFactory = PublicationFactory.getInstance(getLogger());
            Publication publication = pubFactory.getPublication(this.manager, webappUri);

            if (factory.isDocument(publication, getSourceURL())) {
                this.sourceDocument = factory.getFromURL(publication, getSourceURL());
            }

            URLInformation info = new URLInformation(webappUri);
            this.completeArea = info.getCompleteArea();
        } catch (Exception e) {
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
        Document document = getTargetDocument(success);
        String documentUrl = document.getCanonicalDocumentURL();
        String url = "/" + document.getPublication().getId() + "/" + this.completeArea
                + documentUrl;
        return url;
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
            this.targetDocument = getUnitOfWork().getIdentityMap().getFactory()
                    .getFromURL(getSourceDocument().getPublication(), url);
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