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

/* $Id: SetIdentifier.java,v 1.6 2004/03/03 12:56:30 gregor Exp $  */

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
        dublincore.setValue("identifier", documentid);
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
