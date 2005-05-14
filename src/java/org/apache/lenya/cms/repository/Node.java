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
package org.apache.lenya.cms.repository;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.lenya.transaction.TransactionException;
import org.apache.lenya.transaction.Transactionable;

/**
 * Repository node.
 * 
 * @version $Id$
 */
public interface Node extends Transactionable {

    /**
     * The protocol with which to find Lenya nodes
     */
    String LENYA_PROTOCOL = "lenya://";

    /**
     * The identifiable type.
     */
    String IDENTIFIABLE_TYPE = "node";

    /**
     * @return if the node exists.
     * @throws TransactionException if an error occurs.
     */
    boolean exists() throws TransactionException;

    /**
     * @return The input stream.
     * @throws TransactionException if the node does not exist.
     */
    InputStream getInputStream() throws TransactionException;

    /**
     * @return The output stream.
     * @throws TransactionException if the node does not exist.
     */
    OutputStream getOutputStream() throws TransactionException;
    
    /**
     * @return The last modification date.
     * @throws TransactionException if the node does not exist.
     */
    long getLastModified() throws TransactionException;
    
    /**
     * @return The content length.
     * @throws TransactionException if the node does not exist.
     */
    long getContentLength() throws TransactionException;
    
    /**
     * @return The MIME type.
     * @throws TransactionException if the node does not exist.
     */
    String getMimeType() throws TransactionException;

    /**
     * Accessor for the source URI of this node
     * @return the source URI
     * @throws TransactionException if the node does not exist.
     */
    String getSourceURI();

}
