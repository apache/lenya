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

package org.apache.lenya.lucene.parser;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;

import org.apache.log4j.Logger;

/**
 *
 */
public class SwingHTMLHandler extends ParserCallback {
    private static final Logger log = Logger.getLogger(SwingHTMLHandler.class);

    /** 
     * Creates a new instance of SwingHTMLHandler
     */
    public SwingHTMLHandler() {
        debug("\n\n\n\n\nCreating " + getClass().getName());

        // index everything by default
        startIndexing();
    }

    private TagStack tagStack = new TagStack();

    protected TagStack getStack() {
        return this.tagStack;
    }

    private StringBuffer titleBuffer = new StringBuffer();
    private StringBuffer keywordsBuffer = new StringBuffer();

    /**
     * @param data Data to append to the title
     *
     */
    protected void appendToTitle(char[] data) {
        this.titleBuffer.append(data);
    }

    /**
     * Get title
     *
     * @return The title of the HTML document
     */
    public String getTitle() {
        debug("\n\nTitle: " + this.titleBuffer.toString());

        return this.titleBuffer.toString();
    }

    /**
     * Get keywords
     *
     * @return The keywords of the HTML document
     */
    public String getKeywords() {
        log.debug("Keywords: " + this.keywordsBuffer.toString());

        return this.keywordsBuffer.toString();
    }

    private StringBuffer contentsBuffer = new StringBuffer();

    protected void appendToContents(char[] data) {
        this.contentsBuffer.append(data);
    }

    /**
     * Obtain the reader
     *
     * @return The reader
     */
    public Reader getReader() {
        debug("\nContents: " + this.contentsBuffer.toString());

        return new StringReader(this.contentsBuffer.toString());
    }

    private boolean indexing;

    protected boolean isIndexing() {
        return this.indexing;
    }

    protected void startIndexing() {
        this.indexing = true;
    }

    protected void stopIndexing() {
        this.indexing = false;
    }

    //-------------------------------------------------------------------------
    // Tag handling
    //-------------------------------------------------------------------------

    /**
     * Handles a start tag.
     * @param tag The start tag
     * @param attributes The attributes
     * @param pos The position
     */
    public void handleStartTag(Tag tag, MutableAttributeSet attributes, int pos) {
        getStack().push(tag);

        // append whitespace
        if (!this.contentsBuffer.toString().endsWith(" ")) {
            this.contentsBuffer.append(" ");
        }

        if (tag.equals(HTML.Tag.META)) {
            handleMetaTag(attributes);
        }

        if (tag.equals(HTML.Tag.TITLE)) {
            handleTitleStartTag();
        }

        if (isTagInitialized() && tag.equals(getLuceneTag())) {
            handleLuceneStartTag(tag, attributes);
        }
    }

    /**
     * Handles an end tag.
     * @param tag The end tag
     * @param pos The position
     */
    public void handleEndTag(Tag tag, int pos) {
        // append whitespace
        if (!this.contentsBuffer.toString().endsWith(" ")) {
            this.contentsBuffer.append(" ");
        }

        if (isTagInitialized() && tag.equals(getLuceneTag())) {
            handleLuceneEndTag();
        }

        if (tag.equals(HTML.Tag.TITLE)) {
            handleTitleEndTag();
        }

        try {
            getStack().pop();
        } catch (TagStack.UnderflowException e) {
            log(e);
        }
    }

    //-------------------------------------------------------------------------
    // Title
    //-------------------------------------------------------------------------
    private boolean titleParsing;

    protected boolean isTitleParsing() {
        return this.titleParsing;
    }

    protected void startTitleParsing() {
        this.titleParsing = true;
    }

    protected void stopTitleParsing() {
        this.titleParsing = false;
    }

    protected void handleTitleStartTag() {
        startTitleParsing();
    }

    protected void handleTitleEndTag() {
        stopTitleParsing();
    }

    //-------------------------------------------------------------------------
    // Lucene metag tags
    //-------------------------------------------------------------------------
    /**
     * <code>LUCENE_TAG_NAME</code> The Lucene tag name
     */
    public static final String LUCENE_TAG_NAME = "lucene-tag-name";
    /**
     * <code>LUCENE_CLASS_VALUE</code> The Lucene class value
     */
    public static final String LUCENE_CLASS_VALUE = "lucene-class-value";
    private HTML.Tag luceneTag = null;

    /**
     * Sets the tag name used to avoid indexing.
     * @param tag The tag
     */
    protected void setLuceneTag(HTML.Tag tag) {
        debug("Lucene tag:         " + tag);
        this.luceneTag = tag;
    }

    /**
     * Returns the tag name used to avoid indexing.
     * @return Tag name used to avoid indexing
     */
    protected HTML.Tag getLuceneTag() {
        return this.luceneTag;
    }

    private String luceneClassValue = null;

    /**
     * Sets the value for the <code>class</code> attribute used to avoid indexing.
     * @param value
     */
    protected void setLuceneClassValue(String value) {
        debug("Lucene class value: " + value);
        this.luceneClassValue = value;
    }

    /**
     * Returns the value for the <code>class</code> attribute used to avoid indexing.
     * @return The value
     */
    protected String getLuceneClassValue() {
        return this.luceneClassValue;
    }

    /**
     * Returns if the Lucene META tags are provided.
     * @return Whether the Lucene Meta tags are provided
     */
    protected boolean isTagInitialized() {
        return (getLuceneTag() != null) && (getLuceneClassValue() != null);
    }

