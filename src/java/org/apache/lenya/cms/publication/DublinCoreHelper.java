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

/* $Id: DublinCoreHelper.java,v 1.5 2004/03/01 16:18:17 gregor Exp $  */

package org.apache.lenya.cms.publication;

import org.apache.log4j.Category;

/**
 * Facade to get the DublinCore through the cms Document
 */
public final class DublinCoreHelper {

	/**
	 *  
	 */
	private DublinCoreHelper() {
	}

	private static Category log = Category.getInstance(DublinCoreHelper.class);

	/**
	 * Get the value of the DCIdentifier corresponding to a document id.
	 * 
	 * @param publication
	 *            The publication the document(s) belongs to.
	 * @param area
	 *            The area the document(s) belongs to.
	 * @param documentId
	 *            The document id.
	 * @return a String. The value of the DCIdentifier.
	 * @throws SiteTreeException
	 *             when something with the sitetree went wrong.
	 * @throws DocumentBuildException
	 *             when the building of a document failed.
	 * @throws DocumentException
	 *             when something with the document went wrong.
	 */
	public static String getDCIdentifier(Publication publication, String area, String documentId)
		throws SiteTreeException, DocumentBuildException, DocumentException {
		String identifier = null;
		String language = null;
		String url = null;
		Document document = null;

		SiteTree tree = publication.getSiteTree(area);
		SiteTreeNode node = tree.getNode(documentId);

		DocumentBuilder builder = publication.getDocumentBuilder();

		int i = 0;
		Label[] labels = node.getLabels();
		if (labels.length > 0) {
			while (identifier == null && i < labels.length) {
				language = labels[i].getLanguage();
				url = builder.buildCanonicalUrl(publication, area, documentId, language);
				document = builder.buildDocument(publication, url);
				log.debug("document file : " + document.getFile().getAbsolutePath());
				DublinCore dublincore = document.getDublinCore();
				log.debug("dublincore title : " + dublincore.getFirstValue(DublinCore.ELEMENT_TITLE));
				identifier = dublincore.getFirstValue(DublinCore.ELEMENT_IDENTIFIER);
				i = i + 1;
			}
		}
		if ((labels.length < 1) | (identifier == null)) {
			url = builder.buildCanonicalUrl(publication, area, documentId);
			document = builder.buildDocument(publication, url);
			DublinCore dublincore = document.getDublinCore();
			identifier = dublincore.getFirstValue(DublinCore.ELEMENT_IDENTIFIER);
		}

		return identifier;
	}
}
