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
package org.apache.lenya.cms.metadata;

import org.apache.lenya.cms.publication.DocumentException;
import org.apache.lenya.cms.repository.RepositoryException;

/**
 * Owner of meta-data.
 *
 * @version $Id$
 */
public interface MetaDataOwner {

    /**
     * @return A manager for the meta data.
     * @deprecated Use {@link #getMetaData(String)}Êinstead.
     */
    MetaDataManager getMetaDataManager() throws DocumentException;
    
    /**
     * Returns a meta data object.
     * @param namespaceUri The namespace URI.
     * @return A meta data object.
     * @throws RepositoryException if an error occurs.
     */
    MetaData getMetaData(String namespaceUri) throws RepositoryException;
    
    /**
     * Returns the URIs of the meta data currently supported by the owner.
     * @return An array of strings.
     * @throws RepositoryException if an error occurs.
     */
    String[] getMetaDataNamespaceUris() throws RepositoryException;
    
}