    /**
     * Handles a META tag. This method checks for the Lucene configuration tags.
     * @param attributes The attributes
     */
    protected void handleMetaTag(MutableAttributeSet attributes) {
        Object nameObject = attributes.getAttribute(HTML.Attribute.NAME);
        Object valueObject = attributes.getAttribute(HTML.Attribute.VALUE);

        if ((nameObject != null) && (valueObject != null)) {
            String name = (String) nameObject;
            log.debug("Meta tag found: name = " + name);

            if (name.equals(LUCENE_TAG_NAME)) {
                String tagName = (String) valueObject;
                HTML.Tag tag = HTML.getTag(tagName.toLowerCase(Locale.ENGLISH));
                setLuceneTag(tag);
            }

            if (name.equals(LUCENE_CLASS_VALUE)) {
                setLuceneClassValue((String) valueObject);
            }
        }

        Object contentObject = attributes.getAttribute(HTML.Attribute.CONTENT);
        if ((nameObject != null) && (contentObject != null)) {
            String name = (String) nameObject;
            log.debug("Meta tag found: name = " + name);
            if (name.equals("keywords")) {
                log.debug("Keywords found ...");
                this.keywordsBuffer = new StringBuffer((String) contentObject);
            }
        }

        // do not index everything if tags are provided
        if (isTagInitialized()) {
            stopIndexing();
        }
    }

    //-------------------------------------------------------------------------
    // Lucene index control tags
    //-------------------------------------------------------------------------
    private TagStack luceneStack = new TagStack();

    protected TagStack getLuceneStack() {
        return this.luceneStack;
    }

    /**
     * Handles a Lucene index control start tag.
     * @param tag The start tag
     * @param attributes The attributes
     */
    protected void handleLuceneStartTag(HTML.Tag tag, MutableAttributeSet attributes) {
        Object valueObject = attributes.getAttribute(HTML.Attribute.CLASS);

        if (valueObject != null) {
            String value = (String) valueObject;

            if (value.equals(getLuceneClassValue())) {
                getLuceneStack().push(tag);
                debug("");
                debug("---------- Starting indexing ----------");
                startIndexing();
            }
        }
    }

    /**
     * Handles a Lucene index control end tag.
     */
    protected void handleLuceneEndTag() {
        try {
            HTML.Tag stackTag = getStack().top();

            if (!getLuceneStack().isEmpty()) {
                HTML.Tag _luceneTag = getLuceneStack().top();

                if (stackTag == _luceneTag) {
                    debug("");
                    debug("---------- Stopping indexing ----------");
                    getLuceneStack().pop();
                    stopIndexing();
                }
            }
        } catch (TagStack.UnderflowException e) {
            log("Lucene index control tag not closed!", e);
        }
    }

    /**
     * Handles an end tag.
     * @param tag The end tag
     * @param attributes The attributes
     * @param pos The position
     */
    public void handleSimpleTag(Tag tag, MutableAttributeSet attributes, int pos) {
        handleStartTag(tag, attributes, pos);
        handleEndTag(tag, pos);
    }

    /**
     * @see javax.swing.text.html.HTMLEditorKit.ParserCallback#handleText(char[], int)
     */
    public void handleText(char[] data, int pos) {
        if (this.isDebug) {
            System.out.println(".handleText(): data: " + new String(data));
        }

        /*
                if (data[0] == '>') {
                   throw new IllegalStateException();
                   }
        */
        if (isIndexing() || isTitleParsing()) {
            appendToContents(data);
        }

        if (isTitleParsing()) {
            appendToTitle(data);
        }
    }

    //-------------------------------------------------------------------------
    // Logging
    //-------------------------------------------------------------------------
    private boolean isDebug = false;

    /**
     * Logs a message.
     * @param message The debug message
     */
    protected void debug(String message) {
        if (this.isDebug) {
            System.out.println(message);
        }
    }

    /**
     * Logs an exception.
     * @param e The exception
     */
    protected void log(Exception e) {
        log("", e);
    }

    /**
     * Logs an exception with a message.
     * @param message The message
     * @param e The exception
     */
    protected void log(String message, Exception e) {
        System.out.print(getClass().getName() + ": " + message + " ");
        e.printStackTrace(System.out);
    }

    /**
     * Constructor
     */
    public class TagStack {
        private List tags = new ArrayList();

        /**
         * Push tag on the tag stack
         *
         * @param tag The tag
         */
        public void push(HTML.Tag tag) {
            this.tags.add(0, tag);
        }

        /**
         * Pop tag from the tag stack
         *
         * @return Tag
         *
         * @throws UnderflowException if there are no more elements on the stack
         */
        public HTML.Tag pop() throws UnderflowException {
            HTML.Tag tag = top();
            this.tags.remove(tag);

            return tag;
        }

        /**
         * Returns the top tag on the tag stack
         *
         * @return The top element
         *
         * @throws UnderflowException If there are no tags on the tag stack
         */
        public HTML.Tag top() throws UnderflowException {
            HTML.Tag tag = null;

            if (!this.tags.isEmpty()) {
                tag = (HTML.Tag) this.tags.get(0);
            } else {
                throw new UnderflowException();
            }

            return tag;
        }

        /**
         * Checks if the tag stack is empty
         *
         * @return A boolean
         */
        public boolean isEmpty() {
            return this.tags.isEmpty();
        }

        /**
         * Prints out all elements of the tag stack
         */
        public void dump() {
            System.out.print("stack: ");

            for (Iterator i = this.tags.iterator(); i.hasNext();) {
                System.out.print(i.next() + ", ");
            }

            System.out.println("");
        }

        /**
         * The Underflow exception class
         */
        public class UnderflowException extends Exception {
            /**
             * Creates a new UnderflowException object.
             */
            public UnderflowException() {
                super("Stack underflow");
            }
        }
    }
}
