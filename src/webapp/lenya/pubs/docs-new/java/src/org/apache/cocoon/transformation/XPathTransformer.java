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

import org.apache.excalibur.xml.dom.DOMParser;
import org.apache.excalibur.xml.xpath.XPathProcessor;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.source.impl.validity.NOPValidity;

import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.cocoon.xml.XMLUtils;
import org.apache.cocoon.util.HashUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.io.*;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.Stack;

/**
 * A transformer that prunes the source tree based on <code>include</code> and
 * <code>exclude</code> XPath expressions.
 * The <code>include</code> parameter specifies nodes to let through, and
 * <code>exclude</code> parameter nodes to filter out. Either or both may be
 * omitted. The default behaviour is for all nodes to be let through
 * (<code>include</code> = "/").
 * <p>
 * This transformer should be declared in the sitemap at
 * <b>map:sitemap/map:components/map:transformers</b>, as follows<br>
 * <pre>
 * &lt;map:transformer logger="sitemap.transformer.xpath" name="xpath" src="org.apache.cocoon.transformation.XPathTransformer" /&gt;<br>
 * </pre>
 * <h3>Example usage</h3>
 * As an example, consider a user manual XML file:
 * <pre>
 * &lt;manual&gt;
 *   &lt;s1 title="Introduction"&gt;
 *     &lt;p&gt;This is the introduction&lt;/p&gt;
 *     &lt;p&gt;A second paragraph&lt;/p&gt;
 *   &lt;/s1&gt;
 *   &lt;s1 title="Getting started"&gt;
 *     &lt;p&gt;Getting started&lt;/p&gt;
 *     &lt;fixme&gt;Add some content here&lt;/fixme&gt;
 *     &lt;n:note xmlns:n="urn:notes"&gt;banana 1&lt;/n:note&gt; &lt;note&gt;banana 2&lt;/note&gt;
 *     &lt;p&gt;Yes, we have no bananas&lt;/p&gt;
 *   &lt;/s1&gt;
 * &lt;/manual&gt;
 * </pre>
 * We could now deliver named chapters as follows:
 * <pre>
 *   &lt;map:match pattern="manual/*"&gt;
 *      &lt;map:generate src="manual.xml"/&gt;
 *      &lt;map:transform type="xpath"&gt;
 *        &lt;map:parameter name="include" value="/manual/s1[@title='{1}']"/&gt;
 *      &lt;/map:transform&gt;
 *     &lt;map:serialize type="xml"/&gt;
 *   &lt;/map:match&gt;
 * </pre>
 * So <code>manual/Introduction</code> would return the first chapter.
 *
 * @author <a href="mailto:jefft@apache.org">Jeff Turner</a>
 * @version CVS $Id: XPathTransformer.java,v 1.1 2003/04/02 12:33:22 andreas Exp $
 */
