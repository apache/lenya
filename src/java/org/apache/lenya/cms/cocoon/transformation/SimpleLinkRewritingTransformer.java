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

/* $Id: SimpleLinkRewritingTransformer.java,v 1.5 2004/03/01 16:18:20 gregor Exp $  */

package org.apache.lenya.cms.cocoon.transformation;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

/**
 * This is a simple transformer which rewrites &lt;a
 * href="/lenya/unicom/authoring/doctypes/2columns.html"&gt;
 * to &lt;a
 * href="/lenya/unicom/$AREA/doctypes/2columns.html"&gt;.
 * 
 * Ideally this transformer could be replaced by the
 * LinkRewrittingTransformer that Forrest uses if we employ the same
 * scheme for internal links.
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

                        String languageExtension = "";
                        if (matcher.group(2) != null) {
                            languageExtension = matcher.group(2);
                        }

                        String newValue =
                            baseURI
                                + "/"
                                + envelope.getDocument().getArea()
                                + matcher.group(1)
                                + languageExtension 
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
