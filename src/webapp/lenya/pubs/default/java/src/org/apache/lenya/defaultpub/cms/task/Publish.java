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

/* $Id: Publish.java,v 1.2 2004/03/20 11:46:20 gregor Exp $  */

package org.apache.lenya.defaultpub.cms.task;

import java.io.IOException;

import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.cocoon.ProcessingException;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DocumentHelper;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.SiteTree;
import org.apache.lenya.cms.publication.SiteTreeException;
import org.apache.lenya.cms.publication.SiteTreeNode;
import org.apache.lenya.cms.publication.task.PublicationTask;
import org.apache.lenya.cms.task.ExecutionException;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.log4j.Category;

/**
 * Publish a document.
 */
public class Publish extends PublicationTask {

    private static final Category log = Category.getInstance(Publish.class);

    public static final String PARAMETER_DOCUMENT_ID = "document-id";
    public static final String PARAMETER_DOCUMENT_LANGUAGE = "document-language";

    /**
     * @see org.apache.lenya.cms.task.Task#execute(java.lang.String)
     */
    public void execute(String servletContextPath) throws ExecutionException {

        if (log.isDebugEnabled()) {
            log.debug("Starting publishing");
        }

        try {
            Document authoringDocument = getAuthoringDocument();

            if (!checkPreconditions(authoringDocument)) {
                setResult(FAILURE);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Can execute task: parent is published.");
                }
                publish(authoringDocument);
                setResult(SUCCESS);
            }

        } catch (ExecutionException e) {
            throw e;
        } catch (Exception e) {
            throw new ExecutionException(e);
        }

    }

    /**
     * Checks if the preconditions are complied.
     * @param document The document to publish. 
     * @return
     * @throws PublicationException
     * @throws ExecutionException
     * @throws DocumentException
     */
    protected boolean checkPreconditions(Document document)
        throws
            PublicationException,
            DocumentException,
            ProcessingException,
            SiteTreeException,
            ExecutionException {
        boolean OK = true;

        if (!canWorkflowFire(document)) {
            OK = false;
            log.error("Cannot execute task: workflow event not supported.");
        }

        Document liveDocument = getPublication().getAreaVersion(document, Publication.LIVE_AREA);
        Document liveParent = DocumentHelper.getParentDocument(liveDocument);
        if (liveParent != null) {
            SiteTree liveTree = getPublication().getSiteTree(Publication.LIVE_AREA);
            SiteTreeNode liveParentNode = liveTree.getNode(liveParent.getId());
            if (liveParentNode == null) {
                log.error("Cannot execute task: live parent node does not exist.");
                OK = false;
            }
        }

        return OK;
    }

    /**
     * Publishes a document.
     * @param authoringDocument The authoring document.
     */
    protected void publish(Document authoringDocument)
        throws
            PublicationException,
            ExecutionException,
            IOException,
            ParameterException,
            WorkflowException {

        getPublication().copyDocumentToArea(authoringDocument, Publication.LIVE_AREA);

        Document liveDocument =
            getPublication().getAreaVersion(authoringDocument, Publication.LIVE_AREA);

        copyResources(authoringDocument, liveDocument);

        triggerWorkflow(authoringDocument);
    }

    /**
     * Returns the authoring document to apply this task on.
     * @return A document.
     * @throws ParameterException when something went wrong.
     * @throws DocumentBuildException when something went wrong.
     * @throws ExecutionException when something went wrong.
     */
    protected Document getAuthoringDocument()
        throws ParameterException, DocumentBuildException, ExecutionException {
        String id = getParameters().getParameter(PARAMETER_DOCUMENT_ID);
        String language = getParameters().getParameter(PARAMETER_DOCUMENT_LANGUAGE);
        DocumentBuilder builder = getPublication().getDocumentBuilder();
        String url =
            builder.buildCanonicalUrl(getPublication(), Publication.AUTHORING_AREA, id, language);
        Document document = builder.buildDocument(getPublication(), url);
        return document;
    }

}
