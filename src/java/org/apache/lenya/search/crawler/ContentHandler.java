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

/* $Id: ContentHandler.java 473841 2006-11-12 00:46:38Z gregor $  */

package org.apache.lenya.search.crawler;

import java.io.InputStream;
import java.util.List;


/**
 * DOCUMENT ME!
 */
public interface ContentHandler {
    /**
     * Return author
     *
     * @return DOCUMENT ME!
     */
    String getAuthor();

    /**
     * Return categories (from META tags)
     *
     * @return DOCUMENT ME!
     */
    String getCategories();

    /**
     * Return contents
     *
     * @return DOCUMENT ME!
     */
    String getContents();

    /**
     * Return description (from META tags)
     *
     * @return DOCUMENT ME!
     */
    String getDescription();

    /**
     * Return META HREF
     *
     * @return DOCUMENT ME!
     */
    String getHREF();

    /**
     * Return keywords (from META tags)
     *
     * @return DOCUMENT ME!
     */
    String getKeywords();

    /**
     * Return links
     *
     * @return DOCUMENT ME!
     */
    List getLinks();

    /**
     * Return published date (from META tag)
     *
     * @return DOCUMENT ME!
     */
    long getPublished();

    /**
     * Return description (from META tags)
     *
     * @return DOCUMENT ME!
     */
    boolean getRobotFollow();

    /**
     * Return description (from META tags)
     *
     * @return DOCUMENT ME!
     */
    boolean getRobotIndex();

    /**
     * Return page title
     *
     * @return DOCUMENT ME!
     */
    String getTitle();

    /**
     * Parse Content.
     * 
     * @param in DOCUMENT ME!
     */
    void parse(InputStream in);
}
