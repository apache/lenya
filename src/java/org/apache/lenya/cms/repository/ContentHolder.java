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

package org.apache.lenya.cms.repository;

import java.io.InputStream;

import org.apache.lenya.cms.metadata.MetaDataOwner;

/**
 * Super interface for nodes and revisions.
 */
public interface ContentHolder extends MetaDataOwner {

    /**
     * @return The last modification date. The date is measured in milliseconds
     *         since the epoch (00:00:00 GMT, January 1, 1970), and is 0 if it's
     *         unknown.
     * @throws RepositoryException if the node does not exist.
     */
    long getLastModified() throws RepositoryException;

    /**
     * @return The content length.
     * @throws RepositoryException if the node does not exist.
     */
    long getContentLength() throws RepositoryException;

    /**
     * Accessor for the source URI of this node
     * @return the source URI
     */
    String getSourceURI();

    /**
     * @return if the item exists.
     * @throws RepositoryException if an error occurs.
     */
    boolean exists() throws RepositoryException;

    /**
     * @return The input stream.
     * @throws RepositoryException if the node does not exist.
     */
    InputStream getInputStream() throws RepositoryException;

    /**
     * @return The MIME type.
     * @throws RepositoryException if the node does not exist.
     */
    String getMimeType() throws RepositoryException;

}
