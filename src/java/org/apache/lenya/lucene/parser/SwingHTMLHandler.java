/*
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.lucene.parser;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;

import org.apache.log4j.Category;

/**
 * @author Andreas Hartmann
 * @author Michael Wechner
 * @version $Id: SwingHTMLHandler.java,v 1.10 2004/02/02 02:50:38 stefano Exp $
 */
public class SwingHTMLHandler extends ParserCallback {
    Category log = Category.getInstance(SwingHTMLHandler.class);

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
        return tagStack;
    }

    private StringBuffer titleBuffer = new StringBuffer();
    private StringBuffer keywordsBuffer = new StringBuffer();

    /**
     *
     */
    protected void appendToTitle(char[] data) {
        titleBuffer.append(data);
    }

    /**
     * Get title
     *
     * @return DOCUMENT ME!
     */
    public String getTitle() {
        debug("\n\nTitle: " + titleBuffer.toString());

        return titleBuffer.toString();
    }

    /**
     * Get keywords
     *
     * @return DOCUMENT ME!
     */
    public String getKeywords() {
        log.debug("Keywords: " + keywordsBuffer.toString());

        return keywordsBuffer.toString();
    }

    private StringBuffer contentsBuffer = new StringBuffer();

    protected void appendToContents(char[] data) {
        contentsBuffer.append(data);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Reader getReader() {
        debug("\nContents: " + contentsBuffer.toString());

        return new StringReader(contentsBuffer.toString());
    }

    private boolean indexing;

    protected boolean isIndexing() {
        return indexing;
    }

    protected void startIndexing() {
        indexing = true;
    }

    protected void stopIndexing() {
        indexing = false;
    }

    //-------------------------------------------------------------------------
    // Tag handling
    //-------------------------------------------------------------------------

    /**
     * Handles a start tag.
     */
    public void handleStartTag(Tag tag, MutableAttributeSet attributes, int pos) {
        getStack().push(tag);

        // append whitespace
        if (!contentsBuffer.toString().endsWith(" ")) {
            contentsBuffer.append(" ");
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
     */
    public void handleEndTag(Tag tag, int pos) {
        // append whitespace
        if (!contentsBuffer.toString().endsWith(" ")) {
            contentsBuffer.append(" ");
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
        return titleParsing;
    }

    protected void startTitleParsing() {
        titleParsing = true;
    }

    protected void stopTitleParsing() {
        titleParsing = false;
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
    public static final String LUCENE_TAG_NAME = "lucene-tag-name";
    public static final String LUCENE_CLASS_VALUE = "lucene-class-value";
    private HTML.Tag luceneTag = null;

    /**
     * Sets the tag name used to avoid indexing.
     */
    protected void setLuceneTag(HTML.Tag tag) {
        debug("Lucene tag:         " + tag);
        luceneTag = tag;
    }

    /**
     * Returns the tag name used to avoid indexing.
     */
    protected HTML.Tag getLuceneTag() {
        return luceneTag;
    }

    private String luceneClassValue = null;

    /**
     * Sets the value for the <code>class</code> attribute used to avoid indexing.
     */
    protected void setLuceneClassValue(String value) {
        debug("Lucene class value: " + value);
        luceneClassValue = value;
    }

    /**
     * Returns the value for the <code>class</code> attribute used to avoid indexing.
     */
    protected String getLuceneClassValue() {
        return luceneClassValue;
    }

    /**
     * Returns if the Lucene META tags are provided.
     */
    protected boolean isTagInitialized() {
        return (getLuceneTag() != null) && (getLuceneClassValue() != null);
    }

    /**
     * Handles a META tag. This method checks for the Lucene configuration tags.
     */
    protected void handleMetaTag(MutableAttributeSet attributes) {
        Object nameObject = attributes.getAttribute(HTML.Attribute.NAME);
        Object valueObject = attributes.getAttribute(HTML.Attribute.VALUE);

        if ((nameObject != null) && (valueObject != null)) {
            String name = (String) nameObject;
            log.debug("Meta tag found: name = " + name);

            if (name.equals(LUCENE_TAG_NAME)) {
                String tagName = (String) valueObject;
                HTML.Tag tag = HTML.getTag(tagName.toLowerCase());
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
                keywordsBuffer = new StringBuffer((String) contentObject);
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
        return luceneStack;
    }

    /**
     * Handles a Lucene index control start tag.
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
                HTML.Tag luceneTag = getLuceneStack().top();

                if (stackTag == luceneTag) {
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
     */
    public void handleSimpleTag(Tag tag, MutableAttributeSet attributes, int pos) {
        handleStartTag(tag, attributes, pos);
        handleEndTag(tag, pos);
    }

    //-------------------------------------------------------------------------
    // Text handling
    //-------------------------------------------------------------------------
    public void handleText(char[] data, int pos) {
        //String string = new String(data);
        //System.out.println(indent + string.substring(0, Math.min(20, string.length())) + " ...");
        if (isDebug) {
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
     */
    protected void debug(String message) {
        if (isDebug) {
            System.out.println(message);
        }
    }

    /**
     * Logs an exception.
     */
    protected void log(Exception e) {
        log("", e);
    }

    /**
     * Logs an exception with a message.
     */
    protected void log(String message, Exception e) {
        System.out.print(getClass().getName() + ": " + message + " ");
        e.printStackTrace(System.out);
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision: 1.10 $
     */
    public class TagStack {
        private List tags = new ArrayList();

        /**
         * DOCUMENT ME!
         *
         * @param tag DOCUMENT ME!
         */
        public void push(HTML.Tag tag) {
            tags.add(0, tag);
        }

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         *
         * @throws UnderflowException DOCUMENT ME!
         */
        public HTML.Tag pop() throws UnderflowException {
            HTML.Tag tag = top();
            tags.remove(tag);

            return tag;
        }

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         *
         * @throws UnderflowException DOCUMENT ME!
         */
        public HTML.Tag top() throws UnderflowException {
            HTML.Tag tag = null;

            if (!tags.isEmpty()) {
                tag = (HTML.Tag) tags.get(0);
            } else {
                throw new UnderflowException();
            }

            return tag;
        }

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public boolean isEmpty() {
            return tags.isEmpty();
        }

        /**
         * DOCUMENT ME!
         */
        public void dump() {
            System.out.print("stack: ");

            for (Iterator i = tags.iterator(); i.hasNext();) {
                System.out.print(i.next() + ", ");
            }

            System.out.println("");
        }

        /**
         * DOCUMENT ME!
         *
         * @author $author$
         * @version $Revision: 1.10 $
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
