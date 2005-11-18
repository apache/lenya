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
package org.apache.lenya.cms.repo.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.lenya.cms.repo.DocumentType;
import org.apache.lenya.cms.repo.DocumentTypeRegistry;
import org.apache.lenya.cms.repo.RepositoryException;

/**
 * Document type registry implementation.
 */
public class DocumentTypeRegistryImpl implements DocumentTypeRegistry {

    private Map documentTypes = new HashMap();

    /**
     * Registers a document type.
     * @param type The document type.
     * @throws RepositoryException if the document type is already registered.
     */
    public void register(DocumentType type) throws RepositoryException {
        if (this.documentTypes.containsKey(type.getName())) {
            throw new RepositoryException("The document type [" + type.getName()
                    + "] is already registered.");
        }
        this.documentTypes.put(type.getName(), type);
    }

    public String[] getDocumentTypeNames() throws RepositoryException {
        Set names = this.documentTypes.keySet();
        return (String[]) names.toArray(new String[names.size()]);
    }

    public DocumentType getDocumentType(String name) throws RepositoryException {
        if (this.documentTypes.containsKey(name)) {
            return (DocumentType) this.documentTypes.get(name);
        } else {
            throw new RepositoryException("The document type [" + name + "] is not registered.");
        }
    }

}
