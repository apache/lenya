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
package org.apache.lenya.cms.publication;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.excalibur.source.SourceResolver;
import org.apache.lenya.cms.cocoon.source.RepositorySource;
import org.apache.lenya.cms.repository.Node;

/**
 * Value object to identify documents.
 */
public class DocumentIdentifier {

    private Publication publication;
    private String area;
    private String language;
    private String uuid;

    /**
     * Ctor.
     * @param publication The publication.
     * @param area The area.
     * @param uuid The document UUID.
     * @param language The language.
     * @param uuid The UUID.
     */
    public DocumentIdentifier(Publication publication, String area, String uuid, String language) {
        this.publication = publication;
        this.area = area;
        this.language = language;
        this.uuid = uuid;
    }
    
    /**
     * @return The UUID.
     */
    public String getUUID() {
        return this.uuid;
    }

    /**
     * @return The area.
     */
    public String getArea() {
        return area;
    }

    /**
     * @return The language.
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @return The publication.
     */
    public Publication getPublication() {
        return publication;
    }

    public boolean equals(Object obj) {
        return (obj instanceof DocumentIdentifier) && obj.hashCode() == hashCode();
    }

    public int hashCode() {
        return getKey().hashCode();
    }

    protected String getKey() {
        return this.publication.getId() + ":" + this.area + ":" + this.uuid + ":" + this.language;
    }

    public String toString() {
        return getKey();
    }
    
    /**
     * Returns a version of this identifier from another area.
     * @param area The area.
     * @return A document identifier.
     */
    public DocumentIdentifier getAreaVersion(String area) {
        return new DocumentIdentifier(getPublication(), area, getUUID(), getLanguage());
    }

    /**
     * Returns a version of this identifier in a different language.
     * @param language The language.
     * @return A document identifier.
     */
    public DocumentIdentifier getLanguageVersion(String language) {
        return new DocumentIdentifier(getPublication(), getArea(), getUUID(), language);
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getRepositoryNode()
     */
    public Node getRepositoryNode(ServiceManager manager) {
        Node node = null;
        SourceResolver resolver = null;
        RepositorySource documentSource = null;
        try {
            resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
            documentSource = (RepositorySource) resolver.resolveURI(getSourceURI());
            node = documentSource.getNode();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (resolver != null) {
                if (documentSource != null) {
                    resolver.release(documentSource);
                }
                manager.release(resolver);
            }
        }
        return node;
    }

    /**
     * @see org.apache.lenya.cms.publication.Document#getSourceURI()
     */
    public String getSourceURI() {
        String path = getPublication().getPathMapper().getPath(getUUID(), getLanguage());
        return getPublication().getSourceURI() + "/content/" + getArea() + "/" + path;
    }

}
