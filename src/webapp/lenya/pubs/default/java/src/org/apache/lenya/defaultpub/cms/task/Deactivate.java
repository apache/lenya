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

/* $Id: Deactivate.java,v 1.2 2004/03/20 11:46:20 gregor Exp $  */

package org.apache.lenya.defaultpub.cms.task;

import java.io.IOException;

import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.Label;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.ResourcesManager;
import org.apache.lenya.cms.publication.SiteTree;
import org.apache.lenya.cms.publication.SiteTreeException;
import org.apache.lenya.cms.publication.SiteTreeNode;
import org.apache.lenya.cms.publication.task.PublicationTask;
import org.apache.lenya.cms.task.ExecutionException;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.log4j.Category;

/**
 * Deactivate a document.
 */
public class Deactivate extends PublicationTask {

    private static final Category log = Category.getInstance(Deactivate.class);

    public static final String PARAMETER_DOCUMENT_ID = "document-id";
    public static final String PARAMETER_DOCUMENT_LANGUAGE = "document-language";

    /**
     * @see org.apache.lenya.cms.task.Task#execute(java.lang.String)
     */
    public void execute(String servletContextPath) throws ExecutionException {
        if (log.isDebugEnabled()) {
            log.debug("Starting deactivation");
        }

        try {
            Document liveDocument = getLiveDocument();

            if (!checkPreconditions(liveDocument)) {
                setResult(FAILURE);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Can execute task: last label, children are published.");
                }
                deactivate(liveDocument);
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
     * @param liveDocument The document to publish. 
     * @return A boolean value.
     * @throws PublicationException when something went wrong.
     * @throws ExecutionException when something went wrong.
     * @throws DocumentException when something went wrong.
     * FIXME: Remove references to sitetree
     */
    protected boolean checkPreconditions(Document liveDocument)
        throws
            PublicationException,
            DocumentException,
            SiteTreeException,
            ExecutionException {
        boolean OK = true;

        Document authoringDocument = getPublication().getAreaVersion(liveDocument, Publication.AUTHORING_AREA);
        OK = OK && canWorkflowFire(authoringDocument);

        SiteTree tree = getPublication().getSiteTree(liveDocument.getArea());
        SiteTreeNode node = tree.getNode(liveDocument.getId());

        if (node == null) {
            throw new ExecutionException(
                "Sitetree node for document [" + liveDocument + "] does not exist!");
        }

        Label label = node.getLabel(liveDocument.getLanguage());

        if (label == null) {
            throw new ExecutionException(
                "Sitetree label for document [" + liveDocument + "] does not exist!");
        }

        if (node.getLabels().length == 1 && node.getChildren().length > 0) {
            if (log.isDebugEnabled()) {
                log.debug(
                    "Cannot delete last language version of document ["
                        + liveDocument
                        + "] because this node has children.");
            }
            OK = false;
        }

        return OK;
    }

    /**
     * Deactivates a document.
     * @param liveDocument The live document.
     */
    protected void deactivate(Document liveDocument)
        throws
            PublicationException,
            ExecutionException,
            IOException,
            ParameterException,
            WorkflowException,
            DocumentException {
        getPublication().deleteDocument(liveDocument);

        if (!liveDocument.existsInAnyLanguage()) {
            ResourcesManager resourcesManager = new ResourcesManager(liveDocument);
            resourcesManager.deleteResources();
        }

        Document authoringDocument = getPublication().getAreaVersion(liveDocument, Publication.AUTHORING_AREA);
        triggerWorkflow(authoringDocument);
    }

    /**
     * Returns the live document to apply this task on.
     * @return A document.
     * @throws ParameterException when something went wrong.
     * @throws DocumentBuildException when something went wrong.
     * @throws ExecutionException when something went wrong.
     */
    protected Document getLiveDocument()
        throws ParameterException, DocumentBuildException, ExecutionException {
        String id = getParameters().getParameter(PARAMETER_DOCUMENT_ID);
        String language = getParameters().getParameter(PARAMETER_DOCUMENT_LANGUAGE);
        DocumentBuilder builder = getPublication().getDocumentBuilder();
        String url =
            builder.buildCanonicalUrl(getPublication(), Publication.LIVE_AREA, id, language);
        Document document = builder.buildDocument(getPublication(), url);
        return document;
    }

}
