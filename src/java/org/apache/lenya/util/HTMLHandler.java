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

/* $Id: HTMLHandler.java,v 1.11 2004/03/01 16:18:14 gregor Exp $  */

package org.apache.lenya.util;

import java.util.ArrayList;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;

import org.apache.log4j.Category;


/**
 * DOCUMENT ME!
 */
public class HTMLHandler extends ParserCallback {
    Category log = Category.getInstance(HTMLHandler.class);
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
        img_src_all = new ArrayList();
        img_src = new ArrayList();
        a_href_all = new ArrayList();
        a_href = new ArrayList();
        link_href_all = new ArrayList();
        link_href = new ArrayList();
    }

    /**
     * DOCUMENT ME!
     *
     * @param tag DOCUMENT ME!
     * @param attributes DOCUMENT ME!
     * @param pos DOCUMENT ME!
     */
    public void handleStartTag(Tag tag, MutableAttributeSet attributes, int pos) {
        if (tag.equals(HTML.Tag.A)) {
            String href = (String) attributes.getAttribute(HTML.Attribute.HREF);

            if (href != null) {
                a_href_all.add(href);

                if (!a_href.contains(href)) {
                    a_href.add(href);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param tag DOCUMENT ME!
     * @param attributes DOCUMENT ME!
     * @param pos DOCUMENT ME!
     */
    public void handleSimpleTag(Tag tag, MutableAttributeSet attributes, int pos) {
        if (tag.equals(HTML.Tag.IMG)) {
            String src = (String) attributes.getAttribute(HTML.Attribute.SRC);

            if (src != null) {
                img_src_all.add(src);

                if (!img_src.contains(src)) {
                    img_src.add(src);
                }
            }
        }

        if (tag.equals(HTML.Tag.LINK)) {
            String href = (String) attributes.getAttribute(HTML.Attribute.HREF);

            if (href != null) {
                link_href_all.add(href);

                if (!link_href.contains(href)) {
                    link_href.add(href);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ArrayList getImageSrcs() {
        return img_src;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ArrayList getAllImageSrcs() {
        return img_src_all;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ArrayList getLinkHRefs() {
        return link_href;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ArrayList getAllLinkHRefs() {
        return link_href_all;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ArrayList getAHRefs() {
        return a_href;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ArrayList getAllAHRefs() {
        return a_href_all;
    }
}
