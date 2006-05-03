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

/* $Id$  */

package org.apache.lenya.defaultpub.cms.task;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.DocumentHelper;
import org.apache.lenya.cms.publication.DublinCore;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.SiteTree;
import org.apache.lenya.cms.publication.SiteTreeNode;
import org.apache.lenya.cms.publication.task.PublicationTask;
import org.apache.lenya.cms.task.ExecutionException;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.log4j.Logger;

/**
 * Publish a document.
 */
public class Publish extends PublicationTask {

    private static final Logger log = Logger.getLogger(Publish.class);

    public static final String PARAMETER_DOCUMENT_ID = "document-id";
    public static final String PARAMETER_DOCUMENT_LANGUAGE = "document-language";
    public static final String PARAMETER_USER_NAME = "user-name";
    public static final String PARAMETER_USER_EMAIL = "user-email";

    private static final String format = "yyyy-MM-dd HH:mm:ss";
    
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
                String date = new SimpleDateFormat(format).format(new Date());
                reservedCheckOut(authoringDocument);
                setPublicationDate(authoringDocument, date);
                setModificationDate(authoringDocument, date);
                setPublisher(authoringDocument);
                authoringDocument.getDublinCore().save();
                reservedCheckIn(authoringDocument, false);

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
     * set the publisher (the user-id, the user-name and the user-email seperate with a |) 
     * @param document The document.
     * @throws PublicationException When something went wrong.
     * @throws ParameterException When something went wrong.
     */
    public void setPublisher(Document document) throws PublicationException, ParameterException {
        String userId = getParameters().getParameter(PARAMETER_USER_ID);
        String userName = getParameters().getParameter(PARAMETER_USER_NAME);
        String userEmail = getParameters().getParameter(PARAMETER_USER_EMAIL);
        //String publisher = document.getDublinCore().getFirstValue(DublinCore.ELEMENT_PUBLISHER);
        document.getDublinCore().setValue(DublinCore.ELEMENT_PUBLISHER, userId + "|" + userName + "|" + userEmail);
    }

    /**set the date of the publication, the date of the last change  
     * @param document The document.
     * @param date The date in the format yyyy-MM-dd HH:mm:ss.
     * @throws PublicationException When something went wrong
     */
    public void setModificationDate(Document document,String date) throws PublicationException {
        //String lastModDate = document.getDublinCore().getFirstValue(DublinCore.TERM_MODIFIED);
        document.getDublinCore().setValue(DublinCore.TERM_MODIFIED, date);
    }
    
    /** set the date of the first publication
     * @param document The document.
     * @param date The date in the format yyyy-MM-dd HH:mm:ss.
     * @throws PublicationException When something went wrong
     */
    public void setPublicationDate(Document document,String date) throws PublicationException {
        String publicationDate = document.getDublinCore().getFirstValue(DublinCore.TERM_ISSUED);
        if (publicationDate != null && publicationDate.length() > 0){
            return;
        }
        document.getDublinCore().setValue(DublinCore.TERM_ISSUED, date);
    }

    /**
     * Checks if the preconditions are complied.
     * @param document The document to publish. 
     * @return
     * @throws ExecutionException
     * @throws Exception
     * @throws IOException
     */
    protected boolean checkPreconditions(Document document)
        throws
            ExecutionException, IOException, Exception {
        boolean OK = true;

        if (!canWorkflowFire(document)) {
            OK = false;
            log.error("Cannot execute task: workflow event not supported.");
        }

        Document liveDocument = getPublication().getAreaVersion(document, Publication.LIVE_AREA);
        Document liveParent = DocumentHelper.getParentDocument(liveDocument);
        if (liveParent != null) {
            SiteTree liveTree = getPublication().getTree(Publication.LIVE_AREA);
            SiteTreeNode liveParentNode = liveTree.getNode(liveParent.getId());
            if (liveParentNode == null) {
                log.error("Cannot execute task: live parent node does not exist.");
                OK = false;
            }
        }

        if (!canCheckOut(document)){
            log.error("Cannot execute task: the document is checked out by another user.");
            OK = false;
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
