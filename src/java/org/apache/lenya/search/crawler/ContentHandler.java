/*
 * Copyright  1999-2004 The Apache Software Foundation
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

/* $Id$  */

package org.apache.lenya.search.crawler;

import java.io.InputStream;
import java.util.List;


/**
 * Content handler interface
 */
public interface ContentHandler {
    /**
     * Return author
     * @return The author
     */
    String getAuthor();

    /**
     * Return categories (from META tags)
     * @return The categories
     */
    String getCategories();

    /**
     * Return contents
     * @return The contents
     */
    String getContents();

    /**
     * Return description (from META tags)
     * @return The description
     */
    String getDescription();

    /**
     * Return META HREF
     * @return The META HREF
     */
    String getHREF();

    /**
     * Return keywords (from META tags)
     * @return The keywords
     */
    String getKeywords();

    /**
     * Return links
     * @return The links
     */
    List getLinks();

    /**
     * Return published date (from META tag)
     * @return The published date
     */
    long getPublished();

    /**
     * Return Robot follow (from META tags)
     * @return The robot follow
     */
    boolean getRobotFollow();

    /**
     * Return Robot index (from META tags)
     * @return The robot index
     */
    boolean getRobotIndex();

    /**
     * Return page title
     * @return The title
     */
    String getTitle();

    /**
     * Parse Content.
     * @param in The Stream
     */
    void parse(InputStream in);
}
