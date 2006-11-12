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
package org.apache.lenya.cms.publication;

import org.apache.lenya.cms.site.SiteStructure;

/**
 * An area.
 */
public interface Area {

    /**
     * @return The name of the area ("authoring", "live", etc.).
     */
    String getName();
    
    /**
     * @return The publication the area belongs to.
     */
    Publication getPublication();
    
    /**
     * @return The site structure of the area.
     */
    SiteStructure getSite();
    
    /**
     * @param uuid The UUID.
     * @param language The language.
     * @return A document.
     * @throws PublicationException if the document is not contained.
     */
    Document getDocument(String uuid, String language) throws PublicationException;
    
    /**
     * Checks if a document is contained.
     * @param uuid The UUID.
     * @param language The language.
     * @return A boolean value.
     */
    boolean contains(String uuid, String language);

    /**
     * @return All documents in this area.
     */
    Document[] getDocuments();
    
}
