/*
 * WorkflowFactory.java
 *
 * Created on 8. April 2003, 18:08
 */

package org.apache.lenya.cms.workflow;

import java.io.File;
import java.util.Map;

import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.lenya.cms.ac.AccessControlException;
import org.apache.lenya.cms.ac.Identity;
import org.apache.lenya.cms.ac.User;
import org.apache.lenya.cms.ac.UserManager;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentType;
import org.apache.lenya.cms.publication.DocumentTypeBuilder;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;
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
    
    /**
     * Checks if a workflow is assigned to the document.
     * This is done by looking for the workflow history file.
     * @param document The document to check.
     * @return <code>true</code> if the document has a workflow, <code>false</code> otherwise.
     */
    public boolean hasWorkflow(Document document) {
        File historyFile = WorkflowDocument.getHistoryFile(document);
        return historyFile.exists();
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
    
    public Situation buildSituation(Map objectModel) throws WorkflowException {
        Request request = ObjectModelHelper.getRequest(objectModel);
        Session session = request.getSession(true);

        if (session == null) {
            throw new WorkflowException("No session object available!");
        }
        
        Identity identity = (Identity) session.getAttribute("org.apache.lenya.cms.ac.Identity");
        
        if (identity == null) {
            throw new WorkflowException("No session object available!");
        }
        
        String username = identity.getUsername();
        
        Publication publication = PublicationFactory.getPublication(objectModel);
        
        User user;
        try {
            user = UserManager.instance(publication).getUser(username);
        } catch (AccessControlException e) {
            throw new WorkflowException(e);
        }
        
        return buildSituation(user);
    }
    
}
