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

/* $Id$  */

package org.apache.lenya.cms.publication;

import java.io.File;

/**
 * Identity Document Id to path mapper
 */
public class IdentityDocumentIdToPathMapper implements DocumentIdToPathMapper {

    /**
     * @see org.apache.lenya.cms.publication.DocumentIdToPathMapper#getFile(org.apache.lenya.cms.publication.Publication,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public File getFile(Publication publication, String area, String documentId, String language) {
        File areaDirectory = new File(publication.getDirectory(), Publication.CONTENT_PATH
                + File.separator + area);
        File file = new File(areaDirectory, getPath(documentId, language));
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
        return documentId + getSuffix(language);
    }

    /**
     * Constructs the filename for a given language.
     * @param language The language.
     * @return A string value.
     */
    protected String getSuffix(String language) {
        String languageSuffix = "";
        if (language != null && !"".equals(language)) {
            languageSuffix = "_" + language;
        }
        return languageSuffix;
    }

}
