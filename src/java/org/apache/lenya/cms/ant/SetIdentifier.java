/*
 * $Id: SetIdentifier.java,v 1.4 2003/10/21 09:51:54 andreas Exp $ <License>
 * 
 * ============================================================================ The Apache Software
 * License, Version 1.1
 * ============================================================================
 * 
 * Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modifica- tion, are permitted
 * provided that the following conditions are met:
 *  1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 *  3. The end-user documentation included with the redistribution, if any, must include the
 * following acknowledgment: "This product includes software developed by the Apache Software
 * Foundation (http://www.apache.org/)." Alternately, this acknowledgment may appear in the
 * software itself, if and wherever such third-party acknowledgments normally appear.
 *  4. The names "Apache Lenya" and "Apache Software Foundation" must not be used to endorse or
 * promote products derived from this software without prior written permission. For written
 * permission, please contact apache@apache.org.
 *  5. Products derived from this software may not be called "Apache", nor may "Apache" appear in
 * their name, without prior written permission of the Apache Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLU- DING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * This software consists of voluntary contributions made by many individuals on behalf of the
 * Apache Software Foundation and was originally created by Michael Wechner <michi@apache.org> .
 * For more information on the Apache Soft- ware Foundation, please see <http://www.apache.org/> .
 * 
 * Lenya includes software developed by the Apache Software Foundation, W3C, DOM4J Project,
 * BitfluxEditor, Xopus, and WebSHPINX. </License>
 */
package org.apache.lenya.cms.ant;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.DublinCore;
import org.apache.lenya.cms.publication.Label;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.SiteTree;
import org.apache.lenya.cms.publication.SiteTreeException;
import org.apache.lenya.cms.publication.SiteTreeNode;
import org.apache.tools.ant.BuildException;

/**
 * anttask to set the document-id in the dc:identifier of all existing files corresponding to this
 * document-id
 * 
 * @author edith
 *  
 */
public class SetIdentifier extends PublicationTask {

	private String area;
	private String documentid;

	/**
	 *  
	 */
	public SetIdentifier() {
		super();
	}

	/**
	 * @return String The area.
	 */
	public String getArea() {
		return area;
	}

	/**
	 * @return String The document-id.
	 */
	public String getDocumentid() {
		return documentid;
	}

	/**
	 * @param string
	 *            The area.
	 */
	public void setArea(String string) {
		area = string;
	}

	/**
	 * @param string
	 *            The document-id.
	 */
	public void setDocumentid(String string) {
		documentid = string;
	}

	/**
	 * write the document id in the DC Identifier of a document corresponding to this url
	 * 
	 * @param publication
	 *            The publication the document belongs to.
	 * @param url
	 *            The URL of the form /{publication-id}/...
	 * @throws DocumentBuildException
	 *             when something went wrong when building the cms document.
	 * @throws DocumentException
	 *             when something went wrong when getting the DublinCore.
	 */
	public void writeDCIdentifier(Publication publication, String url)
		throws DocumentBuildException, DocumentException {
		assert url != null;

		Document document = null;
		document = publication.getDocumentBuilder().buildDocument(publication, url);
		DublinCore dublincore = document.getDublinCore();
		dublincore.setIdentifier(documentid);
		dublincore.save();
	}

	/**
	 * @see org.apache.tools.ant.Task#execute()
	 */
	public void execute() throws BuildException {
		log("document-id " + this.getDocumentid());
		log("area " + this.getArea());

		Publication publication = getPublication();

		String language = null;
		String url = null;
		SiteTree tree;

		try {
			tree = publication.getSiteTree(area);
		} catch (SiteTreeException e) {
			throw new BuildException(e);
		}
		SiteTreeNode node = tree.getNode(documentid);
		Label[] labels = node.getLabels();

		DocumentBuilder builder = publication.getDocumentBuilder();

		try {
			if (labels.length < 1) {
				log("no languages found for the node with id : " + node.getId());
				url = builder.buildCanonicalUrl(publication, area, documentid);
				writeDCIdentifier(publication, url);
			} else {
				for (int i = 0; i < labels.length; i++) {
					language = labels[i].getLanguage();
					url = builder.buildCanonicalUrl(publication, area, documentid, language);
					writeDCIdentifier(publication, url);
				}
			}
		} catch (DocumentException e1) {
			throw new BuildException(e1);
		} catch (DocumentBuildException e2) {
			throw new BuildException(e2);
		}
	}

}
