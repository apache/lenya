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

/* $Id: CopyPoliciesTask.java,v 1.4 2004/03/03 12:56:30 gregor Exp $  */

package org.apache.lenya.cms.ant;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.SiteTreeNode;
import org.apache.tools.ant.BuildException;

/**
 * Ant task to copy the policies of a document
 */
public class CopyPoliciesTask extends TwoDocumentsOperationTask {
	private String policiesDir;

	/**
	 * 
	 */
	public CopyPoliciesTask() {
		super();
	}

	/**
	 * @return string The policies directory.
	 */
	public String getPoliciesDir() {
		return policiesDir;
	}
	/**
	 * @param string The policies directory
	 */
	public void setPoliciesDir(String string) {
		policiesDir = string;
	}

	/** 
	 * Get all files in a given directory, that are not directories.
	 * If the given directory doesn't exist, return null.
	 * @param directory The directory
	 * @return List of files
	 */
	public File[] getFiles(File directory) {
		FileFilter filter = new FileFilter() {

			public boolean accept(File file) {
				return file.isFile();
			}
		};
		if (directory.exists() && directory.isDirectory()) {
			return directory.listFiles(filter);
		}
		return null;
	}

	/**
	 * Copies the policies file 
	 * @param srcDir The source directory of the policies files.
	 * @param destDir The destination directory of the policies files.
	 */
	public void copyPolicies(File srcDir, File destDir) {
		File[] authoringPolicies = this.getFiles(srcDir);
		if (authoringPolicies == null) {
			return;
		}
		for (int i = 0; i < authoringPolicies.length; i++) {
			String srcPath;
			try {
				srcPath = authoringPolicies[i].getCanonicalPath();
                String policyPath = srcPath.substring(srcDir.getCanonicalPath().length());
				File destFile = new File(destDir, policyPath);
				FileUtil.copyFile(authoringPolicies[i], destFile);
			} catch (IOException e) {
				throw new BuildException(e);
			}
		}
	}

	/** (non-Javadoc)
	 * @see org.apache.lenya.cms.ant.DocumentOperationTask#visitSiteTreeNode(org.apache.lenya.cms.publication.SiteTreeNode)
	 */
	public void visitSiteTreeNode(SiteTreeNode node) {
		String srcArea = this.getFirstarea();
		String destArea = this.getSecarea();

		String srcDocumentid = node.getAbsoluteId();
		String destDocumentid =
			srcDocumentid.replaceFirst(
				getFirstdocumentid(),
				getSecdocumentid());

		if (srcArea.equals(Publication.AUTHORING_AREA)) {
			if (destArea.equals(Publication.AUTHORING_AREA)) {
				File srcDir =
					new File(
						policiesDir,
						this.getFirstarea() + File.separator + srcDocumentid);
				File destDir =
					new File(
						policiesDir,
						this.getSecarea() + File.separator + destDocumentid);
				copyPolicies(srcDir, destDir);

				File srcLiveDir =
					new File(
						policiesDir,
						Publication.LIVE_AREA + File.separator + srcDocumentid);
				File destLiveDir =
					new File(
						policiesDir,
						Publication.LIVE_AREA
							+ File.separator
							+ destDocumentid);

				copyPolicies(srcLiveDir, destLiveDir);

			} else if (
				destArea.equals(Publication.ARCHIVE_AREA)
					| destArea.equals(Publication.TRASH_AREA)) {
				File srcDir =
					new File(
						policiesDir,
						this.getFirstarea() + File.separator + srcDocumentid);
				File destDir =
					new File(
						policiesDir,
						this.getSecarea()
							+ File.separator
							+ this.getFirstarea()
							+ File.separator
							+ destDocumentid);
				copyPolicies(srcDir, destDir);

				File srcLiveDir =
					new File(
						policiesDir,
						Publication.LIVE_AREA + File.separator + srcDocumentid);
				File destLiveDir =
					new File(
						policiesDir,
						this.getSecarea()
							+ File.separator
							+ Publication.LIVE_AREA
							+ File.separator
							+ destDocumentid);

				copyPolicies(srcLiveDir, destLiveDir);

			}
		} else if (
			srcArea.equals(Publication.ARCHIVE_AREA)
				| srcArea.equals(Publication.TRASH_AREA)) {
			if (destArea.equals(Publication.AUTHORING_AREA)) {
				File srcDir =
					new File(
						policiesDir,
						this.getFirstarea()
							+ File.separator
							+ this.getSecarea()
							+ File.separator
							+ srcDocumentid);
				File destDir =
					new File(
						policiesDir,
						this.getSecarea() + File.separator + destDocumentid);
				copyPolicies(srcDir, destDir);

				File srcLiveDir =
					new File(
						policiesDir,
						this.getFirstarea()
							+ File.separator
							+ Publication.LIVE_AREA
							+ File.separator
							+ srcDocumentid);
				File destLiveDir =
					new File(
						policiesDir,
						Publication.LIVE_AREA
							+ File.separator
							+ destDocumentid);

				copyPolicies(srcLiveDir, destLiveDir);

			}
		}
	}
}
