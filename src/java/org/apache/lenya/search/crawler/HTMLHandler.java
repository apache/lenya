/*
 * $Id: HTMLHandler.java,v 1.6 2003/03/06 20:45:52 gregor Exp $
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
package org.lenya.search.crawler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.parser.ParserDelegator;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.6 $
 */
public final class HTMLHandler extends ParserCallback implements ContentHandler {
    private static final char space = ' ';
    private static final char NONE = 0;
    private static final char TITLE = 1;
    private static final char HREF = 2;
    private static final char SCRIPT = 3;
    private static ParserDelegator pd = new ParserDelegator();

    // Content
    private String title;
    private String description;
    private String keywords;
    private String categories;
    private long published;
    private String href;
    private String author;
    private StringBuffer contents;
    private ArrayList links;

    // Robot Instructions
    private boolean robotIndex;
    private boolean robotFollow;
    private char state;
    private SimpleDateFormat dateFormatter;

    /**
     * Constructor - initializes variables
     */
    public HTMLHandler() {
        contents = new StringBuffer();

        links = new ArrayList();

        published = -1;

        // 1996.07.10 15:08:56 PST
        dateFormatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
    }

    /**
     * Parse Content. [24] 320:1
     *
     * @return DOCUMENT ME!
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Return categories (from META tags)
     *
     * @return DOCUMENT ME!
     */
    public String getCategories() {
        return this.categories;
    }

    /**
     * Return contents
     *
     * @return DOCUMENT ME!
     */
    public String getContents() {
        return this.contents.toString();
    }

    /**
     * Return description (from META tags)
     *
     * @return DOCUMENT ME!
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Return META HREF
     *
     * @return DOCUMENT ME!
     */
    public String getHREF() {
        return this.href;
    }

    /**
     * Return keywords (from META tags)
     *
     * @return DOCUMENT ME!
     */
    public String getKeywords() {
        return this.keywords;
    }

    /**
     * Return links
     *
     * @return DOCUMENT ME!
     */
    public List getLinks() {
        return links;
    }

    /**
     * Return published date (from META tag)
     *
     * @return DOCUMENT ME!
     */
    public long getPublished() {
        return this.published;
    }

    /**
     * Return boolean true if links are to be followed
     *
     * @return DOCUMENT ME!
     */
    public boolean getRobotFollow() {
        return this.robotFollow;
    }

    /**
     * Return boolean true if this is to be indexed
     *
     * @return DOCUMENT ME!
     */
    public boolean getRobotIndex() {
        return this.robotIndex;
    }

    /**
     * Return page title
     *
     * @return DOCUMENT ME!
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Handle Anchor <A HREF="~"></A> tags
     *
     * @param attribs DOCUMENT ME!
     */
    public void handleAnchor(MutableAttributeSet attribs) {
        String href = new String();

        href = (String) attribs.getAttribute(HTML.Attribute.HREF);

        if (href == null) {

            return;
        }

        links.add(href);

        state = HREF;
    }

    /**
     * Closing tag
     *
     * @param tag DOCUMENT ME!
     * @param pos DOCUMENT ME!
     */
    public void handleEndTag(Tag tag, int pos) {
        if (state == NONE) {

            return;
        }

        // In order of precedence == > && > ||
        if ((state == TITLE) && tag.equals(HTML.Tag.TITLE)) {
            state = NONE;

            return;
        }

        if ((state == HREF) && tag.equals(HTML.Tag.A)) {
            //links.add(linktext);
            state = NONE;

            return;
        }

        if ((state == SCRIPT) && tag.equals(HTML.Tag.SCRIPT)) {
            state = NONE;

            return;
        }
    }

    /**
     * Handle META tags
     *
     * @param attribs DOCUMENT ME!
     */
    public void handleMeta(MutableAttributeSet attribs) {
        String name = new String();

        String content = new String();

        name = (String) attribs.getAttribute(HTML.Attribute.NAME);

        content = (String) attribs.getAttribute(HTML.Attribute.CONTENT);

        if ((name == null) || (content == null)) {

            return;
        }

        name = name.toUpperCase();

        if (name.equals("DESCRIPTION")) {
            description = content;

            return;
        }

        if (name.equals("KEYWORDS")) {
            keywords = content;

            return;
        }

        if (name.equals("CATEGORIES")) {
            categories = content;

            return;
        }

        if (name.equals("PUBLISHED")) {
            try {
                published = dateFormatter.parse(content).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return;
        }

        if (name.equals("HREF")) {
            href = content;

            return;
        }

        if (name.equals("AUTHOR")) {
            author = content;

            return;
        }

        if (name.equals("ROBOTS")) {
            if (content.indexOf("noindex") != -1) {
                robotIndex = false;
            }

            if (content.indexOf("nofollow") != -1) {
                robotFollow = false;
            }

            author = content;

            return;
        }
    }

    /**
     * Handle standalone tags
     *
     * @param tag DOCUMENT ME!
     * @param attribs DOCUMENT ME!
     * @param pos DOCUMENT ME!
     */
    public void handleSimpleTag(Tag tag, MutableAttributeSet attribs, int pos) {
        if (tag.equals(HTML.Tag.META)) {
            handleMeta(attribs);
        }
    }

    /**
     * Opening tag
     *
     * @param tag DOCUMENT ME!
     * @param attribs DOCUMENT ME!
     * @param pos DOCUMENT ME!
     */
    public void handleStartTag(Tag tag, MutableAttributeSet attribs, int pos) {
        if (tag.equals(HTML.Tag.TITLE)) {
            state = TITLE;
        } else if (tag.equals(HTML.Tag.A)) {
            handleAnchor(attribs);
        } else if (tag.equals(HTML.Tag.SCRIPT)) {
            state = SCRIPT;
        }
    }

    /**
     * Handle page text
     *
     * @param text DOCUMENT ME!
     * @param pos DOCUMENT ME!
     */
    public void handleText(char[] text, int pos) {
        switch (state) {
        case NONE:
            contents.append(text);
            contents.append(space);

            break;

        case TITLE:
            title = new String(text);

            break;

        case HREF:
            contents.append(text);
            contents.append(space);

            //linktext = new String(text);
            break;
        }
    }

    /**
     * Parse Content.
     *
     * @param in DOCUMENT ME!
     */
    public void parse(InputStream in) {
        try {
            reset();

            pd.parse(new BufferedReader(new InputStreamReader(in)), this, true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Return contents
     */
    private void reset() {
        title = null;

        description = null;

        keywords = null;

        categories = null;

        href = null;

        author = null;

        contents.setLength(0);

        links = new ArrayList();

        published = -1;

        // Robot Instructions
        robotIndex = true;

        robotFollow = true;

        state = NONE;
    }
}
