/*
$Id
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
package org.apache.lenya.util;

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
 * @version $Revision: 1.8 $
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
