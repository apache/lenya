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

/* $Id: CopyContentTask.java,v 1.6 2004/03/03 12:56:30 gregor Exp $  */

package org.apache.lenya.cms.ant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.Label;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.SiteTreeNode;
import org.apache.lenya.util.FileUtil;
import org.apache.tools.ant.BuildException;

/**
 * Ant task to copy the contents (xml files) belonging to a subtree defined by a given document id and a given area.
 * Visitor of the defined subtree (visitor pattern)
 */
public class CopyContentTask extends TwoDocumentsOperationTask {

	/**
	 * 
	 */
	public CopyContentTask() {
		super();
	}

	/** (non-Javadoc)
	 * @see org.apache.lenya.cms.publication.SiteTreeNodeVisitor#visitSiteTreeNode(org.apache.lenya.cms.publication.SiteTreeNode)
	 */
	public void visitSiteTreeNode(SiteTreeNode node) {
		Publication publication = getPublication();
		DocumentBuilder builder = publication.getDocumentBuilder();

		String srcDocumentid = node.getAbsoluteId();
		String destDocumentid =
			srcDocumentid.replaceFirst(
				getFirstdocumentid(),
				getSecdocumentid());

		Label[] labels = node.getLabels();
		for (int i = 0; i < labels.length; i++) {
			String language = labels[i].getLanguage();
			String srcUrl =
				builder.buildCanonicalUrl(
					publication,
					getFirstarea(),
					srcDocumentid,
					language);
			Document srcDoc;
			try {
				srcDoc = builder.buildDocument(publication, srcUrl);
			} catch (DocumentBuildException e) {
				throw new BuildException(e);
			}
			File srcFile = srcDoc.getFile();
			if (!srcFile.exists()) {
				log("There are no file " + srcFile.getAbsolutePath());
				return;
			}
			String destUrl =
				builder.buildCanonicalUrl(
					publication,
					getSecarea(),
					destDocumentid,
					language);
			Document destDoc;
			try {
				destDoc = builder.buildDocument(publication, destUrl);
			} catch (DocumentBuildException e) {
				throw new BuildException(e);
			}
			File destFile = destDoc.getFile();

			log(
				"copy file "
					+ srcFile.getAbsolutePath()
					+ "to file "
					+ destFile.getAbsolutePath());
			try {
				FileUtil.copy(
					srcFile.getAbsolutePath(),
					destFile.getAbsolutePath());
			} catch (FileNotFoundException e) {
				throw new BuildException(e);
			} catch (IOException e) {
				throw new BuildException(e);
			}
		}

	}
}
