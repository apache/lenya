/*
 * $Id: MoveDocumentTask.java,v 1.4 2004/01/08 09:35:34 egli Exp $ <License>
 * 
 * ============================================================================ The Apache Software
 * License, Version 1.1
 * ============================================================================
 * 
 * Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modifica- tion, are permitted
 * provided that the following conditions are met: 1. Redistributions of source code must retain
 * the above copyright notice, this list of conditions and the following disclaimer. 2.
 * Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution. 3. The end-user documentation included with the redistribution, if any,
 * must include the following acknowledgment: "This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/)." Alternately, this acknowledgment may
 * appear in the software itself, if and wherever such third-party acknowledgments normally appear. 4.
 * The names "Apache Lenya" and "Apache Software Foundation" must not be used to endorse or promote
 * products derived from this software without prior written permission. For written permission,
 * please contact apache@apache.org. 5. Products derived from this software may not be called
 * "Apache", nor may "Apache" appear in their name, without prior written permission of the Apache
 * Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLU- DING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * This software consists of voluntary contributions made by many individuals on behalf of the
 * Apache Software Foundation and was originally created by Michael Wechner <michi@apache.org> .
 * For more information on the Apache Soft- ware Foundation, please see <http://www.apache.org/> .
 * 
 * Lenya includes software developed by the Apache Software Foundation, W3C, DOM4J Project,
 * BitfluxEditor, Xopus, and WebSHPINX. </License>
 */
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
 * 
 * @author edith
 *  
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

			String parentid = node.getAbsoluteParentId();
			String srcDocumentid = parentid + "/" + node.getId();
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
