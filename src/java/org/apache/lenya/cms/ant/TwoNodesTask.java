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

/* $Id: TwoNodesTask.java,v 1.7 2004/03/03 12:56:30 gregor Exp $  */

package org.apache.lenya.cms.ant;

import org.apache.lenya.cms.publication.SiteTreeException;
import org.apache.tools.ant.BuildException;


/**
 * Abstract base class for Ant tasks, which manipulates two nodes.
 * The first node corresponds to the document with id firstdocumentid
 * and the area firstarea.
 * The 2nd node corresponds to the document with id secdocumentid 
 * and the area secarea.
 */
public abstract class TwoNodesTask extends PublicationTask {
	private String firstarea;
	private String firstdocumentid;
	private String secarea;
	private String secdocumentid;

    /**
     * Creates a new instance of TwoNodesTask
     */
    public TwoNodesTask() {
        super();
    }

	/**
	 * @return String The area of the document of the first node.
	 */
	public String getFirstarea() {
		return firstarea;
	}

	/**
	 * @return String The document-id corresponding to the first node.
	 */
	public String getFirstdocumentid() {
		return firstdocumentid;
	}

	/**
	 * @return String The area of the document of the second node.
	 */
	public String getSecarea() {
		return secarea;
	}

	/**
	 * @return String The document-id corresponding to the second node.
	 */
	public String getSecdocumentid() {
		return secdocumentid;
	}

	/**
	 * @param string The area of the document of the first node.
	 */
	public void setFirstarea(String string) {
		firstarea = string;
	}

	/**
	 * @param string The document-id corresponding to the first node.
	 */
	public void setFirstdocumentid(String string) {
		firstdocumentid = string;
	}

	/**
	 * @param string The area of the document of the second node.
	 */
	public void setSecarea(String string) {
		secarea = string;
	}

	/**
	 * @param string The document-id corresponding to the second node.
	 */
	public void setSecdocumentid(String string) {
		secdocumentid = string;
	}

    /**
     * To be overriden.
     * Manipulation of two nodes . 
     * @param firstdocumentid : id of the first document
     * @param secdocumentid : id of the second document
     * @param firstarea : area of the tree of the first node
     * @param secarea : area of the tree of the 2nd node
     * 
     * @throws SiteTreeException if an error occurs
     */
    public abstract void manipulateTree(String firstdocumentid, String secdocumentid,
        String firstarea, String secarea)
        throws SiteTreeException;

    /** (non-Javadoc)
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException {
        try {
            log("document-id corresponding to the first node: " + this.getFirstdocumentid());
            log("document-id corresponding to the second node: " + this.getSecdocumentid());
            log("area corresponding to the first node: " + this.getFirstarea());
			log("area corresponding to the second node: " + this.getSecarea());
            manipulateTree(getFirstdocumentid(), getSecdocumentid(), getFirstarea(), getSecarea());
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }
}
