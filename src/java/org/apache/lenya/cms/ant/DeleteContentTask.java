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

/* $Id: DeleteContentTask.java,v 1.8 2004/03/03 12:56:30 gregor Exp $  */

package org.apache.lenya.cms.ant;

import java.io.File;
import java.io.IOException;

import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.Label;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.SiteTree;
import org.apache.lenya.cms.publication.SiteTreeNode;
import org.apache.tools.ant.BuildException;

/**
 * Ant task to delete the contents (xml files) of documents corresponding to a defined subtree
 * Visitor of the defined subtree (visitor pattern). The subtree is reverse visited.
 */
public class DeleteContentTask extends TwoDocumentsOperationTask {

	/**
	 * 
	 */
	public DeleteContentTask() {
		super();
	}

	/** (non-Javadoc)
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
			Document doc;
			try {
				doc = builder.buildDocument(publication, url);
			} catch (DocumentBuildException e) {
				throw new BuildException(e);
			}
			File srcFile = doc.getFile();
			if (!srcFile.exists()) {
				log("There are no file " + srcFile.getAbsolutePath());
				return;
			}
			File directory = srcFile.getParentFile();
			try {
				FileUtil.forceDelete(srcFile);
			} catch (IOException e) {
				//FIXME: catch Exception because of window's delete problem 
				log("exception " + e);
			}
			if (directory.exists()
				&& directory.isDirectory()
				&& directory.listFiles().length == 0) {
				try {
					FileUtil.forceDelete(directory);
				} catch (IOException e) {
					//FIXME: catch Exception because of window's delete problem 
					log("exception " + e);
				}
			}
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
