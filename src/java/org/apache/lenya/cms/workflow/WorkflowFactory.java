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
import org.apache.lenya.workflow.impl.WorkflowBuilder;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Element;

/**
 *
 * @author andreas
 */
public class WorkflowFactory {

    public static final String WORKFLOW_DIRECTORY =
        "config/workflow".replace('/', File.separatorChar);

    /** Creates a new instance of WorkflowFactory */
    protected WorkflowFactory() {
    }

    public static WorkflowFactory newInstance() {
        return new WorkflowFactory();
    }

    public static final String WORKFLOW_ELEMENT = "workflow";
    public static final String SRC_ATTRIBUTE = "src";
    
    /*
     * Creates a new workflow instance.
     */
    public WorkflowInstance buildInstance(Document document) throws WorkflowException {
        assert document != null;
        return new WorkflowDocument(document);
    }

    protected static Workflow buildWorkflow(Publication publication, DocumentType documentType)
        throws WorkflowException {
            
        assert publication != null;
        assert documentType != null;

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

            workflow = WorkflowBuilder.buildWorkflow(workflowFile);
        } catch (Exception e) {
            throw new WorkflowException(e);
        }
        return workflow;
    }

    /* 
     * Creates a new workflow situation.
     */
    public Situation buildSituation(User user) throws WorkflowException {
        assert user != null;
        return new CMSSituation(user);
    }
    
}
