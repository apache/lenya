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

import org.apache.avalon.framework.service.ServiceException;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.cms.publication.URLInformation;

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
    private String COMPLETE_AREA = "private.completeArea";

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
            DocumentFactory factory = getUnitOfWork().getIdentityMap().getFactory();
            PublicationFactory pubFactory = PublicationFactory.getInstance(getLogger());
            Publication publication = pubFactory.getPublication(this.manager, url);

            if (factory.isDocument(publication, url)) {
                setParameter(DOCUMENT, factory.getFromURL(publication, url));
            }

            URLInformation info = new URLInformation(url);
            setParameter(COMPLETE_AREA, info.getCompleteArea());
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
        String completeArea = getParameterAsString(COMPLETE_AREA);
        String documentArea = document.getArea();
        if (completeArea.startsWith(Publication.INFO_AREA_PREFIX)) {
            documentArea = Publication.INFO_AREA_PREFIX + documentArea;
        }
        String url = "/" + document.getPublication().getId() + "/" + documentArea + documentUrl;
        return url;
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
            Document target = getUnitOfWork().getIdentityMap().getFactory()
                    .getFromURL(getSourceDocument().getPublication(), url);
            setParameter(TARGET_DOCUMENT, target);
        } catch (ServiceException e) {
            throw new RuntimeException(e);
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