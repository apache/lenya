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

/* $Id: DeleteRCTask.java,v 1.5 2004/03/03 12:56:30 gregor Exp $  */

package org.apache.lenya.cms.ant;

import java.io.File;
import java.io.IOException;

import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.SiteTree;
import org.apache.lenya.cms.publication.SiteTreeNode;
import org.apache.tools.ant.BuildException;

/**
 * Ant task to delete the rcml- and backup files of documents corresponding to a defined subtree
 * (Visitor pattern) Visitor of the subtree. The subtree is reverse visited.
 */
public class DeleteRCTask extends TwoDocumentsOperationTask {
	private String rcmldir = "";
	private String rcbakdir = "";
	private String srcareadir = "";

	/**
	 * 
	 */
	public DeleteRCTask() {
		super();
	}

	/** (non-Javadoc)
	 * @see org.apache.lenya.cms.publication.SiteTreeNodeVisitor#visitSiteTreeNode(org.apache.lenya.cms.publication.SiteTreeNode)
	 */
	public void visitSiteTreeNode(SiteTreeNode node) {
		String publicationPath;
		String rcmlDirectory;
		String rcbakDirectory;
		try {
			publicationPath = this.getPublicationDirectory().getCanonicalPath();
			rcmlDirectory =
				new File(publicationPath, this.getRcmldir()).getCanonicalPath();
			rcbakDirectory =
				new File(publicationPath, this.getRcbakdir())
					.getCanonicalPath();
		} catch (IOException e) {
			throw new BuildException(e);
		}

		String destDocumentid = node.getAbsoluteId();
		String srcDocumentid =
			destDocumentid.replaceFirst(
				getSecdocumentid(),
				getFirstdocumentid());

		File srcRcmlDir =
			new File(
				rcmlDirectory,
				this.getSrcareadir() + File.separator + srcDocumentid);

		if (srcRcmlDir.exists()) {
			try {
				FileUtil.forceDelete(srcRcmlDir);
			} catch (IOException e) {
				//FIXME: catch Exception because of window's delete problem 
				log("exception " + e);
			}
		}

		File srcRcbakDir =
			new File(
				rcbakDirectory,
				this.getSrcareadir() + File.separator + srcDocumentid);

		if (srcRcbakDir.exists()) {
			try {
				FileUtil.forceDelete(srcRcbakDir);
			} catch (IOException e) {
				//FIXME: catch Exception because of window's delete problem 
				log("exception " + e);
			}
			log("delete rcbak directory " + srcRcbakDir.getAbsolutePath());
		}

	}

	/** 
	 * @see org.apache.tools.ant.Task#execute()
	 **/
	public void execute() throws BuildException {
		try {
			log("document-id for the source :" + this.getFirstdocumentid());
			log("document-id for the destination :" + this.getSecdocumentid());
			log("area for the destination :" + this.getSecarea());
			log("rcml dir" + this.getRcmldir());
			log("rcbak dir" + this.getRcbakdir());
			log("src area dir" + this.getSrcareadir());

			//visit the destination tree
			Publication publication = getPublication();
			SiteTree tree = publication.getSiteTree(this.getSecarea());
			SiteTreeNode node = tree.getNode(this.getSecdocumentid());
			node.acceptReverseSubtree(this);

		} catch (Exception e) {
			throw new BuildException(e);
		}
	}
	/**
	 * @return The backup directory.
	 */
	public String getRcbakdir() {
		return rcbakdir;
	}

	/**
	 * @return The rcml directory.
	 */
	public String getRcmldir() {
		return rcmldir;
	}

	/**
	 * @return The path of the area from the publication.
	 */
	public String getSrcareadir() {
		return srcareadir;
	}

	/**
	 * @param string The backup directory.
	 */
	public void setRcbakdir(String string) {
		rcbakdir = string;
	}

	/**
	 * @param string The rcml directory.
	 */
	public void setRcmldir(String string) {
		rcmldir = string;
	}

	/**
	 * @param string The path of the area from the publication.
	 */
	public void setSrcareadir(String string) {
		srcareadir = string;
	}

}
