/*
 * DocumentTypeImpl.java
 *
 * Created on 8. April 2003, 18:03
 */

package org.lenya.cms.publication;

import java.io.File;
import org.lenya.cms.workflow.Workflow;
import org.lenya.cms.workflow.impl.WorkflowBuilder;
import org.lenya.cms.workflow.impl.WorkflowBuilderFactory;

/**
 *
 * @author  andreas
 */
public class DocumentType {
    
    /** Creates a new instance of DocumentTypeImpl */
    public DocumentType(Workflow workflow) {
        assert workflow != null;
        this.workflow = workflow;
    }
    
    private Workflow workflow;
    
    /** Returns the workflow of this DocumentType.
     * @return The workflow of this DocumentType.
     *
     */
    public Workflow getWorkflow() {
        return workflow;
    }
    
}
