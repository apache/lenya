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
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationFactory;
import org.apache.lenya.workflow.*;
import org.apache.lenya.workflow.Workflow;
import org.apache.lenya.workflow.impl.WorkflowBuilder;

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

    /*
     * Creates a new workflow instance.
     */
    public WorkflowInstance buildInstance(Document document) throws WorkflowException {
        assert document != null;
        return new CMSHistory(document).getInstance();
    }
    
    /**
     * Checks if a workflow is assigned to the document.
     * This is done by looking for the workflow history file.
     * @param document The document to check.
     * @return <code>true</code> if the document has a workflow, <code>false</code> otherwise.
     */
    public boolean hasWorkflow(Document document) {
        return new CMSHistory(document).isInitialized();
    }

    protected static Workflow buildWorkflow(Publication publication, String workflowFileName)
        throws WorkflowException {
            
        assert publication != null;
        assert workflowFileName != null && !"".equals(workflowFileName);

        File workflowDirectory = new File(publication.getDirectory(), WORKFLOW_DIRECTORY);
        File workflowFile = new File(workflowDirectory, workflowFileName);
        Workflow workflow = WorkflowBuilder.buildWorkflow(workflowFile);

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
    
   
    /**
     * Initializes the history of a document.
     * @param document The document object.
     * @param type The document type the document belongs to.
     * @param user The user who created the document.
     * @throws WorkflowException When something goes wrong.
     */
    public static void initHistory(Document document, String workflowId) throws WorkflowException {
        new CMSHistory(document).initialize(workflowId);
    }
    
}
