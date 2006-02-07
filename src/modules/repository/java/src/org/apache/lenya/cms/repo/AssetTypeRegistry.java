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
package org.apache.lenya.cms.repo;

/**
 * Document type registry.
 */
public interface AssetTypeRegistry {

    /**
     * @return All available document type names.
     * @throws RepositoryException if an error occurs.
     */
    String[] getDocumentTypeNames() throws RepositoryException;
    
    /**
     * @param name The name of the document type.
     * @return The document type.
     * @throws RepositoryException if no document type exists with this name.
     */
    AssetType getDocumentType(String name) throws RepositoryException;
    
}
