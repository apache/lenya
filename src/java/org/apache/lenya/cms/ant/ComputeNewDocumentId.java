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

/* $Id$  */

package org.apache.lenya.cms.ant;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentManager;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;


/**
 * <p>
 * Ant task to set the property "newdocumentid" in the project with the 
 * value of computed unique document id, needed for the destination file
 * Overriden for copy/move/rename.
 * </p>
 * <p>Parameters:</p>
 * <ul>
 * <li>area - The area in which is the sitetree.</li>
 * <li>firstdocumentid - The document id of the source</li>
 * <li>secdocumentid - The document id of the parent of the destination
       or in case of rename the new name</li>
 * </ul>
 */
public class ComputeNewDocumentId extends PublicationTask {
    
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
		return this.area;
	}

	/**
	 * set the value of the area in which is the sitetree
	 * @param string The area of the sitetree.
	 */
	public void setArea(String string) {
		this.area = string;
	}

    /**
     * @return string The document id of the source 
     */
    protected String getFirstdocumentid() {
        return this.firstdocumentid;
    }

    /**
     * set the value of the document id of the source
     * @param string The document id of the source
     */
    public void setFirstdocumentid(String string) {
        this.firstdocumentid = string;
    }

    /**
     * @return string The document id of the parent of the destination
     * or the new name in case of rename
     */
    protected String getSecdocumentid() {
        return this.secdocumentid;
    }

    /**
     * @param string The document id of the parent of the destination
     * or the new name in case of rename
     */
    public void setSecdocumentid(String string) {
        this.secdocumentid = string;
    }

	/**
	 * Method to be overriden to compute the document id of the destination.
	 * @param _firstdocumentid The document id of the source.
	 * @param _secdocumentid Some string to characterize the destination (ex 
	 * document id of parent, new name).
	 * @return string. The new document id
	 */
	protected String compute(String _firstdocumentid, String _secdocumentid) {
		return _secdocumentid;
	}

    /**
     * Compute the unique document id: append a "_version number" to the id,
     * if there is already a node in the sitetree with this id.
     * @param documentid  The document id.
     * @param _area The area in which is the sitetree.
     * @return newdocumentid The unique document id. 
     */
	protected String computeUniqueId(String documentid, String _area) {

        DocumentManager docManager = null;
        try {
            docManager = (DocumentManager) getServiceManager().lookup(DocumentManager.ROLE);
            Document document = getIdentityMap().get(getPublication(), _area, documentid);
            document = docManager.getAvailableDocument(document);
            return document.getId();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            if (docManager != null) {
                getServiceManager().release(docManager);
            }
        }
    }

	/**
	 * Set the property node.newdocumentid for the project
	 * @param documentid The new document id.
	 */
	protected void setNewProperty(String documentid) {
		Target _target = getOwningTarget();
		Project _project = _target.getProject();
		_project.setProperty("node.newdocumentid", documentid);
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
