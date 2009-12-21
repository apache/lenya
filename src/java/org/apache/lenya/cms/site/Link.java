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
package org.apache.lenya.cms.site;

import org.apache.lenya.cms.publication.Document;

/**
 * A link in the site structure references a document.
 * A site node contains a link for each translation.
 */
public interface Link {

    /**
     * @return The language of this link.
     */
    String getLanguage();
    
    /**
     * @return The document this link points to.
     */
    Document getDocument();

    /**
     * @return The node this link belongs to.
     */
    SiteNode getNode();

    /**
     * @return The label of this link.
     */
    String getLabel();
    
    /**
     * @param label The new label.
     */
    void setLabel(String label);
    
    /**
     * Removes the link.
     */
    void delete();
    
}
