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

/* $Id: ComputeNewDocumentId.java,v 1.6 2004/03/03 12:56:30 gregor Exp $  */

package org.apache.lenya.cms.ant;

import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.UniqueDocumentId;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;


/**
 * Ant task to set the property "newdocumentid" in the project with the 
 * value of computed unique document id, needed for the destination file
 * Overriden for copy/move/rename.  
 * @param area The area in which is the sitetree.
 * @param firstdocumentid The document id of the source
 * @param secdocumentid The document id of the parent of the destination
 * or in case of rename the new name
 */
public class ComputeNewDocumentId extends PublicationTask{
	private String area;
	private String firstdocumentid;
	private String secdocumentid;

    /**
     * Creates a new instance of ComputeNewDocumentId
     */
    public ComputeNewDocumentId() {
        super();
    }

	/**
	 * @return string The area in which is the sitetree.
	 */
	public String getArea() {
		return area;
	}

	/**
	 * set the value of the area in which is the sitetree
	 * @param string The area of the sitetree.
	 */
	public void setArea(String string) {
		area = string;
	}

    /**
     * @return string The document id of the source 
     */
    protected String getFirstdocumentid() {
        return firstdocumentid;
    }

    /**
     * set the value of the document id of the source
     * @param string The document id of the source
     */
    public void setFirstdocumentid(String string) {
        firstdocumentid = string;
    }

    /**
     * @return string The document id of the parent of the destination
     * or the new name in case of rename
     */
    protected String getSecdocumentid() {
        return secdocumentid;
    }

    /**
     * @param string The document id of the parent of the destination
     * or the new name in case of rename
     */
    public void setSecdocumentid(String string) {
        secdocumentid = string;
    }

	/**
	 * Method to be overriden to compute the document id of the destination.
	 * @param firstdocumentid The document id of the source.
	 * @param secdocumentid Some string to characterize the destination (ex 
	 * document id of parent, new name).
	 * @return string. The new document id
	 */
	protected String compute(String firstdocumentid, String secdocumentid) {
		return secdocumentid;
	}

    /**
     * Compute the unique document id: append a "_version number" to the id,
     * if there is already a node in the sitetree with this id.
     * @param documentid  The document id.
     * @param area The area in which is the sitetree.
     * @return newdocumentid The unique document id. 
     */
	protected String computeUniqueId(String documentid, String area) {

		Publication publication = getPublication();

		UniqueDocumentId uniqueDocumentId = new UniqueDocumentId();
        String newdocumentid = uniqueDocumentId.computeUniqueDocumentId(publication, area, documentid);
		return newdocumentid;

    }

	/**
	 * Set the property node.newdocumentid for the project
	 * @param documentid The new document id.
	 */
	protected void setNewProperty(String documentid) {
		Target target = getOwningTarget();
		Project project = target.getProject();
		project.setProperty("node.newdocumentid", documentid);
	}

    /**
     * @see org.apache.tools.ant.Task#execute()
     **/
    public void execute() throws BuildException {
        try {
            log("document id of the source" + getFirstdocumentid());
            log("document id of the destination" + getSecdocumentid());
            log("area: " + getArea());
            String documentId = compute(getFirstdocumentid(), getSecdocumentid());
			String uniqueId = computeUniqueId(documentId, getArea());
            setNewProperty(uniqueId);
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }
}
