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

/* $Id: MoveWorkflowTask.java,v 1.5 2004/03/03 12:56:30 gregor Exp $  */

package org.apache.lenya.cms.ant;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.Label;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.SiteTreeNode;
import org.apache.lenya.cms.workflow.WorkflowFactory;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.tools.ant.BuildException;

/**
 * Ant task, which implements the SiteTreeNodeVisitor for the operation move the workflow
 * history files.
 * (Visitor pattern)
 */
public class MoveWorkflowTask extends TwoDocumentsOperationTask {

	/**
	 * 
	 */
	public MoveWorkflowTask() {
		super();
	}

	/** (non-Javadoc)
	 * @see org.apache.lenya.cms.publication.SiteTreeNodeVisitor#visitSiteTreeNode(org.apache.lenya.cms.publication.SiteTreeNode)
	 */
	public void visitSiteTreeNode(SiteTreeNode node) {
		Publication publication = getPublication();
		DocumentBuilder builder = publication.getDocumentBuilder();

		Label[] labels = node.getLabels(); 
		for (int i=0 ; i<labels.length; i ++){
			String language = labels[i].getLanguage();

			String srcDocumentid = node.getAbsoluteId();
			String destDocumentid = srcDocumentid.replaceFirst(this.getFirstdocumentid(),this.getSecdocumentid());

			String srcUrl = builder.buildCanonicalUrl(publication, getFirstarea(), srcDocumentid, language);
			String destUrl = builder.buildCanonicalUrl(publication, getSecarea(), destDocumentid, language);

			log("url for the source : "+srcUrl);
			log("url for the destination : "+destUrl);

			Document srcDoc;
			Document destDoc;
			WorkflowFactory factory = WorkflowFactory.newInstance();
			
			log("init workflow history");
			try {
				srcDoc = builder.buildDocument(publication, srcUrl);
				destDoc = builder.buildDocument(publication, destUrl);
			} catch (DocumentBuildException e) {
				throw new BuildException(e);
			}

			log("move workflow history of "+srcDoc.getFile().getAbsolutePath()+" to " + destDoc.getFile().getAbsolutePath());
			try {
				if (factory.hasWorkflow(srcDoc)) {
					log("has workflow");
					WorkflowFactory.moveHistory(srcDoc, destDoc);
					log("workflow moved");
				}
			} catch (WorkflowException e) {
				throw new BuildException(e);
			}
		}
	}

}
