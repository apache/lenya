/*
$Id: DeleteContentTask.java,v 1.3 2003/10/21 09:51:55 andreas Exp $
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

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentBuilder;
import org.apache.lenya.cms.publication.Publication;
import org.apache.tools.ant.BuildException;

/**
 * Ant task to delete the content (xml file) of a document with document id 
 * <documentid>, area <area> and language <language>
 * @author edith
 */
public class DeleteContentTask extends PublicationTask {
	private String area;
	private String documentid;
	private String language;

	/**
     * Creates a new instance of DeleteContentTask
	 */
	public DeleteContentTask() {
		super();
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
	 * @param string The area.
	 */
	public void setArea(String string) {
		area = string;
	}

	/**
	 * Set the value of the document id.
	 * 
	 * @param string The document id.
	 */
	public void setDocumentid(String string) {
		documentid = string;
	}

	/**
	 * Set the value of the language.
	 * 
	 * @param string The language.
	 */
	public void setLanguage(String string) {
		language = string;
	}

	/**
	 * Remove the content of the document corresponding to the document id
	 * <documentid>, area <area> and language <language>.  
	 * @param language The language
	 * @param documentid The document id
	 * @param area The area
	 */
	public void deleteContent(
		String language,
		String documentid,
		String area) {
		Publication publication = getPublication();
		DocumentBuilder builder = publication.getDocumentBuilder();
		String url =
			builder.buildCanonicalUrl(publication, area, documentid, language);
		Document document;
		try {
			document = builder.buildDocument(publication, url);
		} catch (DocumentBuildException e) {
			throw new BuildException(e);
		}
		File file = document.getFile();

		if (!file.exists()) {
			log("There is no file " + file.getAbsolutePath());
			return;
		}

		File directory = file.getParentFile();
		file.delete();

		if (directory.exists() && directory.isDirectory()) {
			File[] list = directory.listFiles();
			if (list.length < 1) {
				directory.delete();
			}
		}
	}

	/** (non-Javadoc)
	 * @see org.apache.tools.ant.Task#execute()
	 */
	public void execute() throws BuildException {
		try {
			log("document-id : " + getDocumentid());
			log("area: " + getArea());
			log("language : " + getArea());
			deleteContent(getLanguage(), getDocumentid(), getArea());
		} catch (Exception e) {
			throw new BuildException(e);
		}
	}
}
