/*
$Id: SimpleLinkRewritingTransformer.java,v 1.2 2003/10/22 16:39:18 egli Exp $
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
package org.apache.lenya.cms.cocoon.transformation;

import org.apache.avalon.framework.parameters.Parameters;

import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.transformation.AbstractTransformer;
import org.apache.lenya.cms.publication.PageEnvelope;
import org.apache.lenya.cms.publication.PageEnvelopeException;
import org.apache.lenya.cms.publication.PageEnvelopeFactory;
import org.apache.lenya.cms.publication.Publication;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is a simple transformer which rewrites &lt;a
 * href="/lenya/unicom/authoring/doctypes/2columns.html"&gt;
 * to &lt;a
 * href="/lenya/unicom/$AREA/doctypes/2columns.html"&gt;.
 * 
 * Ideally this transformer could be replaced by the
 * LinkRewrittingTransformer that Forrest uses if we employ the same
 * scheme for internal links.
 *
 * @author Christian Egli
 */
public class SimpleLinkRewritingTransformer extends AbstractTransformer {

    private String baseURI;
    private PageEnvelope envelope = null;

    public static final String INTERNAL_LINK_PREFIX = "site:";

    public void setup(
        SourceResolver resolver,
        Map objectModel,
        String source,
        Parameters parameters)
        throws ProcessingException {

        try {
            envelope =
                PageEnvelopeFactory.getInstance().getPageEnvelope(objectModel);
        } catch (PageEnvelopeException e) {
            throw new ProcessingException(e);
        }

        String mountPoint =
            envelope.getContext() + "/" + envelope.getPublication().getId();

        StringBuffer uribuf = new StringBuffer();

        uribuf.append(mountPoint);

        baseURI = uribuf.toString();
    }

    public void startElement(
        String uri,
        String name,
        String qname,
        Attributes attrs)
        throws SAXException {
        AttributesImpl newAttrs = null;

        // FIXME: This pattern is extremely similar to the pattern in 
        // org.apache.lenya.cms.publication.xsp.DocumentReferencesHelper and has the 
        // same problems. See DocumentReferencesHelper#getInternalLinkPattern().
        Pattern pattern =
            Pattern.compile(
                    envelope.getContext()
                    + "/"
                    + envelope.getPublication().getId()
                    + "/"
                    + Publication.AUTHORING_AREA
                    + "(/[-a-zA-Z0-9_/]+?)(_[a-z][a-z])?\\.html");

        if (name.equals("a")) {
            for (int i = 0, size = attrs.getLength(); i < size; i++) {
                String attrName = attrs.getLocalName(i);
                if (attrName.equals("href")) {
                    String value = attrs.getValue(i);

                    Matcher matcher = pattern.matcher(value);
                    if (matcher.find()) {
                        // yes, this is an internal link that we need to rewrite
                        if (newAttrs == null)
                            newAttrs = new AttributesImpl(attrs);

                        String newValue =
                            baseURI
                                + "/"
                                + envelope.getDocument().getArea()
                                + matcher.group(1)
                                + ".html";
                        newAttrs.setValue(i, newValue);
                    }
                }
            }
        }

        if (newAttrs == null)
            super.startElement(uri, name, qname, attrs);
        else
            super.startElement(uri, name, qname, newAttrs);
    }

    public void recycle() {
        this.envelope = null;
        this.baseURI = null;
    }
}
