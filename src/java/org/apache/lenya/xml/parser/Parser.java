/*
 * $Id: Parser.java,v 1.7 2003/04/24 13:53:14 gregor Exp $
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
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.apache.lenya.xml.parser;

import org.w3c.dom.*;

import java.io.*;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner, lenya
 * @version 0.4.16
 */
public interface Parser {
    /**
     * DOCUMENT ME!
     *
     * @param filename DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    Document getDocument(String filename) throws Exception;

    /**
     * DOCUMENT ME!
     *
     * Create a document from a reader.
     * @param is DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    Document getDocument(Reader reader) throws Exception;

    /**
     * DOCUMENT ME!
     *
     * @param is DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    Document getDocument(InputStream is) throws Exception;

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    Document getDocument();

    /**
     * DOCUMENT ME!
     *
     * @param document DOCUMENT ME!
     * @param name DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    Element newElementNode(Document document, String name);

    /**
     * DOCUMENT ME!
     *
     * @param document DOCUMENT ME!
     * @param data DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    Text newTextNode(Document document, String data);

    /**
     * DOCUMENT ME!
     *
     * @param document DOCUMENT ME!
     * @param data DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    Comment newCommentNode(Document document, String data);
}
