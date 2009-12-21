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
 * This interface is basically the reverse of DocumentIdToPathMapper.
 */
public interface PathToDocumentIdMapper {

    /**
     * Compute the document-id for a given file.
     * @param publication the publication where the file is.
     * @param area the area where the file is.
     * @param file the file that is associated with the document
     * @return the document-id of the document associated with the given file.
     * @throws DocumentDoesNotExistException if there is no document associated with this file.
     */
    String getDocumentId(Publication publication, String area, File file)
        throws DocumentDoesNotExistException;
        
    /**
     * Returns the language for a given file
     * @param file the document file
     * @return the language for the given document file or null if the file
     * has no language.
     */
    public String getLanguage(File file);

}
