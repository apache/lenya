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

/* $Id: DocumentType.java,v 1.9 2004/03/01 16:18:16 gregor Exp $  */

package org.apache.lenya.cms.publication;

import org.apache.lenya.cms.authoring.ParentChildCreatorInterface;


/**
 * A document type.
 */
public class DocumentType {
    public static final String NAMESPACE = "http://apache.org/cocoon/lenya/doctypes/1.0";
    public static final String DEFAULT_PREFIX = "dt";

    /** Creates a new instance of DocumentType
     * 
     * @param name the name of the document type
     * 
     */
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
	 * Get the creator for this document type.
	 * 
	 * @return a <code>ParentChildCreatorInterface</code>
	 */
    public ParentChildCreatorInterface getCreator() {
        return creator;
    }

	/**
	 * Set the creator
	 * 
	 * @param creator a <code>ParentChildCreatorInterface</code>
	 */
    protected void setCreator(ParentChildCreatorInterface creator) {
        assert creator != null;
        this.creator = creator;
    }

    private String workflowFile = null;

    /**
     * Returns if this document type has a workflow definition.
     * 
     * @return A boolean value.
     */
    public boolean hasWorkflow() {
        return workflowFile != null;
    }

	/**
	 * Get the file name of the workflow file.
	 * 
	 * @return a <code>String</code>
	 * 
	 * @throws DocumentTypeBuildException if the document type has no workflow
	 */
    public String getWorkflowFileName() throws DocumentTypeBuildException {
        if (!hasWorkflow()) {
            throw new DocumentTypeBuildException("The document type '" + getName() +
                "' has no workflow!");
        }

        return workflowFile;
    }

	/**
	 * Set the file name of the workflow file.
	 * 
	 * @param string the new file name
	 */
    public void setWorkflowFileName(String string) {
        assert string != null;
        workflowFile = string;
    }

    /** (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getName();
    }
}
