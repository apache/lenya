/*
 * <License>
 * 
 * ============================================================================ The Apache Software
 * License, Version 1.1
 * ============================================================================
 * 
 * Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modifica- tion, are permitted
 * provided that the following conditions are met: 1. Redistributions of source code must retain
 * the above copyright notice, this list of conditions and the following disclaimer. 2.
 * Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution. 3. The end-user documentation included with the redistribution, if any,
 * must include the following acknowledgment: "This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/)." Alternately, this acknowledgment may
 * appear in the software itself, if and wherever such third-party acknowledgments normally appear. 4.
 * The names "Apache Lenya" and "Apache Software Foundation" must not be used to endorse or promote
 * products derived from this software without prior written permission. For written permission,
 * please contact apache@apache.org. 5. Products derived from this software may not be called
 * "Apache", nor may "Apache" appear in their name, without prior written permission of the Apache
 * Software Foundation.
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
package org.apache.lenya.cms.publication;

import java.util.HashMap;

/**
 * @author andreas
 * @version $Id: DefaultDocumentBuilder.java,v 1.22 2003/10/21 16:34:36 andreas Exp $
 *  
 */
public class DefaultDocumentBuilder implements DocumentBuilder {
	/**
	 * Non-public constructor.
	 */
	protected DefaultDocumentBuilder() {
	}

	private static DefaultDocumentBuilder instance;

	/**
	 * Returns the singleton instance.
	 * 
	 * @return A document builder.
	 */
	public static DefaultDocumentBuilder getInstance() {
		if (instance == null) {
			instance = new DefaultDocumentBuilder();
		}

		return instance;
	}

	/**
	 * @see org.apache.lenya.cms.publication.DocumentBuilder#buildDocument(org.apache.lenya.cms.publication.Publication,
	 *      java.lang.String)
	 */
	public Document buildDocument(Publication publication, String url)
		throws DocumentBuildException {

		URLInformation info = new URLInformation(url);

		String documentURL = info.getDocumentUrl();
		String originalURL = documentURL;

		String extension = getExtension(documentURL);
		documentURL = removeExtensions(documentURL);

		String language = getLanguage(documentURL);
		String fullLanguage = "".equals(language) ? "" : ("_" + language);
		documentURL = documentURL.substring(0, documentURL.length() - fullLanguage.length());

		boolean defaultLanguageForced = false;
		if ("".equals(language)) {
			defaultLanguageForced = true;
			language = publication.getDefaultLanguage();
		}

		String documentId = documentURL;

		if (!documentId.startsWith("/")) {
			throw new DocumentBuildException(
				"Document ID [" + documentId + "] does not start with '/'!");
		}

		DefaultDocument document =
			new DefaultDocument(publication, documentId, info.getArea(), language);
		document.setExtension(extension);
		document.setDocumentURL(originalURL);

		if (defaultLanguageForced) {
			// unfortunatelly we cannot count on the document to always be available
			// in the default language. So if the default language is not in the list
			// of available languages for this document, simply use the first available
			// language.
			HashMap languagesMap = new HashMap();
			String[] languages = null;
			try {
				languages = document.getLanguages();
			} catch (DocumentException e) {
				throw new DocumentBuildException(e);
			}
			// If the document has no languages, we'll just leave it
			// as it is (i.e. we leave it at the default language)
			if (languages.length > 0) {
				for (int i = 0; i < languages.length; i++) {
					languagesMap.put(languages[i], languages[i]);
				}
				if (!languagesMap.containsKey(document.getLanguage())) {
					document.setLanguage(languages[0]);
				}
			}
		}

		return document;
	}

	/**
	 * Removes all "."-separated extensions from a URL (e.g., <code>/foo.print.html</code> is
	 * transformed to <code>/foo</code>).
	 * 
	 * @param url
	 *            The URL to trim.
	 * @return A URL string.
	 */
	protected String removeExtensions(String url) {
		int dotIndex = url.indexOf(".");
		if (dotIndex > -1) {
			url = url.substring(0, dotIndex);
		}
		return url;
	}

	/**
	 * Returns the language of a URL.
	 * 
	 * @param urlWithoutSuffix
	 *            The URL without the suffix.
	 * @return A string.
	 */
	protected String getLanguage(String urlWithoutSuffix) {

		String suffix = "";
		String url = urlWithoutSuffix;
		if (url.length() >= 3 && url.charAt(url.length() - 3) == '_') {
			suffix = url.substring(url.length() - 2);
		}
		return suffix;
	}

	/**
	 * Returns the extension of a URL.
	 * 
	 * @param url
	 *            The URL.
	 * @return The extension.
	 */
	protected String getExtension(String url) {
		int startOfSuffix = url.lastIndexOf('.');
		String suffix = "";

		if ((startOfSuffix > -1) && !url.endsWith(".")) {
			suffix = url.substring(startOfSuffix + 1);
		}

		return suffix;
	}

	/**
	 * @see org.apache.lenya.cms.publication.DocumentBuilder#isDocument(org.apache.lenya.cms.publication.Publication,
	 *      java.lang.String)
	 */
	public boolean isDocument(Publication publication, String url) throws DocumentBuildException {
		boolean isDocument = false;

		String publicationURI = url.substring(("/" + publication.getId()).length());
		if (publicationURI.startsWith("/")) {
			publicationURI = publicationURI.substring(1);

			int slashIndex = publicationURI.indexOf("/");
			if (slashIndex > -1) {
				String documentUri = publicationURI.substring(slashIndex);
				if (documentUri.startsWith("/")) {
					isDocument = true;
				}
			}
		}

		return isDocument;
	}

	protected String buildCanonicalDocumentUrl(
		Publication publication,
		String documentid,
		String language) {

		String languageSuffix = "";
		if (!language.equals(publication.getDefaultLanguage())) {
			languageSuffix = "_" + language;
		}

		String url = documentid + languageSuffix + ".html";
		return url;
	}

	/**
	 * @see org.apache.lenya.cms.publication.DocumentBuilder#buildCanonicalUrl(org.apache.lenya.cms.publication.Publication,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 */
	public String buildCanonicalUrl(
		Publication publication,
		String area,
		String documentid,
		String language) {

		String documentUrl = buildCanonicalDocumentUrl(publication, documentid, language);
		String url = "/" + publication.getId() + "/" + area + documentUrl;

		return url;
	}

	/**
	 * @see org.apache.lenya.cms.publication.DocumentBuilder#buildCanonicalUrl(org.apache.lenya.cms.publication.Publication,
	 *      java.lang.String, java.lang.String)
	 */
	public String buildCanonicalUrl(Publication publication, String area, String documentId) {
		return buildCanonicalUrl(publication, area, documentId, publication.getDefaultLanguage());
	}

	/**
	 * @see org.apache.lenya.cms.publication.DocumentBuilder#buildLanguageVersion(org.apache.lenya.cms.publication.Document,
	 *      java.lang.String)
	 */
	public Document buildLanguageVersion(Document document, String language) {
		DefaultDocument newDocument =
			new DefaultDocument(
				document.getPublication(),
				document.getId(),
				document.getArea(),
				language);
		newDocument.setExtension(document.getExtension());
		String url =
			buildCanonicalDocumentUrl(document.getPublication(), document.getId(), language);
		newDocument.setDocumentURL(url);

		return newDocument;
	}
}
