/*
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
import java.io.IOException;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.Label;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.SiteTreeNode;
import org.apache.lenya.cms.rc.RevisionController;
import org.apache.tools.ant.BuildException;

/**
 * Ant task, to init the rc files of the destination's documents corresponding to a given source subtree.
 * Evry destination file is checked in.
 * (Visitor pattern)
 * @author edith
 *
 */
public class InitRCTask extends TwoDocumentsOperationTask {
	private String rcmlDir = "";
	private String rcbakDir = "";
	private String userId = "";
	private RevisionController rc = null;
	/**
	 * 
	 */
	public InitRCTask() {
		super();
	}

	/**
	 * @return String The backups' directory.
	 */
	public String getRcbakDir() {
		return rcbakDir;
	}

	/**
	 * @return String The rcml-files' directory.
	 */
	public String getRcmlDir() {
		return rcmlDir;
	}

	/**
	 * @param string The backup's directory.
	 */
	public void setRcbakDir(String string) {
		rcbakDir = string;
	}

	/**
	 * @param string The rcml-files' directory.
	 */
	public void setRcmlDir(String string) {
		rcmlDir = string;
	}

	/**
	 * @see org.apache.lenya.cms.publication.SiteTreeNodeVisitor#visitSiteTreeNode(org.apache.lenya.cms.publication.SiteTreeNode)
	 */
	public void visitSiteTreeNode(SiteTreeNode node) {
		try {
			Publication publication = getPublication();
			String publicationPath =
				this.getPublicationDirectory().getCanonicalPath();
			DocumentBuilder builder = publication.getDocumentBuilder();

			String parentid = node.getAbsoluteParentId();
			String srcDocumentid = parentid + "/" + node.getId();
			String destDocumentid =
				srcDocumentid.replaceFirst(
					getFirstdocumentid(),
					getSecdocumentid());

			Label[] labels = node.getLabels();
			for (int i = 0; i < labels.length; i++) {
				String language = labels[i].getLanguage();
				String destUrl =
					builder.buildCanonicalUrl(
						publication,
						getSecarea(),
						destDocumentid,
						language);
				Document destDoc;
				destDoc = builder.buildDocument(publication, destUrl);
                String filename = destDoc.getFile().getCanonicalPath();
				filename = filename.substring(publicationPath.length());
                rc.reservedCheckIn(filename, getUserId(), true);
			}
		} catch (Exception e) {
			throw new BuildException(e);
		}
	}

	/** 
	 * @see org.apache.tools.ant.Task#execute()
	 **/

	public void execute() throws BuildException {
		try {
            log("rcml dir" + this.getRcmlDir());
            log("rcbak dir" + this.getRcbakDir());
            log("user" + this.getUserId());
            String publicationPath =
                this.getPublicationDirectory().getCanonicalPath();
            String rcmlDirectory = new File(publicationPath, this.getRcmlDir()).getCanonicalPath();
            String rcbakDirectory = new File(publicationPath, this.getRcbakDir()).getCanonicalPath();
            this.rc  =
                new RevisionController(
            rcmlDirectory,
            rcbakDirectory,
                    publicationPath);
		} catch (IOException e) {
			throw new BuildException(e);
		}
		super.execute();
	}

	/**
	 * @return String The user id.
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param string The user id.
	 */
	public void setUserId(String string) {
		userId = string;
	}

}
