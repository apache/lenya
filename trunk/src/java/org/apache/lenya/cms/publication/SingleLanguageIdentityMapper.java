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

/**
 * Single Language Identity mapper.
 * Similar to the {@link DefaultDocumentIdToPathMapper}, but doesn't add 
 * a language suffix to the source URIs. This is useful for publications
 * which do not have multiple language version of the same document,
 * such as the "blog" publication.
 * @version $Id$
 */
public class SingleLanguageIdentityMapper extends IdentityDocumentIdToPathMapper {

    /**
     * The parameter <code>language</code> is ignored, since this mapper is used for situations where only one language version of a document exists
     * @see org.apache.lenya.cms.publication.IdentityDocumentIdToPathMapper#getSuffix(java.lang.String)
     */
    protected String getSuffix(String language) {
        return "";
    }

}
