/*
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
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
 * 
 * @author <a href="mailto:andreas@apache.org">Andreas Hartmann</a>
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
