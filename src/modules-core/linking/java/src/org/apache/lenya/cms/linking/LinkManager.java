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
package org.apache.lenya.cms.linking;

import org.apache.lenya.cms.publication.Document;

/**
 * Link manager.
 */
public interface LinkManager {
    
    /**
     * The Avalon service role.
     */
    String ROLE = LinkManager.class.getName();

    /**
     * Returns all links from a document.
     * @param source The document.
     * @return An array of links.
     */
    Link[] getLinksFrom(Document source);

    /**
     * Returns all documents which reference a certain document. This depends on
     * the currently available translations of the target document and the link
     * resolver fallback mode.
     * @param target The target document.
     * @return An array of documents.
     */
    Document[] getReferencingDocuments(Document target);

}
