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
package org.apache.lenya.cms.cocoon.transformation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.transformation.AbstractSAXTransformer;
import org.apache.lenya.cms.publication.DefaultDocumentBuilder;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.SiteTree;
import org.apache.lenya.cms.publication.SiteTreeNode;

import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * This transformer lists the children of a document if the tag <namespaceURI:index> 
 * is present in this document. The list of the children is in the form :
 * <namespaceURI:index>
 *   <child href="....html>
 *     <ci:include src="..." element="included"/> 
 *   </child>
 *   ...
 * </namespaceURI:index>
 * Multiple language : if a child doesn't exist in the parent language, then the version 
 * in the default language will be considered. If it doesn't exist too, any other existent 
 * language will be considered.
 * 
 * @author edith
 */
public class DocumentIndexTransformer
	extends AbstractSAXTransformer
	implements Parameterizable {

	private String namespace;
	private String cIncludeNamespace;

	public static final String INDEX_ELEMENT = "index";
	public static final String ABSTRACT_ATTRIBUTE = "abstract";

	/** (non-Javadoc)
		 * @see org.apache.avalon.framework.parameters.Parameterizable#parameterize(org.apache.avalon.framework.parameters.Parameters)
		 */
	public void parameterize(Parameters parameters) throws ParameterException {
		this.namespace = parameters.getParameter("namespace", null);
		this.cIncludeNamespace =
			parameters.getParameter("cIncludeNamespace", null);
	}

	private Document document;

	private Publication publication;

	private String area;

	private DefaultDocumentBuilder builder;

	private SiteTree siteTree;

	/** (non-Javadoc)
	 * @see org.apache.cocoon.sitemap.SitemapModelComponent#setup(org.apache.cocoon.environment.SourceResolver, java.util.Map, java.lang.String, org.apache.avalon.framework.parameters.Parameters)
	 */
	public void setup(
		SourceResolver resolver,
		Map objectModel,
		String src,
		Parameters parameters)
		throws ProcessingException, SAXException, IOException {
		try {
			parameterize(parameters);

			PageEnvelope envelope = null;
			envelope =
				PageEnvelopeFactory.getInstance().getPageEnvelope(objectModel);

			setDocument(envelope.getDocument());
			setPublication(document.getPublication());
			setArea(document.getArea());
			setBuilder(DefaultDocumentBuilder.getInstance());
			setSiteTree(publication.getSiteTree(area));

		} catch (Exception e) {
			throw new ProcessingException(e);
		}

	}

	/** (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(
		String uri,
		String localName,
		String raw,
		Attributes attr)
		throws SAXException {

		if (uri != null
			&& cIncludeNamespace != null
			&& uri.equals(namespace)) {
			if (localName.equals(INDEX_ELEMENT) == true) {

				String cIncludePrefix = "";
				if (!this.cIncludeNamespace.equals("")) {
					cIncludePrefix = "ci:";
				}

				String documentId = document.getId();
				String language = document.getLanguage();
				String defaultLanguage = publication.getDefaultLanguage();
				SiteTreeNode[] children =
					siteTree.getNode(documentId).getChildren();

				super.startElement(uri, localName, raw, attr);

				for (int i = 0; i < children.length; i++) {
					String childId =
						documentId + File.separator + children[i].getId();

					//get child document with the same language than the parent document
					String url =
						builder.buildCanonicalUrl(
							publication,
							area,
							childId,
							language);
					Document doc;
					try {
						doc = builder.buildDocument(publication, url);
					} catch (DocumentBuildException e) {
						throw new SAXException(e);
					}
					File file = doc.getFile();

					if (!file.exists()) {
						//get first the child document in the default language and then in any other existent language
						getLogger().debug(
							"There are no child file "
								+ file.getAbsolutePath()
								+ " in the same language than the parent document"
								+ language);

						//available language    
						String[] availableLanguages = null;
						try {
							availableLanguages = doc.getLanguages();
						} catch (DocumentException e) {
							throw new SAXException(e);
						}

						ArrayList languages = new ArrayList();
						for (int l = 0; l < availableLanguages.length; l++) {
							if (availableLanguages[l].equals(language)) {
								getLogger().debug(
									"do nothing because language was already tested"
										+ availableLanguages[l]);
							} else if (
								availableLanguages[l].equals(
									defaultLanguage)) {
								languages.add(0, availableLanguages[l]);
							} else {
								languages.add(availableLanguages[l]);

							}
						}

						int j = 0;
						while (!file.exists() && j < languages.size()) {
							String newlanguage = (String) languages.get(j);
							url =
								builder.buildCanonicalUrl(
									publication,
									area,
									childId,
									newlanguage);
							try {
								doc = builder.buildDocument(publication, url);
							} catch (DocumentBuildException e) {
								throw new SAXException(e);
							}
							file = doc.getFile();

							j = j + 1;
						}
					}

					if (file.exists()) {
						//create the tags for the child
						String path;
						try {
							path = file.getCanonicalPath();
						} catch (IOException e) {
							throw new SAXException(e);
						}

						AttributesImpl attribute = new AttributesImpl();
						attribute.addAttribute("", "href", "href", "", url);
						super.startElement("", "child", "child", attribute);

						AttributesImpl attributes = new AttributesImpl();
						attributes.addAttribute("", "src", "src", "", path);
						attributes.addAttribute(
							"",
							"element",
							"element",
							"",
							"included");

						super.startElement(
							this.cIncludeNamespace,
							"include",
							cIncludePrefix + "include",
							attributes);
						super.endElement(
							this.cIncludeNamespace,
							"include",
							cIncludePrefix + "include");
						super.endElement("", "child", "child");
					} else {
						//do nothing for this child
						getLogger().warn(
							"There are no existing file for the child with id "
								+ childId);
					}

				}
			} else {
				super.startElement(uri, localName, raw, attr);
			}
		} else {
			super.startElement(uri, localName, raw, attr);
		}

	}

	/**
	 * @return SiteTree The siteTree belonging to the area of the document
	 */
	public SiteTree getSiteTree() {
		return siteTree;
	}

	/**
	 * @param tree The siteTree of the area, which the document belongs.
	 */
	public void setSiteTree(SiteTree tree) {
		siteTree = tree;
	}

	/**
	 * @param string The area, which the document belongs.
	 */
	public void setArea(String string) {
		area = string;
	}

	/**
	 * @param builder The document builder.
	 */
	public void setBuilder(DefaultDocumentBuilder builder) {
		this.builder = builder;
	}

	/**
	 * @param document The document.
	 */
	public void setDocument(Document document) {
		this.document = document;
	}

	/**
	 * @param publication The publication, which the document belongs.
	 */
	public void setPublication(Publication publication) {
		this.publication = publication;
	}

}