public class XPathTransformer
    extends AbstractDOMTransformer
    implements CacheableProcessingComponent
{

    /** XPath Processor */
    private XPathProcessor processor = null;
    private DOMParser parser = null;

    /** XPath specifying nodes to include. Defaults to the root node */
    protected String include = null;
    /** XPath specifying nodes to exclude. Defaults to "" (no exclusions) */
    protected String exclude = null;

    public void setup(SourceResolver resolver, Map objectModel,
            String source, Parameters parameters)
        throws ProcessingException, SAXException, IOException {
        super.setup(resolver, objectModel, source, parameters);
        this.include = (String)parameters.getParameter("include", "/");
        this.exclude = (String)parameters.getParameter("exclude", null);
        }


    public void compose(ComponentManager manager) {
        super.compose(manager);
        try {
            this.processor = (XPathProcessor)this.manager.lookup(XPathProcessor.ROLE);
        } catch (Exception e) {
            getLogger().error("cannot obtain XPathProcessor", e);
        }
        try {
            this.parser = (DOMParser)this.manager.lookup(DOMParser.ROLE);
        } catch (Exception e) {
            getLogger().error("cannot obtain DOMParser", e);
        }
    }


    /** Implementation of a template method declared in AbstractDOMTransformer.
     * @param doc DOM of XML received by the transformer
     * @return A pared-down DOM.
     */
    protected Document transform(Document doc) {
        getLogger().debug("Transforming with include='"+include+"', exclude='"+exclude+"'");
        Document newDoc = null;
		try {
        	newDoc = handleIncludes(doc, this.include);
        	newDoc = handleExcludes(newDoc, this.exclude);
		} catch (SAXException se) {
			// Really ought to be able to propagate these to caller
			getLogger().error("Error when transforming XML", se);
			throw new RuntimeException("Error transforming XML. See error log for details: "+se);
		}
        return newDoc;
    }

    /**
     * Construct a new DOM containing nodes matched by <code>include</code> XPath expression.
     * @param doc Original DOM
     * @param xpath XPath include expression
     * @return DOM containing nodes from <code>doc</code> matched by <code>XPath</code>
     */
    private Document handleIncludes(Document doc, String xpath) throws SAXException {
        if (xpath == null || xpath.equals("/")) {
            return doc;
        }
        Document newDoc = parser.createDocument();
        NodeList nodes = processor.selectNodeList(doc, xpath);
        for (int i=0; i<nodes.getLength(); i++) {
            Node node = (Node)nodes.item(i);
            addNode(newDoc, node);
        }
        return newDoc;
    }

    /**
     * Construct a new DOM excluding nodes matched by <code>exclude</code> XPath expression.
     * @param doc Original DOM
     * @param xpath XPath exclude expression
     * @return DOM containing nodes from <code>doc</code>, excluding those
     * matched by <code>XPath</code>
     */
    private Document handleExcludes(Document doc, String xpath) {
        if (xpath == null || xpath.trim().equals("")) {
            return doc;
        }
        NodeList nodes = processor.selectNodeList(doc, xpath);
        for (int i=0; i<nodes.getLength(); i++) {
            Node node = (Node)nodes.item(i);
            // Detach this node. Attr nodes need to be handled specially
            if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
                Attr attrNode = (Attr)node;
                Element parent = attrNode.getOwnerElement();
                parent.removeAttributeNode(attrNode);
            } else {
                Node parent = node.getParentNode();
                if (parent != null) {
                    parent.removeChild(node);
                }
            }
        }
        return doc;
    }

    /**
     * Add a node to the Document, including all of the node's ancestor nodes.
     * Eg, if <code>node</code> is the title="Introduction" node in the
     * example, the doc would have node /manual/s1/@title='Introduction' added
     * to it.
     *
     * @fixme This method could do with some optimization. Currently, every
     * node addition results in one expensive node equality check per ancestor
     * @param doc Document to add a node to
     * @param nodeTemplate Node from another Document which we wish to
     * replicate in <code>doc</code>. This is used as a template, not actually
     * physically copied.
     */
    private void addNode(Document doc, final Node nodeTemplate) {
        // Get a stack of node's ancestors (inclusive)
        Stack stack = new Stack();
        Node parent = nodeTemplate;
        Document oldDoc = nodeTemplate.getOwnerDocument();
        while (parent != oldDoc) {
            stack.push(parent);
            parent = parent.getParentNode();
        }
        // Example stack: (top) [ /manual, /manual/s1, /manual/s1/@title ] (bottom)

        // Now from the earliest (root) ancestor, add cloned nodes to the
        // doc. We check if a suitable ancestor node doesn't already exist in
        // addNode()
        parent = doc;
        while (!stack.empty()) {
            Node oldNode = (Node)stack.pop();
            Node newNode = null;
            if (!stack.empty()) {
              // Shallow copy o a parent node (in example: /manual, then /manual/s1)
              newNode = doc.importNode(oldNode, false); // Do a shallow copy
              copyNamespaceDeclarations(oldNode, newNode);
              parent = findOrCreateNode(parent, newNode);
            } else {
              // Deep copy of the matched node (in example: /manual/s1/@title)
              newNode = doc.importNode(oldNode, true);
              copyNamespaceDeclarations(oldNode, newNode);
              parent.appendChild(newNode);
            }
        }
    }

    /**
     * Add xmlns namespace declaration attribute to newNode, based on those from oldNode.
     * It seems that a DOM object built from SAX with namespace-prefixes=false
     * doesn't have xmlns attribute declarations by default, so we must
     * manually add them.
     * @param oldNode Original node, with namespace attributes intact
     * @param newNode If an Element, this node will have an <code>xmlns</code>
     * (or <code>xmlns:prefix</code>) attribute added to define the node's namespace.
     */
    private void copyNamespaceDeclarations(final Node oldNode, Node newNode) {
      if (newNode.getNodeType() == Document.ELEMENT_NODE) {
        String prefix = oldNode.getPrefix();
        String nsURI = oldNode.getNamespaceURI();
        Element newElem = (Element)newNode;
        if (nsURI != null) {
          if (prefix == null || prefix.equals("")) {
            if (!newElem.hasAttribute("xmlns")) newElem.setAttribute("xmlns", nsURI);
          } else {
            if (!newElem.hasAttribute("xmlns:"+prefix)) newElem.setAttribute("xmlns:"+prefix, nsURI);
          }
        }
      }
    }
 
    /**
     * Add newNode as a child of parent, first checking if any equivalent node
     * to newNode already exists as a child of parent.
     *
     * @param parent Parent node of found or created node
     * @param newNode The node potentially added to parent, unless parent
     * already has an equivalent node
     * @return the appended node, or the old equivalent node if found.
     */
    private Node findOrCreateNode(Node parent, Node newNode) {
        NodeList otherChildren = parent.getChildNodes();
        for (int i=0; i<otherChildren.getLength(); i++) {
            Node child = (Node)otherChildren.item(i);
            if (nodeEquality(child, newNode)) {
                // Found existing equivalent node
                return child;
            }
        }
        // No existing equivalent node found; add and return newNode
        parent.appendChild(newNode);
        return newNode;
    }

    /**
     * Shallow-test two nodes for equality.
     * To quote from the Xerces DOM3 Node.isEqualNode() javadocs, from where
     * most of the code is filched:
     *
     * [Nodes are equal if] the following string attributes are equal:
     * <code>nodeName</code>, <code>localName</code>,
     * <code>namespaceURI</code>, <code>prefix</code>, <code>nodeValue</code>.
     * This is: they are both <code>null</code>, or they have the same length
     * and are character for character identical.
     */
    private boolean nodeEquality(final Node n1, final Node n2) {
        if (n1.getNodeType() != n2.getNodeType()) {
            return false;
        }
        if (n1.getNodeName() == null) {
            if (n2.getNodeName() != null) {
                return false;
            }
        }
        else if (!n1.getNodeName().equals(n2.getNodeName())) {
            return false;
        }

        if (n1.getLocalName() == null) {
            if (n2.getLocalName() != null) {
                return false;
            }
        }
        else if (!n1.getLocalName().equals(n2.getLocalName())) {
            return false;
        }

        if (n1.getNamespaceURI() == null) {
            if (n2.getNamespaceURI() != null) {
                return false;
            }
        }
        else if (!n1.getNamespaceURI().equals(n2.getNamespaceURI())) {
            return false;
        }

        if (n1.getPrefix() == null) {
            if (n2.getPrefix() != null) {
                return false;
            }
        }
        else if (!n1.getPrefix().equals(n2.getPrefix())) {
            return false;
        }

        if (n1.getNodeValue() == null) {
            if (n2.getNodeValue() != null) {
                return false;
            }
        }
        else if (!n1.getNodeValue().equals(n2.getNodeValue())) {
            return false;
        }
        return true;
    }

    // Unused debugging methods

    private final void printNode(String msg, Node node) {
        getLogger().info(msg+" "+node.getNodeName());
    }

    private final void printDeepNode(String msg, Node node) {
        try {
            getLogger().info(msg+" "+XMLUtils.serializeNodeToXML(node));
        } catch (ProcessingException pe) {
            getLogger().error("Error printing node", pe);
        }
    }

    // Cache methods

    /**
     * Generate the unique key.
     * This key must be unique inside the space of this component.
     *
     * @return A hash of the include and exclude parameters, thus uniquely
     * identifying this XPathTransformer amongst it's peers.
     */
    public Serializable generateKey() {
        return ""+HashUtil.hash(this.include+this.exclude);
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
        this.include = null;
        this.exclude = null;
        // note that we don't turf our parser and processor,
    }

    /**
     * dispose
     */
    public void dispose() {
        super.dispose();
        this.processor = null;
        this.parser = null;
        this.include = null;
        this.exclude = null;
    }
}

