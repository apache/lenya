/*
 * WorkflowFactory.java
 *
 * Created on 8. April 2003, 18:08
 */

package org.apache.lenya.cms.workflow;

import java.io.File;

import org.apache.lenya.cms.ac.User;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentType;
import org.apache.lenya.cms.publication.DocumentTypeBuilder;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.workflow.*;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.WorkflowFactory;
import org.apache.lenya.workflow.impl.WorkflowBuilder;
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

    /** Creates a new instance of WorkflowFactory */
    protected WorkflowFactoryImpl(Document document, User user) {

        assert document != null;
        this.document = document;
        
        assert user != null;
        situation = new CMSSituation(user);
    }

    public static WorkflowFactoryImpl newInstance(Document document, User user) {
        return new WorkflowFactoryImpl(document, user);
    }

    public static final String WORKFLOW_ELEMENT = "workflow";
    public static final String SRC_ATTRIBUTE = "src";
    /* (non-Javadoc)
     * @see org.apache.lenya.cms.workflow.WorkflowFactory#createWorkflow()
     */

    private Document document;
    private User user;

    private WorkflowDocument workflowDocument;
    private Situation situation;

    public WorkflowInstance buildInstance() throws WorkflowBuildException {
        if (workflowDocument == null) {
            workflowDocument = new WorkflowDocument(document);
        }
        return workflowDocument;
    }

    protected static Workflow buildWorkflow(Publication publication, DocumentType documentType)
        throws WorkflowBuildException {

        File doctypesDirectory =
            new File(publication.getDirectory(), DocumentTypeBuilder.DOCTYPE_DIRECTORY);
        File doctypeFile = new File(doctypesDirectory, documentType.getName() + ".xml");

        Workflow workflow;

        try {
            org.w3c.dom.Document xmlDocument = DocumentHelper.readDocument(doctypeFile);

            NamespaceHelper helper =
                new NamespaceHelper(
                    DocumentType.NAMESPACE,
                    DocumentType.DEFAULT_PREFIX,
                    xmlDocument);
            Element root = xmlDocument.getDocumentElement();

            Element workflowElement =
                (Element) root.getElementsByTagNameNS(
                    DocumentType.NAMESPACE,
                    WORKFLOW_ELEMENT).item(
                    0);

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
        return workflow;
    }

    /* (non-Javadoc)
     * @see org.apache.lenya.cms.workflow.WorkflowFactory#buildSituation()
     */
    public Situation buildSituation() throws WorkflowBuildException {
        return situation;
    }

}
