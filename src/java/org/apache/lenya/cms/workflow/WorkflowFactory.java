/*
 * WorkflowFactory.java
 *
 * Created on 8. April 2003, 18:08
 */

package org.apache.lenya.cms.workflow;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.lenya.cms.ac.Role;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.Publication;
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

    /**
     * Returns an instance of the workflow factory.
     * @return A workflow factory.
     */
    public static WorkflowFactory newInstance() {
        return new WorkflowFactory();
    }

    /**
     * Creates a new workflow instance.
     * @param document The document to create the instance for.
     * @return A workflow instance.
     * @throws WorkflowException when something went wrong.
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

    /**
     * Builds a workflow for a given publication.
     * @param publication The publication.
     * @param workflowFileName The workflow definition filename.
     * @return A workflow object.
     * @throws WorkflowException when something went wrong.
     */
    protected static Workflow buildWorkflow(Publication publication, String workflowFileName)
        throws WorkflowException {
            
        assert publication != null;
        assert workflowFileName != null && !"".equals(workflowFileName);

        File workflowDirectory = new File(publication.getDirectory(), WORKFLOW_DIRECTORY);
        File workflowFile = new File(workflowDirectory, workflowFileName);
        Workflow workflow = WorkflowBuilder.buildWorkflow(workflowFile);

        return workflow;
    }

    /**
     * Creates a new workflow situation.
     * @param roles The roles of the situation.
     * @return A situation.
     * @throws WorkflowException when something went wrong.
     */
    public Situation buildSituation(Role roles[]) throws WorkflowException {
        return new CMSSituation(roles);
    }
    
    /**
     * Creates a situation for a Cocoon object model.
     * @param objectModel The object model.
     * @return A workflow situation.
     * @throws WorkflowException when something went wrong.
     */
    public Situation buildSituation(Map objectModel) throws WorkflowException {
        Request request = ObjectModelHelper.getRequest(objectModel);
        Session session = request.getSession(true);

        List roleList = (List) request.getAttribute(Role.class.getName());
        if (roleList == null) {
            throw new WorkflowException("Request does not contain roles!");
        }
        Role[] roles = (Role[]) roleList.toArray(new Role[roleList.size()]);
        return buildSituation(roles);
    }
    
   
    /**
     * Initializes the history of a document.
     * @param document The document object.
     * @param workflowId The ID of the workflow.
     * @throws WorkflowException When something goes wrong.
     */
    public static void initHistory(Document document, String workflowId) throws WorkflowException {
        new CMSHistory(document).initialize(workflowId);
    }
    
}
