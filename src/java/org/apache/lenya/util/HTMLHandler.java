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
package org.lenya.util;

import org.apache.log4j.Category;

import java.util.ArrayList;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.6 $
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
