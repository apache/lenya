/*
$Id: CopyPoliciesTask.java,v 1.2 2003/11/10 16:42:19 andreas Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
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
 * 
 * @author edith
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

		String parentid = node.getAbsoluteParentId();
		String srcDocumentid = parentid + "/" + node.getId();
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
