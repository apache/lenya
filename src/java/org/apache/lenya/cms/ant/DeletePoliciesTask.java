/*
$Id: DeletePoliciesTask.java,v 1.3 2004/02/04 10:09:03 egli Exp $
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
import org.apache.lenya.cms.publication.SiteTree;
import org.apache.lenya.cms.publication.SiteTreeNode;
import org.apache.tools.ant.BuildException;

/**
 * Ant task to delete the policies of documents corresponding to a defined subtree
 * Visitor of the defined subtree (visitor pattern). The subtree is reverse visited.
 * @author edith
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
