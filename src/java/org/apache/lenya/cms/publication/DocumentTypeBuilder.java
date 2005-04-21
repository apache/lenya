/*
 * Copyright  1999-2005 The Apache Software Foundation
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


/**
 * Interface for a builder for document types.
 *
 * Note that the term "document types" is deprecated, and
 * will be replaced by the term "resource types" in the future.
 * 
 * @version $Id$
 */
public interface DocumentTypeBuilder {

    /**
     * The Avalon role.
     */
    String ROLE = DocumentTypeBuilder.class.getName();
    
    /**
     * Builds a document type for a given name.
     * @param name A string value.
     * @param publication The publication the document type belongs to.
     * @return A document type object.
     * @throws DocumentTypeBuildException When something went wrong.
     */
    public DocumentType buildDocumentType(String name, Publication publication)
        throws DocumentTypeBuildException;

}
