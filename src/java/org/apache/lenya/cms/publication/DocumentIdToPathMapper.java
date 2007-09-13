/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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

/* $Id: DocumentIdToPathMapper.java 473841 2006-11-12 00:46:38Z gregor $  */

package org.apache.lenya.cms.publication;

import java.io.File;

public interface DocumentIdToPathMapper {
    
    /**
     * Compute the document-path for a given publication, area
     * and document-id. The file separator is the slash (/).
     *
     * @param documentId the document-id of the document
     * @param language the language of the document
     * 
     * @return the path to the document, without publication ID and area
     */
    String getPath(String documentId, String language);

    /**
     * Compute the document-path for a given publication, area, 
     * document-id and language
     *
     * @param publication the publication of the document
     * @param area the area of the document
     * @param documentId the document-id of the document
     * @param language the language of the document
     * 
     * @return the path to the document
     */
    File getFile(Publication publication, String area, String documentId,
        String language);

    /**
     * Compute the document-path for a given publication, area and 
     * document-id. As there are possibly multiple files for the same 
     * document-id (for different languages) the return value is a directory.
     *  
     * @param publication The publication.
     * @param area The area.
     * @param documentId The document id.
     * @param language The language of the document.
     * 
     * @return The directory where all the files with the same 
     * document-id are located
     */
    File getDirectory(Publication publication, String area, String documentId, String language);

    /**
     * @deprecated replaced by getDirectory with access to the language
     * 
     * Compute the document-path for a given publication, area and 
     * document-id. As there are possibly multiple files for the same 
     * document-id (for different languages) the return value is a directory.
     *  
     * @param publication The publication.
     * @param area The area.
     * @param documentId The document id.
     * 
     * @return The directory where all the files with the same 
     * document-id are located
     */
    File getDirectory(Publication publication, String area, String documentId);
}
