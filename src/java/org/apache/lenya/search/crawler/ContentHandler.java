/*
 * $Id: ContentHandler.java,v 1.4 2003/03/04 19:44:56 gregor Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Wyona includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.lenya.search.crawler;

import java.io.InputStream;

import java.util.List;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.4 $
 */
public interface ContentHandler {
    /**
     * Return author
     *
     * @return DOCUMENT ME!
     */
    public String getAuthor();

    /**
     * Return categories (from META tags)
     *
     * @return DOCUMENT ME!
     */
    public String getCategories();

    /**
     * Return contents
     *
     * @return DOCUMENT ME!
     */
    public String getContents();

    /**
     * Return description (from META tags)
     *
     * @return DOCUMENT ME!
     */
    public String getDescription();

    /**
     * Return META HREF
     *
     * @return DOCUMENT ME!
     */
    public String getHREF();

    /**
     * Return keywords (from META tags)
     *
     * @return DOCUMENT ME!
     */
    public String getKeywords();

    /**
     * Return links
     *
     * @return DOCUMENT ME!
     */
    public List getLinks();

    /**
     * Return published date (from META tag)
     *
     * @return DOCUMENT ME!
     */
    public long getPublished();

    /**
     * Return description (from META tags)
     *
     * @return DOCUMENT ME!
     */
    public boolean getRobotFollow();

    /**
     * Return description (from META tags)
     *
     * @return DOCUMENT ME!
     */
    public boolean getRobotIndex();

    /**
     * Return page title
     *
     * @return DOCUMENT ME!
     */
    public String getTitle();

    /**
     * Parse Content.
     */
    public void parse(InputStream in);
}
