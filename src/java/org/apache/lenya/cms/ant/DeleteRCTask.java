/*
$Id: DeleteRCTask.java,v 1.2 2004/01/21 18:04:55 edith Exp $
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
s
 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.cms.ant;

import java.io.File;
import java.io.IOException;

import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.SiteTree;
import org.apache.lenya.cms.publication.SiteTreeNode;
import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.tools.ant.BuildException;

/**
 * Ant task to delete the rcml- and backup files of documents corresponding to a defined subtree
 * (Visitor pattern) Visitor of the subtree. The subtree is reverse visited.
 * @author edith
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
		Publication publication = getPublication();
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

		String parentid = node.getAbsoluteParentId();
		String destDocumentid = parentid + "/" + node.getId();
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
