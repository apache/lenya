/*
$Id: CopyContentTask.java,v 1.4 2003/10/29 13:41:28 edith Exp $
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
 * @author edith
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

		String parentid = node.getAbsoluteParentId();
		String srcDocumentid = parentid + "/" + node.getId();
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
