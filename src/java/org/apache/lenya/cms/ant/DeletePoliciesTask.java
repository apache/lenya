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

/* $Id: DeletePoliciesTask.java,v 1.4 2004/03/03 12:56:30 gregor Exp $  */

package org.apache.lenya.cms.ant;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.SiteTree;
import org.apache.lenya.cms.publication.SiteTreeNode;
import org.apache.tools.ant.BuildException;

/**
 * Ant task to delete the policies of documents corresponding to a defined subtree
 * Visitor of the defined subtree (visitor pattern). The subtree is reverse visited.
 */
public class DeletePoliciesTask extends TwoDocumentsOperationTask {
    private String policiesDir;

	/**
	 * 
	 */
	public DeletePoliciesTask() {
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
	 * Delte the policies file 
	 * @param srcDir The directory of the policies files.
	 */
	public void deletePolicies(File srcDir) {
		File[] authoringPolicies = this.getFiles(srcDir);
		if (authoringPolicies == null) {
            log("no policies file to delete");
			return;
		}
		for (int i = 0; i < authoringPolicies.length; i++) {
            try {
            FileUtil.forceDelete(authoringPolicies[i]);
            } catch (IOException e) {
                //FIXME: catch Exception because of window's delete problem 
                log("exception " +e);
            }

		}
        if (srcDir.exists() && srcDir.isDirectory() && srcDir.listFiles().length == 0) {  
            try {
          FileUtil.forceDelete(srcDir);
            } catch (IOException e) {
				//FIXME: catch Exception because of window's delete problem 
                log("exception " +e);
            }
        }
	}

	/** (non-Javadoc)
	 * @see org.apache.lenya.cms.ant.DocumentOperationTask#visitSiteTreeNode(org.apache.lenya.cms.publication.SiteTreeNode)
	 */
	public void visitSiteTreeNode(SiteTreeNode node) {
		String srcArea = this.getFirstarea();
		String destArea = this.getSecarea();

		String destDocumentid = node.getAbsoluteId();
		String srcDocumentid =
			destDocumentid.replaceFirst(
				getSecdocumentid(),
				getFirstdocumentid());

		try {
			if (srcArea.equals(Publication.AUTHORING_AREA)) {
				if (destArea.equals(Publication.AUTHORING_AREA)) {
					File srcDir =
						new File(
							policiesDir,
							this.getFirstarea()
								+ File.separator
								+ srcDocumentid);
					log("delete :" + srcDir.getCanonicalPath());
                    deletePolicies(srcDir);
					File srcLiveDir =
						new File(
							policiesDir,
							Publication.LIVE_AREA
								+ File.separator
								+ srcDocumentid);
					log("delete :" + srcLiveDir.getCanonicalPath());
                    deletePolicies(srcLiveDir);

				} else if (
					destArea.equals(Publication.ARCHIVE_AREA)
						| destArea.equals(Publication.TRASH_AREA)) {
					File srcDir =
						new File(
							policiesDir,
							this.getFirstarea()
								+ File.separator
								+ srcDocumentid);
					log("delete :" + srcDir.getCanonicalPath());
                    deletePolicies(srcDir);

					File srcLiveDir =
						new File(
							policiesDir,
							Publication.LIVE_AREA
								+ File.separator
								+ srcDocumentid);
					log("delete :" + srcLiveDir.getCanonicalPath());
                    deletePolicies(srcLiveDir);
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
					log("delete :" + srcDir.getCanonicalPath());
                    deletePolicies(srcDir);

					File srcLiveDir =
						new File(
							policiesDir,
							this.getFirstarea()
								+ File.separator
								+ Publication.LIVE_AREA
								+ File.separator
								+ srcDocumentid);
					log("delete :" + srcLiveDir.getCanonicalPath());
                    deletePolicies(srcLiveDir);
				}
			}
		} catch (IOException e) {
			throw new BuildException(e);
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
