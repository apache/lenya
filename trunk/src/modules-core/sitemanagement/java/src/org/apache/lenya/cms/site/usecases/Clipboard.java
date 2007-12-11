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
package org.apache.lenya.cms.site.usecases;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentBuildException;
import org.apache.lenya.cms.publication.DocumentFactory;
import org.apache.lenya.cms.publication.Publication;

/**
 * Clipboard for cut/copy/paste of documents. The clipping method is either {@link #METHOD_CUT} or
 * {@link #METHOD_COPY}.
 * 
 * @version $Id$
 */
public class Clipboard {

    private String publicationId;
    private String area;
    private String uuid;
    private String language;
    private int method;

    /**
     * The "cut" method.
     */
    public static final int METHOD_CUT = 0;
    
    /**
     * The "copy" method.
     */
    public static final int METHOD_COPY = 1;

    /**
     * Ctor.
     * @param document The document to put on the clipboard.
     * @param _method The clipping method.
     */
    public Clipboard(Document document, int _method) {
        this.publicationId = document.getPublication().getId();
        this.area = document.getArea();
        this.uuid = document.getUUID();
        this.language = document.getLanguage();
        this.method = _method;
    }

    /**
     * Returns the document for the current identity map.
     * @param identityMap The identity map.
     * @param publication The publication.
     * @return A document.
     * @throws DocumentBuildException if the document could not be built.
     */
    public Document getDocument(DocumentFactory identityMap, Publication publication) throws DocumentBuildException {
        Document document = identityMap.get(publication, this.area, this.uuid, this.language);
        return document;
    }

    /**
     * @return The ID of the publication the document belongs to.
     */
    public String getPublicationId() {
        return this.publicationId;
    }

    /**
     * Returns the method of this clipboard.
     * @return An integer.
     */
    public int getMethod() {
        return this.method;
    }
}