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

/* $Id: DeactivateResourcesTask.java,v 1.3 2004/03/03 12:56:30 gregor Exp $  */

package org.apache.lenya.cms.ant;

import java.io.File;

import org.apache.lenya.cms.publication.DefaultSiteTree;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.Label;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.ResourcesManager;
import org.apache.lenya.cms.publication.SiteTreeException;
import org.apache.lenya.cms.publication.SiteTreeNode;
import org.apache.tools.ant.BuildException;

/**
 * Ant task to remove the resources belonging to a document with document id <documentid>, area
 * <area>and language <language>. The resources are removed when no more version of this document
 * is available.
 */
public class DeactivateResourcesTask extends PublicationTask {
	private String area;
	private String documentid;
	private String language;

	/**
	 * Creates a new instance of DeactivateResourcesTask
	 */
	public DeactivateResourcesTask() {
		super();
	}

	/**
	 * Remove the resources belonging to the document with document id <documentid>, area <area>
	 * and language <language>, when no more version of this document is available.
	 * 
	 * @param language
	 *            The language
	 * @param documentid
	 *            The document id
	 * @param area
	 *            The area
	 * 
	 * @throws SiteTreeException
	 *             if an error occurs
	 */
	public void deactivateResources(String language, String documentid, String area)
		throws SiteTreeException {
		Publication publication = getPublication();
		DefaultSiteTree tree = null;

		try {
			tree = publication.getSiteTree(area);
			SiteTreeNode node = tree.getNode(documentid);
			Label[] labels = null;
			if (node != null) {
				labels = node.getLabels();
			}
			if (node == null | (labels != null && labels.length < 1)) {

				DocumentBuilder builder = publication.getDocumentBuilder();
				String url = builder.buildCanonicalUrl(publication, area, documentid, language);
				Document doc;
				try {
					doc = builder.buildDocument(publication, url);
				} catch (DocumentBuildException e) {
					throw new BuildException(e);
				}
				ResourcesManager resourcesMgr = new ResourcesManager(doc);
				File[] resources = resourcesMgr.getResources();
				for (int i = 0; i < resources.length; i++) {
					resources[i].delete();
				}
				File directory = resourcesMgr.getPath();
				directory.delete();
			}
		} catch (Exception e) {
			throw new SiteTreeException(e);
		}
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.tools.ant.Task#execute()
	 */
	public void execute() throws BuildException {
		try {
			log("document-id : " + getDocumentid());
			log("area: " + getArea());
			log("language : " + getArea());
			deactivateResources(getLanguage(), getDocumentid(), getArea());
		} catch (Exception e) {
			throw new BuildException(e);
		}
	}

	/**
	 * Get the value of the area.
	 * 
	 * @return The area.
	 */
	public String getArea() {
		return area;
	}

	/**
	 * Get the value of the document id.
	 * 
	 * @return The document id.
	 */
	public String getDocumentid() {
		return documentid;
	}

	/**
	 * Get the value of the language.
	 * 
	 * @return The language.
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * Set the value of the area.
	 * 
	 * @param string
	 *            The area.
	 */
	public void setArea(String string) {
		area = string;
	}

	/**
	 * Set the value of the document id.
	 * 
	 * @param string
	 *            The document id.
	 */
	public void setDocumentid(String string) {
		documentid = string;
	}

	/**
	 * Set the value of the language.
	 * 
	 * @param string
	 *            The language.
	 */
	public void setLanguage(String string) {
		language = string;
	}

}
