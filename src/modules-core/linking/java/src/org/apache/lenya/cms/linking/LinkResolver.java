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

import java.net.MalformedURLException;

import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentFactory;

/**
 * <p>
 * Resolve a link from a document to another document using it's
 * </p>
 * <ul>
 * <li>publication ID</li>
 * <li>area</li>
 * <li>UUID</li>
 * <li>language</li>
 * <li>revision number</li>
 * </ul>
 * <p>
 * All of these parameters are optional and default to the attributes of the
 * document which contains the link.
 * </p>
 * <p>
 * Syntax (square brackets denote optional parts):
 * </p>
 * <code>lenya-document:&lt;uuid&gt;[,lang=...][,area=...][,rev=...][,pub=...]</code>
 * <p>
 * The fallback mode determines the behaviour if the target language is omitted
 * and the target document doesn't exist in the language of the source document.
 * The default fallback mode is {@link #MODE_DEFAULT_LANGUAGE}.
 * <p>
 */
public interface LinkResolver {

    /**
     * The Avalon role.
     */
    String ROLE = LinkResolver.class.getName();
    
    /**
     * The link URI scheme.
     */
    String SCHEME = "lenya-document";
    
    /**
     * Fail if the target document doesn't exist in the source language.
     */
    int MODE_FAIL = 0;
    
    /**
     * Try to fall back to the default language.
     */
    int MODE_DEFAULT_LANGUAGE = 1;

    /**
     * Sets the fallback mode.
     * @param mode one of {@link #MODE_FAIL} and {@link #MODE_DEFAULT_LANGUAGE}.
     */
    void setFallbackMode(int mode);

    /**
     * @return the fallback mode.
     */
    int getFallbackMode();

    /**
     * Resolve a link.
     * 
     * @param currentDocument The document which contains the link.
     * @param linkUri The link URI.
     * @return A link target.
     * @throws MalformedURLException if the URI is invalid.
     */
    LinkTarget resolve(Document currentDocument, String linkUri) throws MalformedURLException;

    /**
     * Resolve a link. The link URI has to contain the UUID, language, area and publication ID.
     * @param factory The document factory to use.
     * @param linkUri The link URI.
     * @return A link target.
     * @throws MalformedURLException if the URI is invalid.
     */
    LinkTarget resolve(DocumentFactory factory, String linkUri) throws MalformedURLException;

}
