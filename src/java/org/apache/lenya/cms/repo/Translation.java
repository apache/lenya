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
package org.apache.lenya.cms.repo;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.lenya.cms.repo.metadata.MetaData;

/**
 * Document.
 */
public interface Translation {

    /**
     * @return The document's language code.
     * @throws RepositoryException if an error occurs.
     */
    String getLanguage() throws RepositoryException;

    /**
     * @return The input stream to obtain the document's content from.
     * @throws RepositoryException if an error occurs.
     */
    InputStream getInputStream() throws RepositoryException;

    /**
     * <p>
     * Returns an output stream which can be used to write content to the document.
     * </p>
     * <p>
     * If the content node's document type is validating and has a schema attached, the content is
     * validated after it has been written.
     * </p>
     * @return The output stream to write the document's content to.
     * @throws RepositoryException if an error occurs.
     */
    OutputStream getOutputStream() throws RepositoryException;

    /**
     * @return The content length.
     * @throws RepositoryException if an error occurs.
     */
    long getContentLength() throws RepositoryException;

    /**
     * @return The last modification date of the document.
     * @throws RepositoryException if an error occurs.
     */
    long getLastModified() throws RepositoryException;
    
    /**
     * @return The MIME type of the document.
     * @throws RepositoryException if an error occurs.
     */
    String getMimeType() throws RepositoryException;

    /**
     * @return The document's label.
     * @throws RepositoryException if an error occurs.
     */
    String getLabel() throws RepositoryException;

    /**
     * @param label The document's label.
     * @throws RepositoryException if an error occurs.
     */
    void setLabel(String label) throws RepositoryException;

    /**
     * @return The content node this document belongs to.
     * @throws RepositoryException if an error occurs.
     */
    Asset getAsset() throws RepositoryException;

    /**
     * @param elementSet The name of the element set. The element set must be registered in the
     *            repository's meta data registry.
     * @return The meta data.
     * @throws RepositoryException if an error occurs.
     */
    MetaData getMetaData(String elementSet) throws RepositoryException;

}
