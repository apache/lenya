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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.parser.ParserDelegator;


/**
 * The HTML handler
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
        this.contents = new StringBuffer();
        this.links = new ArrayList();
        this.published = -1;

        // 1996.07.10 15:08:56 PST
        this.dateFormatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
    }

    /**
     * Return the author
     * @return The author
     */
    public String getAuthor() {
        return this.author;
    }

    /**
     * Return categories (from META tags)
     * @return The categories
     */
    public String getCategories() {
        return this.categories;
    }

    /**
     * Return contents
     * @return The contents
     */
    public String getContents() {
        return this.contents.toString();
    }

    /**
     * Return description (from META tags)
     * @return The description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Return META HREF
     * @return The Meta HREF
     */
    public String getHREF() {
        return this.href;
    }

    /**
     * Return keywords (from META tags)
     * @return The keywords
     */
    public String getKeywords() {
        return this.keywords;
    }

    /**
     * Return links
     * @return The links
     */
    public List getLinks() {
        return this.links;
    }

    /**
     * Return published date (from META tag)
     * @return The published date
     */
    public long getPublished() {
        return this.published;
    }

    /**
     * Return boolean true if links are to be followed
     * @return Whether to follow links
     */
    public boolean getRobotFollow() {
        return this.robotFollow;
    }

    /**
     * Return boolean true if this is to be indexed
     * @return Whether to index
     */
    public boolean getRobotIndex() {
        return this.robotIndex;
    }

    /**
     * Return page title
     * @return The title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Handle Anchor <A HREF="~"></A> tags
     * @param attribs The list of attributes
     */
    public void handleAnchor(MutableAttributeSet attribs) {
        String _href = "";
        _href = (String) attribs.getAttribute(HTML.Attribute.HREF);

        if (_href == null) {
            return;
        }

        this.links.add(_href);
        this.state = HREF;
    }

    /**
     * Handle the Closing tag
     *
     * @param tag The tag
     * @param pos The position
     */
    public void handleEndTag(Tag tag, int pos) {
        if (this.state == NONE) {
            return;
        }

        // In order of precedence == > && > ||
        if ((this.state == TITLE) && tag.equals(HTML.Tag.TITLE)) {
            this.state = NONE;
            return;
        }

        if ((this.state == HREF) && tag.equals(HTML.Tag.A)) {
            //links.add(linktext);
            this.state = NONE;
            return;
        }

        if ((this.state == SCRIPT) && tag.equals(HTML.Tag.SCRIPT)) {
            this.state = NONE;
            return;
        }
    }

    /**
     * Handle META tags
     * @param attribs The set of attributes
     */
    public void handleMeta(MutableAttributeSet attribs) {
        String name = "";
        String content = "";

        name = (String) attribs.getAttribute(HTML.Attribute.NAME);
        content = (String) attribs.getAttribute(HTML.Attribute.CONTENT);

        if ((name == null) || (content == null)) {
            return;
        }

        name = name.toUpperCase(Locale.ENGLISH);

        if (name.equals("DESCRIPTION")) {
            this.description = content;
            return;
        }

        if (name.equals("KEYWORDS")) {
            this.keywords = content;
            return;
        }

        if (name.equals("CATEGORIES")) {
            this.categories = content;
            return;
        }

        if (name.equals("PUBLISHED")) {
            try {
                this.published = this.dateFormatter.parse(content).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return;
        }

        if (name.equals("HREF")) {
            this.href = content;
            return;
        }

        if (name.equals("AUTHOR")) {
            this.author = content;
            return;
        }

        if (name.equals("ROBOTS")) {
            if (content.indexOf("noindex") != -1) {
                this.robotIndex = false;
            }

            if (content.indexOf("nofollow") != -1) {
                this.robotFollow = false;
            }

            this.author = content;
            return;
        }
    }

    /**
     * Handle standalone tag
     *
     * @param tag The tag
     * @param attribs The set of attributes
     * @param pos The position
     */
    public void handleSimpleTag(Tag tag, MutableAttributeSet attribs, int pos) {
        if (tag.equals(HTML.Tag.META)) {
            handleMeta(attribs);
        }
    }

    /**
     * Handle Opening tag
     *
     * @param tag The tag
     * @param attribs The set of attributes
     * @param pos The position
     */
    public void handleStartTag(Tag tag, MutableAttributeSet attribs, int pos) {
        if (tag.equals(HTML.Tag.TITLE)) {
            this.state = TITLE;
        } else if (tag.equals(HTML.Tag.A)) {
            handleAnchor(attribs);
        } else if (tag.equals(HTML.Tag.SCRIPT)) {
            this.state = SCRIPT;
        }
    }

    /**
     * Handle page text
     *
     * @param text The text
     * @param pos The position
     */
    public void handleText(char[] text, int pos) {
        switch (this.state) {
        case NONE:
            this.contents.append(text);
            this.contents.append(space);
            break;

        case TITLE:
            this.title = new String(text);
            break;

        case HREF:
            this.contents.append(text);
            this.contents.append(space);
            //linktext = new String(text);
            break;
        }
    }

    /**
     * Parse Content.
     * @param in The input stream to parse
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
     * Reset private fields holding content
     */
    private void reset() {
        this.title = null;
        this.description = null;
        this.keywords = null;
        this.categories = null;
        this.href = null;
        this.author = null;
        this.contents.setLength(0);
        this.links = new ArrayList();
        this.published = -1;
        this.robotIndex = true;
        this.robotFollow = true;
        this.state = NONE;
    }
}
