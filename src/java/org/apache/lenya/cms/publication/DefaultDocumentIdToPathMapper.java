/*
 * $Id: DefaultDocumentIdToPathMapper.java,v 1.20 2004/01/07 18:15:50 egli Exp $ <License>
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

import java.io.File;

/**
 * @author egli
 * 
 *  
 */
public class DefaultDocumentIdToPathMapper
    implements DocumentIdToPathMapper, PathToDocumentIdMapper {
    	
    /**
	 * @see org.apache.lenya.cms.publication.DocumentIdToPathMapper#getFile(org.apache.lenya.cms.publication.Publication,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 */
    public File getFile(Publication publication, String area, String documentId, String language) {
        File file = new File(getDirectory(publication, area, documentId), getFilename(language));
        return file;
    }

    /**
	 * (non-Javadoc)
	 * 
	 * @see org.apache.lenya.cms.publication.DocumentIdToPathMapper#getDirectory(org.apache.lenya.cms.publication.Publication,
	 *      java.lang.String, java.lang.String)
	 */
    public File getDirectory(Publication publication, String area, String documentId) {
        assert documentId.startsWith("/");
        // remove leading slash
        documentId = documentId.substring(1);
        documentId = documentId.replace('/', File.separatorChar);

        File file =
            new File(
                publication.getDirectory(),
                Publication.CONTENT_PATH + File.separator + area + File.separator + documentId);

        return file;
    }

    /**
	 * @see org.apache.lenya.cms.publication.DocumentIdToPathMapper#getPath(java.lang.String,
	 *      java.lang.String)
	 */
    public String getPath(String documentId, String language) {
        assert documentId.startsWith("/");
        // remove leading slash
        documentId = documentId.substring(1);
        return documentId + "/" + getFilename(language);
    }

    /**
	 * Constructs the filename for a given language.
	 * 
	 * @param language The language.
	 * @return A string value.
	 */
    protected String getFilename(String language) {
        String languageSuffix = "";
        if (language != null && !"".equals(language)) {
            languageSuffix = "_" + language;
        }
        return "index" + languageSuffix + ".xml";
    }

    /**
     * Returns the document ID for a certain file.
     * 
     * @param publication The publication.
     * @param area The area.
     * @param file The file representing the document.
     * @throws DocumentDoesNotExistException when the document
     * referenced by the file does not exist.
     */
    public String getDocumentId(
        Publication publication,
        String area,
        File file)
        throws DocumentDoesNotExistException {

        String fileName = file.getAbsolutePath();
        String contentDirName =
            publication.getContentDirectory(area).getAbsolutePath();
        if (fileName.startsWith(contentDirName)) {
            // trim everything up to the documentId
            String relativeFileName =
                fileName.substring(contentDirName.length());
            // trim everything after the documentId
            relativeFileName =
                relativeFileName.substring(
                    0,
                    relativeFileName.lastIndexOf(File.separator));
            // and replace the os specific separator by '/'
            return relativeFileName.replace(File.separatorChar, '/');
        } else {
            throw new DocumentDoesNotExistException(
                "No document associated with file" + fileName);
        }
    }
    
    /**
     * Returns the language for a certain file
     * 
     * @param file the document file
     * 
     * @return the language for the given document file or null if
     * the file has no language.
     */
    public String getLanguage(File file) {
        String fileName = file.getName();
        String language = null;
        String fileNameSuffix = ".xml";
        String fileNamePrefix = "index";
        // check if the file is of the form index.html or index_en.html

        if (fileName.startsWith(fileNamePrefix)
            && fileName.endsWith(fileNameSuffix)) {
            String languageSuffix =
                fileName.substring(
                    fileNamePrefix.length(),
                    fileName.indexOf(fileNameSuffix));
            if (languageSuffix.length() > 0) {
                // trim the leading '_'
                language = languageSuffix.substring(1);
            }
        }
        return language;
    }
}
