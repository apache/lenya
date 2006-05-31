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
package org.apache.lenya.cms.site.usecases;


/**
 * Usecase to create a document.
 * 
 * @version $Id: CreateDocument.java 379098 2006-02-20 11:35:10Z andreas $
 */
public class CreateOpenDocument extends CreateDocument {

    protected static final String ODT_EXTENSION = "odt";


    protected String getSourceExtension() {
        return ODT_EXTENSION;
    }
}
