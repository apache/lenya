/*
 * DocumentTypeImpl.java
 *
 * Created on 8. April 2003, 18:03
 */

package org.apache.lenya.cms.publication;

import org.apache.lenya.cms.authoring.ParentChildCreatorInterface;

/**
 * A document type.
 *
 * @author <a href="mailto:andreas.hartmann@wyona.org">Andreas Hartmann</a>
 */
public class DocumentType {
    
	public static final String NAMESPACE = "http://www.lenya.org/2003/doctype";
	public static final String DEFAULT_PREFIX = "dt";
    
    /** Creates a new instance of DocumentType */
    protected DocumentType(String name) {
        assert name != null;
    	this.name = name;
    }
    
    private String name;
    
	/**
     * Returns the name of this document type.
	 * @return A string value.
	 */
	public String getName() {
		return name;
	}
    
    private ParentChildCreatorInterface creator = null;
    
    /**
     * @return
     */
    public ParentChildCreatorInterface getCreator() {
        return creator;
    }

    /**
     * @param string
     */
    protected void setCreator(ParentChildCreatorInterface creator) {
        assert creator != null;
        this.creator = creator;
    }
    
    private String workflowFile = null;
    
    /**
     * Returns if this document type has a workflow definition.
     * @return A boolean value.
     */
    public boolean hasWorkflow() {
        return workflowFile != null;
    }
    
    /**
     * @return
     */
    public String getWorkflowFileName() throws DocumentTypeBuildException {
        if (!hasWorkflow()) {
            throw new DocumentTypeBuildException("The document type '" + getName() + "' has no workflow!");
        }
        return workflowFile;
    }

    /**
     * @param string
     */
    public void setWorkflowFileName(String string) {
        assert string != null;
        workflowFile = string;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getName();
    }

}
