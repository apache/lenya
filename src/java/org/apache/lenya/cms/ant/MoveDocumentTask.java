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

/* $Id: MoveDocumentTask.java,v 1.6 2004/03/03 12:56:30 gregor Exp $  */

package org.apache.lenya.cms.ant;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.Label;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.SiteTree;
import org.apache.lenya.cms.publication.SiteTreeNode;
import org.apache.lenya.cms.publication.SiteTreeNodeVisitor;
import org.apache.lenya.cms.workflow.WorkflowFactory;
import org.apache.lenya.workflow.WorkflowException;
import org.apache.tools.ant.BuildException;

/**
 * Ant task, which implements the SiteTreeNodeVisitor for the operation move a document. (Visitor
 * pattern)
 */
public class MoveDocumentTask extends PublicationTask implements SiteTreeNodeVisitor {

	private String firstarea;
	private String firstdocumentid;
	private String secarea;
	private String secdocumentid;

	/**
	 *  
	 */
	public MoveDocumentTask() {
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
	 * @return String The area of the destination.
	 */
	public String getSecarea() {
		return secarea;
	}

	/**
	 * @return String The document-id corresponding to the destination.
	 */
	public String getSecdocumentid() {
		return secdocumentid;
	}

	/**
	 * @param string
	 *            The area of the source.
	 */
	public void setFirstarea(String string) {
		firstarea = string;
	}

	/**
	 * @param string
	 *            The document-id corresponding to the source.
	 */
	public void setFirstdocumentid(String string) {
		firstdocumentid = string;
	}

	/**
	 * @param string
	 *            The area of the destination.
	 */
	public void setSecarea(String string) {
		secarea = string;
	}

	/**
	 * @param string
	 *            The document-id corresponding to the destination.
	 */
	public void setSecdocumentid(String string) {
		secdocumentid = string;
	}

	/**
	 * move the workflow files
	 * 
	 * @see org.apache.lenya.cms.publication.SiteTreeNodeVisitor#visitSiteTreeNode(org.apache.lenya.cms.publication.SiteTreeNode)
	 */
	public void visitSiteTreeNode(SiteTreeNode node) {
		Publication publication = getPublication();

		Label[] labels = node.getLabels();
		for (int i = 0; i < labels.length; i++) {
			String language = labels[i].getLanguage();

			String srcDocumentid = node.getAbsoluteId();
			String destDocumentid = srcDocumentid.replaceFirst(firstdocumentid, secdocumentid);

			// TODO: content(fix the build file)
			// TODO: resources (fix the build file)
			// TODO: rcml (fix the build file)
			// TODO: rcbak (fix the build file)

			//move workflow
			DocumentBuilder builder = publication.getDocumentBuilder();
			String url = builder.buildCanonicalUrl(publication, firstarea, srcDocumentid, language);
			String newurl =
				builder.buildCanonicalUrl(publication, secarea, destDocumentid, language);

			Document document;
			Document newDocument;
			WorkflowFactory factory = WorkflowFactory.newInstance();

			log("move workflow history");
			try {
				document = builder.buildDocument(publication, url);
				newDocument = builder.buildDocument(publication, newurl);
			} catch (DocumentBuildException e) {
				throw new BuildException(e);
			}
			try {
				if (factory.hasWorkflow(document)) {
					WorkflowFactory.moveHistory(document, newDocument);
				}
			} catch (WorkflowException e) {
				throw new BuildException(e);
			}

		}
	}

	/**
	 * @see org.apache.tools.ant.Task#execute()
	 */
	public void execute() throws BuildException {
		try {
			log("document id for the source" + this.getFirstdocumentid());
			log("area for the source" + this.getFirstarea());
			log("document id for the destination" + this.getSecdocumentid());
			log("area for the destination" + this.getSecarea());

			Publication publication = getPublication();
			SiteTree tree = publication.getSiteTree(getFirstarea());
			SiteTreeNode node = tree.getNode(getFirstdocumentid());

			node.acceptSubtree(this);
		} catch (Exception e) {
			throw new BuildException(e);
		}
	}

}
