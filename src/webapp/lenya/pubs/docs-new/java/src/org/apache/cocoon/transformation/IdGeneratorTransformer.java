/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.

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

 4. The names "Apache Cocoon" and  "Apache Software Foundation" must  not  be
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
 Stefano Mazzocchi  <stefano@apache.org>. For more  information on the Apache
 Software Foundation, please see <http://www.apache.org/>.

*/
package org.apache.cocoon.transformation;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.excalibur.xml.xpath.XPathProcessor;
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.source.impl.validity.NOPValidity;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.cocoon.xml.XMLUtils;
import org.apache.cocoon.util.HashUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Text;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Stack;

/**
 * A Transformer for adding a URL-encoded 'id' attribute to a node, whose value
 * is determined by the string value of another node.
 *
 * <p>
 * For example, if we were parsing XML like:
 * <pre>
 * &lt;section>
 *   &lt;title>Blah blah</title>
 *   ....
 * &lt;/section>
 * </pre>
 * We could add an 'id' attribute to the 'section' element with a transformer
 * configured as follows:
 * <pre>
 * &lt;map:transformer name="idgen"
 *      src="org.apache.cocoon.transformation.IdGeneratorTransformer">
 *   &lt;element>/document/body//*[local-name() = 'section']&lt;/element>
 *   &lt;id>title/text()&lt;/id>
 * &lt;/map:transformer>
 * </pre>
 * The 'element' parameter is an XPath expression identifying candidates for
 * having an id added.
 * The 'id' parameter is an XPath relative to each found 'element', and
 * specifies a string to use as the id attribute value.  The value will be URL
 * encoded in the id attribute.  If an id with the specified value already
 * exists, the new id will be made unique with XPath's
 * <code>generate-id()</code> function.
 * <p>
 * By default, the added attribute is called <code>id</code>.  This can be
 * altered by specifying an <code>id-attr</code> parameter:
 * <pre>
 *   &lt;id-attr>ID&lt;/id-attr>
 * </pre>
 * If the specified attribute is already present on the node, it will not be
 * rewritten.
 */
