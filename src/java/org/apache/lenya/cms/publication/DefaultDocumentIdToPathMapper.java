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

/* $Id: DefaultDocumentIdToPathMapper.java,v 1.22 2004/03/01 16:18:17 gregor Exp $  */

package org.apache.lenya.cms.publication;

import java.io.File;

public class DefaultDocumentIdToPathMapper
    implements DocumentIdToPathMapper, PathToDocumentIdMapper {
    	
    public static final String BASE_FILENAME_PREFIX = "index";
    public static final String BASE_FILENAME_SUFFIX = ".xml";

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
        return BASE_FILENAME_PREFIX + languageSuffix + BASE_FILENAME_SUFFIX;
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

        // check if the file is of the form index.html or index_en.html

        if (fileName.startsWith(BASE_FILENAME_PREFIX)
            && fileName.endsWith(BASE_FILENAME_SUFFIX)) {
            String languageSuffix =
                fileName.substring(
                    BASE_FILENAME_PREFIX.length(),
                    fileName.indexOf(BASE_FILENAME_SUFFIX));
            if (languageSuffix.length() > 0) {
                // trim the leading '_'
                language = languageSuffix.substring(1);
            }
        }
        return language;
    }
}
