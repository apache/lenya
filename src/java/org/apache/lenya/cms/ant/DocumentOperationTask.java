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

/* $Id: DocumentOperationTask.java,v 1.3 2004/03/03 12:56:30 gregor Exp $  */

package org.apache.lenya.cms.ant;

import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.SiteTree;
import org.apache.lenya.cms.publication.SiteTreeNode;
import org.apache.lenya.cms.publication.SiteTreeNodeVisitor;
import org.apache.tools.ant.BuildException;

/**
 * Abstract base class for Ant tasks, which implements the SiteTreeNodeVisitor 
 * to call an operation for a document. 
 * (Visitor pattern)
 */
public abstract class DocumentOperationTask extends PublicationTask implements SiteTreeNodeVisitor {

		private String firstarea;
		private String firstdocumentid;

	/**
	 * 
	 */
	public DocumentOperationTask() {
		super();
	}


	/**
	 * @return String The area of the source.
	 */
	public String getFirstarea() {
		return firstarea;
	}

	/**
	 * @return String The document-id corresponding to the source.
	 */
	public String getFirstdocumentid() {
		return firstdocumentid;
	}

	/**
	 * @param string The area of the source.
	 */
	public void setFirstarea(String string) {
		firstarea = string;
	}

	/**
	 * @param string The document-id corresponding to the source.
	 */
	public void setFirstdocumentid(String string) {
		firstdocumentid = string;
	}

	/**
	 * To be overriden.
	 * @see org.apache.lenya.cms.publication.SiteTreeNodeVisitor#visitSiteTreeNode(org.apache.lenya.cms.publication.SiteTreeNode)
	 */
	public abstract void visitSiteTreeNode(SiteTreeNode node); 

	/** 
	 * @see org.apache.tools.ant.Task#execute()
	 **/
	public void execute() throws BuildException {
		try {
			log("document-id for the source" + this.getFirstdocumentid());
			log("area for the source" + this.getFirstarea());

			Publication publication= getPublication();
			SiteTree tree = publication.getSiteTree(getFirstarea());
			SiteTreeNode node = tree.getNode(getFirstdocumentid());

			node.acceptSubtree(this);
		} catch (Exception e) {
			throw new BuildException(e);
		}
}


}
