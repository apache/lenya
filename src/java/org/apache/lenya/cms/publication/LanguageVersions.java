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

/* $Id: LanguageVersions.java,v 1.5 2004/03/01 16:18:17 gregor Exp $  */

package org.apache.lenya.cms.publication;

/**
 * Document set containing all language versions of a document.
 */
public class LanguageVersions extends DocumentSet {

    /**
     * Ctor.
     * @param document The document.
     * @throws DocumentException when something went wrong.
     */
    public LanguageVersions(Document document) throws DocumentException {
        String[] languages = document.getLanguages();
        DocumentBuilder builder = document.getPublication().getDocumentBuilder();
        add(document);
        
        for (int i = 0; i < languages.length; i++) {
            if (!document.getLanguage().equals(languages[i])) {
                Document languageVersion = builder.buildLanguageVersion(document, languages[i]);
                add(languageVersion);
            }
        }
    }

}
