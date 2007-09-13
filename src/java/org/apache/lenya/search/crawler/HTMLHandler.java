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

/* $Id: HTMLHandler.java 473841 2006-11-12 00:46:38Z gregor $  */

package org.apache.lenya.search.crawler;

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
        String href = (String) attribs.getAttribute(HTML.Attribute.HREF);

        if (href != null) {
            links.add(href);
            state = HREF;
        }
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
        String name = (String) attribs.getAttribute(HTML.Attribute.NAME);
        String content = (String) attribs.getAttribute(HTML.Attribute.CONTENT);

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
