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

/* $Id: DeleteResourcesTask.java,v 1.3 2004/03/03 12:56:30 gregor Exp $  */

package org.apache.lenya.cms.ant;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.Label;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.ResourcesManager;
import org.apache.lenya.cms.publication.SiteTree;
import org.apache.lenya.cms.publication.SiteTreeNode;
import org.apache.tools.ant.BuildException;

/**
 * Ant task to delete the resources of documents corresponding to a defined subtree
 * (Visitor pattern) Visitor of the subtree. The subtree is reverse visited.
 */
public class DeleteResourcesTask extends TwoDocumentsOperationTask {

	/**
	 * 
	 */
	public DeleteResourcesTask() {
		super();
	}

	/**
	 * Delete the resources of the documents corresponding to this node
	 *  
	 * @see org.apache.lenya.cms.publication.SiteTreeNodeVisitor#visitSiteTreeNode(org.apache.lenya.cms.publication.SiteTreeNode)
	 */
	public void visitSiteTreeNode(SiteTreeNode node) {
		Publication publication = getPublication();
		DocumentBuilder builder = publication.getDocumentBuilder();

		String destDocumentid = node.getAbsoluteId();
		String srcDocumentid =
			destDocumentid.replaceFirst(
				getSecdocumentid(),
				getFirstdocumentid());

		Label[] labels = node.getLabels();
		for (int i = 0; i < labels.length; i++) {
			String language = labels[i].getLanguage();
			String url =
				builder.buildCanonicalUrl(
					publication,
					getFirstarea(),
					srcDocumentid,
					language);
			Document srcDoc;
			try {
				srcDoc = builder.buildDocument(publication, url);
			} catch (DocumentBuildException e) {
				throw new BuildException(e);
			}

			ResourcesManager resourcesMgr = new ResourcesManager(srcDoc);
			resourcesMgr.deleteResources();
		}
	}

	/** 
	 * @see org.apache.tools.ant.Task#execute()
	 **/
	public void execute() throws BuildException {
		try {
			log("document-id for the source :" + this.getFirstdocumentid());
			log("area for the source :" + this.getFirstarea());
			log("document-id for the destination :" + this.getSecdocumentid());
			log("area for the destination :" + this.getSecarea());

			Publication publication = getPublication();
			SiteTree tree = publication.getSiteTree(this.getSecarea());
			SiteTreeNode node = tree.getNode(this.getSecdocumentid());
			node.acceptReverseSubtree(this);
		} catch (Exception e) {
			throw new BuildException(e);
		}
	}

}
