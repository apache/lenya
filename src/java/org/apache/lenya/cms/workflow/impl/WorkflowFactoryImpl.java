/*
 * WorkflowFactory.java
 *
 * Created on 8. April 2003, 18:08
 */

package org.apache.lenya.cms.workflow.impl;

import java.io.File;

import org.apache.lenya.cms.ac.User;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentType;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.workflow.*;
import org.apache.lenya.cms.workflow.Situation;
import org.apache.lenya.cms.workflow.Workflow;
import org.apache.lenya.cms.workflow.WorkflowFactory;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Element;

/**
 *
 * @author andreas
 */
public class WorkflowFactoryImpl extends WorkflowFactory {

    public static final String WORKFLOW_DIRECTORY =
        "config/workflow".replace('/', File.separatorChar);

    public static final String DOCTYPE_DIRECTORY =
        "config/doctypes".replace('/', File.separatorChar);

    /** Creates a new instance of WorkflowFactory */
    public WorkflowFactoryImpl(
        Publication publication,
        Document document,
        User user) {

        assert publication != null;
        this.publication = publication;

        assert document != null;
        this.document = document;

        assert user != null;
        this.user = user;
    }

    private Publication publication;

    private Document document;

    private User user;

    public static WorkflowFactoryImpl newInstance(
        Publication publication,
        Document document,
        User user) {
        return new WorkflowFactoryImpl(publication, document, user);
    }

    /* (non-Javadoc)
     * @see org.apache.lenya.cms.workflow.WorkflowFactory#createSituation()
     */
    public Situation buildSituation() throws WorkflowBuildException {
        return new CMSSituation(getUser());
    }

    private Workflow workflow;

    public static final String WORKFLOW_ELEMENT = "workflow";
    public static final String SRC_ATTRIBUTE = "src";

    /* (non-Javadoc)
     * @see org.apache.lenya.cms.workflow.WorkflowFactory#createWorkflow()
     */
    public Workflow buildWorkflow() throws WorkflowBuildException {

        if (this.workflow != null) {
            return this.workflow;
        }

        DocumentType documentType = getDocument().getType();
        String name = documentType.getName();

        File doctypesDirectory =
            new File(publication.getDirectory(), DOCTYPE_DIRECTORY);
        File doctypeFile = new File(doctypesDirectory, name + ".xml");

        Workflow workflow;

        try {
            org.w3c.dom.Document xmlDocument =
                DocumentHelper.readDocument(doctypeFile);

            NamespaceHelper helper =
                new NamespaceHelper(
                    DocumentType.NAMESPACE,
                    DocumentType.DEFAULT_PREFIX,
                    xmlDocument);
            Element root = xmlDocument.getDocumentElement();

            Element workflowElement =
                (Element) root
                    .getElementsByTagNameNS(
                        DocumentType.NAMESPACE,
                        WORKFLOW_ELEMENT)
                    .item(0);

            String source = workflowElement.getAttribute(SRC_ATTRIBUTE);
            assert source != null;

            File publicationDirectory = publication.getDirectory();
            File workflowDirectory = new File(publicationDirectory, WORKFLOW_DIRECTORY);
            File workflowFile = new File(workflowDirectory, source);

            WorkflowBuilder builder = new WorkflowBuilder();
            workflow = builder.buildWorkflow(workflowFile);
        } catch (Exception e) {
            throw new WorkflowBuildException(e);
        }

        this.workflow = workflow;
        return workflow;
    }

    /**
     * @return
     */
    public Document getDocument() {
        return document;
    }

    /**
     * @return
     */
    public Publication getPublication() {
        return publication;
    }

    /**
     * @return
     */
    public User getUser() {
        return user;
    }

    /* (non-Javadoc)
     * @see org.apache.lenya.cms.workflow.WorkflowFactory#buildInstance()
     */
    public WorkflowInstance buildInstance() throws WorkflowBuildException {
        // TODO build instance depending on document
        WorkflowInstanceImpl instance =
            new WorkflowInstanceImpl(buildWorkflow());
        return instance;
    }

}