public class IdGeneratorTransformer
    extends AbstractDOMTransformer
    implements CacheableProcessingComponent, Configurable, Disposable
{

    /** XPath Processor */
    private XPathProcessor processor = null;

    protected String elementXPath = null;
    protected String idXPath = null;
    protected String idAttr = null;

    public void configure(Configuration configuration) throws ConfigurationException {
        getLogger().info("## || Configuring IdGeneratorTransformer with "+configuration);
        this.elementXPath = configuration.getChild("element").getValue(null);
        this.idXPath = configuration.getChild("id").getValue(null);
        this.idAttr = configuration.getChild("id-attr").getValue("id");
        if (elementXPath == null) {
            throw new ConfigurationException(
                    "## The IdGenerator 'element' parameter must be specified. For example, "+
                    "<element>/document/body//*[local-name() = 'section']</element>");
        }
        if (idXPath == null) {
            throw new ConfigurationException(
                    "## The IdGenerator 'id' parameter must be specified. For example,"+
                    "<id>title/text()</id>");
        }
    }

    public void setup(SourceResolver resolver, Map objectModel,
            String source, Parameters parameters)
        throws ProcessingException, SAXException, IOException
    {
        super.setup(resolver, objectModel, source, parameters);
        /*
         If you prefer dynamic configuration, use this instead of
         configure(), and remember to clear the fields in recycle()

        this.elementXPath = (String)parameters.getParameter("element", null);
        if (this.elementXPath == null) {
            throw new ProcessingException(
                    "The IdGenerator 'element' parameter must be specified. For example, "+
                    "<map:parameter name=\"element\" value=\"/document/body//*[local-name() = 'section']\"/>");
        }
        this.idXPath = (String)parameters.getParameter("id", null);
        if (idXPath == null) {
            throw new ProcessingException(
                    "The IdGenerator 'id' parameter must be specified. For example,"+
                    "<map:parameter name=\"id\" value=\"title/text()\"/>");
        }
        this.idAttr = (String)parameters.getParameter("id-attr", "id");
        */
    }

    public void compose(ComponentManager manager) {
        super.compose(manager);
        try {
            this.processor = (XPathProcessor)this.manager.lookup(XPathProcessor.ROLE);
        } catch (Exception e) {
            getLogger().error("cannot obtain XPathProcessor", e);
        }
    }

    /** Implementation of a template method declared in AbstractDOMTransformer.
     * @param doc DOM of XML received by the transformer
     * @return A pared-down DOM.
     */
    protected Document transform(Document doc) {
        getLogger().debug("## Transforming with element='"+elementXPath+"', id='"+idXPath+"'");
        Document newDoc = null;
        try {
            newDoc = addIds(doc, elementXPath, idXPath);
        } catch (SAXException se) {
            // Really ought to be able to propagate these to caller
            getLogger().error("Error when transforming XML: "+se.getMessage(), se.getException());
            throw new RuntimeException("Error transforming XML. See error log for details: "+se.getMessage()+". Nested exception: "+se.getException().getMessage());
        }
        return newDoc;
    }

    private Document addIds(Document doc, String elementXPath, String idXPath) throws SAXException {
        getLogger().debug("## Using element XPath "+elementXPath);
        NodeList sects = processor.selectNodeList(doc, elementXPath);
        getLogger().debug("## .. got "+sects.getLength()+" sections");
        for (int i=0; i<sects.getLength(); i++) {
            Element sect = (Element)sects.item(i);
            sect.normalize();
            getLogger().debug("## Using id XPath "+idXPath);
            String id = null;
            try {
              id = processor.evaluateAsString(sect, idXPath);
            } catch (Exception e) {
                throw new SAXException("'id' XPath expression '"+idXPath+"' does not return a text node: "+e, e);
            }
            getLogger().info("## Got id "+id);
            if (!sect.hasAttribute(this.idAttr)) {
                String newId = URLEncoder.encode(id);
                newId = avoidConflicts(doc, sect, this.idAttr, newId);

                // Upgrade to DOM 2 support
                //sect.setAttribute(this.idAttr, newId);
                sect.setAttributeNS(sect.getNamespaceURI(), this.idAttr, newId);
            }
        }
        return doc;
    }

    /**
     * Ensure that IDs aren't repeated in the document.  If an element with the
     * specified id is already present, <code>generate-id</code> is used to
     * distinguish the new one.
     */
    private String avoidConflicts(Document doc, Element sect, String idAttr, String newId) {
        // We rely on the URLencoding of newId to avoid ' conflicts here:
        NodeList conflicts = processor.selectNodeList(doc, "//*[@"+idAttr+"='"+newId+"']");
        int numConflicts = conflicts.getLength();
        getLogger().info("## "+numConflicts+" conflicts with "+newId);
        if (numConflicts != 0) {
            newId += "-"+processor.evaluateAsString(sect, "generate-id()");
        }
        return newId;
    }

    // Cache methods

    /**
     * Generate the unique key.
     * This key must be unique inside the space of this component.
     *
     * @return A hash of the element and id parameters, thus uniquely
     * identifying this IdGenerator amongst it's peers.
     */
    public Serializable generateKey() {
        return ""+HashUtil.hash(this.elementXPath+this.idXPath);
    }


    /**
     * Generate the validity object.
     *
     * @return An "always valid" SourceValidity object. This transformer has no
     * inputs other than the incoming SAX events.
     */
    public SourceValidity generateValidity() {
        return new NOPValidity();
    }


    /**
     * Recycle the component.
     */
    public void recycle() {
        super.recycle();
        // Uncomment these if we're dynamically (in the map:transform) configuring
        //this.elementXPath = null;
        //this.idXPath = null;
        // note that we don't turf our processor,
    }

    /**
     * dispose
     */
    public void dispose() {
        super.dispose();
        this.processor = null;
        this.elementXPath = null;
        this.idXPath = null;
        this.idAttr = null;
    }
}
