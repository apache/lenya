/*
 * Created on 20.09.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.lenya.cms.usecase;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.workflow.Situation;

/**
 * @author nobby
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java -
 * Code Style - Code Templates
 */
public class DocumentUsecase extends AbstractUsecase {

    /**
     * Ctor.
     */
    public DocumentUsecase() {
        super();
    }

    /**
     * @see org.apache.lenya.cms.usecase.Usecase#setup(java.lang.String,
     *      org.apache.lenya.workflow.Situation)
     */
    public void setup(String sourceUrl, Situation situation) {
        super.setup(sourceUrl, situation);
        try {
            this.sourceDocument = getUnitOfWork().getIdentityMap().getFactory().getFromURL(
                    sourceUrl);
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
     * If {@link #setTargetDocument(Document)}was not called, the URL of the source document (
     * {@link #getSourceDocument()}) is returned.
     * @see org.apache.lenya.cms.usecase.Usecase#getTargetURL(boolean)
     */
    public String getTargetURL(boolean success) {
        return getTargetDocument(success).getCanonicalWebappURL();
    }

    /**
     * @see org.apache.lenya.cms.usecase.AbstractUsecase#getSourceURL()
     */
    protected String getSourceURL() {
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
}