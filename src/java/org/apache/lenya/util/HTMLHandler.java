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

/* $Id$  */

package org.apache.lenya.util;

import java.util.ArrayList;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;


/**
 * HTML handler class
 */
public class HTMLHandler extends ParserCallback {
    private ArrayList img_src;
    private ArrayList img_src_all;
    private ArrayList a_href;
    private ArrayList a_href_all;
    private ArrayList link_href;
    private ArrayList link_href_all;

    /**
     * Creates a new HTMLHandler object.
     */
    public HTMLHandler() {
        this.img_src_all = new ArrayList();
        this.img_src = new ArrayList();
        this.a_href_all = new ArrayList();
        this.a_href = new ArrayList();
        this.link_href_all = new ArrayList();
        this.link_href = new ArrayList();
    }

    /**
     * Handle the start tag
     * @param tag The tag
     * @param attributes The set of attributes
     * @param pos The position
     */
    public void handleStartTag(Tag tag, MutableAttributeSet attributes, int pos) {
        if (tag.equals(HTML.Tag.A)) {
            String href = (String) attributes.getAttribute(HTML.Attribute.HREF);

            if (href != null) {
                this.a_href_all.add(href);

                if (!this.a_href.contains(href)) {
                    this.a_href.add(href);
                }
            }
        }
    }

    /**
     * Handle a simple tag
     * @param tag The tag
     * @param attributes The set of attributes
     * @param pos The position
     */
    public void handleSimpleTag(Tag tag, MutableAttributeSet attributes, int pos) {
        if (tag.equals(HTML.Tag.IMG)) {
            String src = (String) attributes.getAttribute(HTML.Attribute.SRC);

            if (src != null) {
                this.img_src_all.add(src);

                if (!this.img_src.contains(src)) {
                    this.img_src.add(src);
                }
            }
        }

        if (tag.equals(HTML.Tag.LINK)) {
            String href = (String) attributes.getAttribute(HTML.Attribute.HREF);

            if (href != null) {
                this.link_href_all.add(href);

                if (!this.link_href.contains(href)) {
                    this.link_href.add(href);
                }
            }
        }
    }

    /**
     * Get the list of src attributes for images
     * @return The list of src attributes
     */
    public ArrayList getImageSrcs() {
        return this.img_src;
    }

    /**
     * Get the list of src attributes for all images
     * @return  The list of src attributes
     */
    public ArrayList getAllImageSrcs() {
        return this.img_src_all;
    }

    /**
     * Get a list of links
     * @return  The list of links
     */
    public ArrayList getLinkHRefs() {
        return this.link_href;
    }

    /**
     * Get a list of all links
     * @return The list of links
     */
    public ArrayList getAllLinkHRefs() {
        return this.link_href_all;
    }

    /**
     * Get a list of a href=
     * @return The list of a href
     */
    public ArrayList getAHRefs() {
        return this.a_href;
    }

    /**
     * Get a list of all a href=
     * @return The list of a href
     */
    public ArrayList getAllAHRefs() {
        return this.a_href_all;
    }
}
