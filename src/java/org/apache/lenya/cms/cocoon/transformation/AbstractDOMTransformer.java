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
package org.apache.lenya.cms.cocoon.transformation;

import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.parameters.Parameters;

import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.transformation.AbstractTransformer;
import org.apache.cocoon.transformation.Transformer;
import org.apache.cocoon.xml.dom.DOMBuilder;
import org.apache.cocoon.xml.dom.DOMStreamer;

import org.w3c.dom.Document;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.io.IOException;

import java.util.Map;


/**
 * An Abstract DOM Transformer, for use when a transformer needs a DOM-based
 * view of the document.
 * Subclass this interface and implement <code>transform(Document doc)</code>.
 * If you need a ComponentManager there is an instance variable
 * <code>manager</code> for use.
 *
 * @author <a href="mailto:rossb@apache.org">Ross Burton</a>
 * @author <a href="mailto:brobertson@mta.ca">Bruce G. Robertson</a>
 * @author <a href="mailto:vgritsenko@apache.org">Vadim Gritsenko</a>
 * @version CVS $Id: AbstractDOMTransformer.java,v 1.3 2003/06/30 11:52:22 andreas Exp $
 */
public abstract class AbstractDOMTransformer extends AbstractTransformer implements Transformer,
    DOMBuilder.Listener, Composable, Disposable, Recyclable {
    /**
     *  The SAX entity resolver
     */
    protected SourceResolver resolver;

    /**
     *  The request object model
     */
    protected Map objectModel;

    /**
     *  The URI requested
     */
    protected String source;

    /**
     *  Parameters in the sitemap
     */
    protected Parameters parameters;

    /**
     * A <code>ComponentManager</code> which is available for use.
     */
    protected ComponentManager manager;

    /**
     * The <code>DOMBuilder</code> used to build DOM tree out of
     *incoming SAX events.
     */
    protected DOMBuilder builder;

    /**
     * Creates a new AbstractDOMTransformer object.
     */
    public AbstractDOMTransformer() {
        super();
        this.builder = new DOMBuilder(this);
    }

    /**
     * Set the component manager.
     */
    public void compose(ComponentManager manager) {
        this.manager = manager;
    }

    /**
     * Set the <code>SourceResolver</code>, objectModel <code>Map</code>,
     * the source and sitemap <code>Parameters</code> used to process the request.
     *
     * If you wish to process the parameters, override this method, call
     * <code>super()</code> and then add your code.
     */
    public void setup(SourceResolver resolver, Map objectModel, String src, Parameters par)
        throws ProcessingException, SAXException, IOException {
        this.resolver = resolver;
        this.objectModel = objectModel;
        this.source = src;
        this.parameters = par;
    }

    /**
     * Recycle the component.
     */
    public void recycle() {
        this.resolver = null;
        this.source = null;
        this.objectModel = null;
        this.parameters = null;
        this.builder.recycle();
    }

    /**
     * dispose
     */
    public void dispose() {
        this.builder = null;
        this.manager = null;
        this.builder = null;
    }

    /**
     * This method is called when the Document is finished.
     * @param doc The DOM Document object representing this SAX stream
     * @see org.apache.cocoon.xml.dom.DOMBuilder.Listener
     */
    public void notify(Document doc) throws SAXException {
        // Call the user's transform method
        Document newdoc = transform(doc);

        // Now we stream the resulting DOM tree down the pipe
        DOMStreamer s = new DOMStreamer(contentHandler, lexicalHandler);
        s.setNormalizeNamespaces(false);
        s.stream(newdoc);
    }

    /**
     * Transform the specified DOM, returning a new DOM to stream down the pipeline.
     * @param doc The DOM Document representing the SAX stream
     * @return A DOM Document to stream down the pipeline
     */
    protected abstract Document transform(Document doc);

    //
    // SAX Methods. Send incoming SAX events to the DOMBuilder.
    //
    public void setDocumentLocator(Locator locator) {
        builder.setDocumentLocator(locator);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws SAXException DOCUMENT ME!
     */
    public void startDocument() throws SAXException {
        builder.startDocument();
    }

    /**
     * DOCUMENT ME!
     *
     * @throws SAXException DOCUMENT ME!
     */
    public void endDocument() throws SAXException {
        builder.endDocument();
    }

    /**
     * DOCUMENT ME!
     *
     * @param prefix DOCUMENT ME!
     * @param uri DOCUMENT ME!
     *
     * @throws SAXException DOCUMENT ME!
     */
    public void startPrefixMapping(String prefix, String uri)
        throws SAXException {
        builder.startPrefixMapping(prefix, uri);
    }

    /**
     * DOCUMENT ME!
     *
     * @param prefix DOCUMENT ME!
     *
     * @throws SAXException DOCUMENT ME!
     */
    public void endPrefixMapping(String prefix) throws SAXException {
        builder.endPrefixMapping(prefix);
    }

    /**
     * DOCUMENT ME!
     *
     * @param uri DOCUMENT ME!
     * @param loc DOCUMENT ME!
     * @param raw DOCUMENT ME!
     * @param a DOCUMENT ME!
     *
     * @throws SAXException DOCUMENT ME!
     */
    public void startElement(String uri, String loc, String raw, Attributes a)
        throws SAXException {
        builder.startElement(uri, loc, raw, a);
    }

    /**
     * DOCUMENT ME!
     *
     * @param uri DOCUMENT ME!
     * @param loc DOCUMENT ME!
     * @param raw DOCUMENT ME!
     *
     * @throws SAXException DOCUMENT ME!
     */
    public void endElement(String uri, String loc, String raw)
        throws SAXException {
        builder.endElement(uri, loc, raw);
    }

    /**
     * DOCUMENT ME!
     *
     * @param c DOCUMENT ME!
     * @param start DOCUMENT ME!
     * @param len DOCUMENT ME!
     *
     * @throws SAXException DOCUMENT ME!
     */
    public void characters(char[] c, int start, int len)
        throws SAXException {
        builder.characters(c, start, len);
    }

    /**
     * DOCUMENT ME!
     *
     * @param c DOCUMENT ME!
     * @param start DOCUMENT ME!
     * @param len DOCUMENT ME!
     *
     * @throws SAXException DOCUMENT ME!
     */
    public void ignorableWhitespace(char[] c, int start, int len)
        throws SAXException {
        builder.ignorableWhitespace(c, start, len);
    }

    /**
     * DOCUMENT ME!
     *
     * @param target DOCUMENT ME!
     * @param data DOCUMENT ME!
     *
     * @throws SAXException DOCUMENT ME!
     */
    public void processingInstruction(String target, String data)
        throws SAXException {
        builder.processingInstruction(target, data);
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     *
     * @throws SAXException DOCUMENT ME!
     */
    public void skippedEntity(String name) throws SAXException {
        builder.skippedEntity(name);
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     * @param publicId DOCUMENT ME!
     * @param systemId DOCUMENT ME!
     *
     * @throws SAXException DOCUMENT ME!
     */
    public void startDTD(String name, String publicId, String systemId)
        throws SAXException {
        builder.startDTD(name, publicId, systemId);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws SAXException DOCUMENT ME!
     */
    public void endDTD() throws SAXException {
        builder.endDTD();
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     *
     * @throws SAXException DOCUMENT ME!
     */
    public void startEntity(String name) throws SAXException {
        builder.startEntity(name);
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     *
     * @throws SAXException DOCUMENT ME!
     */
    public void endEntity(String name) throws SAXException {
        builder.endEntity(name);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws SAXException DOCUMENT ME!
     */
    public void startCDATA() throws SAXException {
        builder.startCDATA();
    }

    /**
     * DOCUMENT ME!
     *
     * @throws SAXException DOCUMENT ME!
     */
    public void endCDATA() throws SAXException {
        builder.endCDATA();
    }

    /**
     * DOCUMENT ME!
     *
     * @param ch DOCUMENT ME!
     * @param start DOCUMENT ME!
     * @param len DOCUMENT ME!
     *
     * @throws SAXException DOCUMENT ME!
     */
    public void comment(char[] ch, int start, int len)
        throws SAXException {
        builder.comment(ch, start, len);
    }
}
