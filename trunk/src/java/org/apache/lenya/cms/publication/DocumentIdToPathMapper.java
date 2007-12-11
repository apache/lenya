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

/* $Id$  */

package org.apache.lenya.cms.publication;

import java.io.File;

/**
 * Document Id to Path mapper interface
 */
public interface DocumentIdToPathMapper {

    /**
     * Compute the document-path for a given publication, area and document-uuid. The file separator
     * is the slash (/).
     * 
     * @param uuid the UUID of the document
     * @param language the language of the document
     * 
     * @return the path to the document, without publication ID and area
     */
    String getPath(String uuid, String language);

    /**
     * Compute the document-path for a given publication, area, document-uuid and language
     * 
     * @param publication the publication of the document
     * @param area the area of the document
     * @param uuid the uuid of the document
     * @param language the language of the document
     * 
     * @return the path to the document
     */
    File getFile(Publication publication, String area, String uuid, String language);

}
